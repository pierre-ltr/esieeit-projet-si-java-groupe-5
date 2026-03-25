# Projet SI Java - ESIEE-IT (2025-2026)

## Contexte

Projet SI en Java : construire une API backend propre, structurée, documentée et testée, avec un workflow Git proche entreprise.

## Objectifs

- Mettre en place un dépôt Git propre (`main`/`develop`/`feature/*`)
- Implémenter un MVP (auth + gestion de ressources métier)
- Respecter une architecture claire (controller/service/repository)
- Ajouter des tests unitaires
- Produire une documentation exploitable (`README.md` + `BACKLOG.md`)

## Équipe

- Pierre Leterrier - Lead Dev (PO / Lead Dev / Dev / QA)
- Antoine Claudon - Dev
- Mehdi Mezouar - PO
- Tao Volle - QA
- Halim Cifti - Testeur

## Stack

- Java 21
- Gradle (wrapper)
- Spring Boot 3.3
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- JWT (JJWT)
- MySQL 8 via Docker Compose
- JUnit 5

## Installation

### Prérequis

- Java 17/21
- Git
- Docker Desktop ou Docker Engine démarré

### Cloner

```bash
git clone <URL>
cd <repo>
```

## Lancer

### Démarrage rapide avec Docker Compose

Si `.env` existe déjà, un simple :

```bash
docker compose up -d
```

démarre maintenant la base MySQL et l’API Spring Boot.

Vérification :

```bash
docker compose ps
curl -i http://localhost:8080/
```

Premier lancement si besoin :

```bash
cp .env.example .env
docker compose up -d
```

### Tests

```bash
./gradlew test
```

### Run API Spring Boot en local

```bash
./gradlew bootRun
```

Variables utilisées par l’application :

- `DB_HOST` : hôte MySQL, défaut `localhost`
- `DB_PORT` : port exposé, défaut `3307`
- `DB_NAME` : nom de la base, défaut `project_si_db`
- `DB_USER` : utilisateur applicatif, défaut `project_user`
- `DB_PASSWORD` : mot de passe applicatif, défaut `project_pass`
- `DB_ROOT_PASSWORD` : mot de passe root utilisé par Docker Compose
- `JWT_SECRET` : secret JWT encodé en Base64
- `JWT_EXPIRATION_MS` : durée de vie du token, défaut `3600000`

Exemple de vérification une fois l’API démarrée :

```bash
curl -i http://localhost:8080/
```

Note :

- `docker compose up -d` lance déjà aussi l’API dans un conteneur
- `./gradlew bootRun` reste utile si tu préfères lancer Spring Boot directement depuis ta machine

## Workflow Git

- `main` : stable
- `develop` : intégration
- `feature/*` : 1 user story = 1 branche
- PR obligatoire vers `develop`

## Convention de commits

- `chore(init): bootstrap gradle wrapper and project structure`
- `docs(readme): add setup and workflow instructions`
- `test(app): add initial sanity test`

Format recommandé : `<type>(<scope>): <message>`

Types courants : `feat`, `fix`, `chore`, `docs`, `refactor`, `test`.

## Backlog

Voir `BACKLOG.md`.

## Documentation de modélisation (TP 2.1)

- Modèle métier : `docs/DOMAIN_MODEL.md`
- Structure des packages : `docs/PACKAGE_STRUCTURE.md`
- Décisions d’architecture : `docs/DECISIONS.md`
- Règles métier et validations (TP 2.2) : `docs/DOMAIN_RULES.md`
- API REST Task CRUD (TP 3.1) : `docs/API_TASKS.md`
- Validation et gestion d’erreurs (TP 3.2) : `docs/API_ERRORS.md`

## TP 4.1 - Base de données & JPA

Éléments déjà en place dans le projet :

- `docker-compose.yml` pour MySQL 8
- configuration datasource/JPA dans `src/main/resources/application.yml`
- profils `application-dev.yml` et `application-test.yml`
- entités JPA : `User`, `Project`, `Task`, `Comment`
- enums métier persistées en `STRING`
- relations JPA : `@OneToMany`, `@ManyToOne`, `@ElementCollection`
- test de vérification JPA : `JpaMappingSmokeTest`

Points de vérification attendus :

- lancer Docker avec `docker compose up -d`
- lancer l’app avec `./gradlew bootRun`
- vérifier dans les logs Hibernate la connexion DB et la création/mise à jour du schéma

Note locale :

- le repo est prêt pour le TP 4.1
- la vérification runtime MySQL n’a pas pu être rejouée ici car le daemon Docker local n’était pas démarré

## TP 4.2 - Repositories & données de test

Ce qui a été ajouté :

- repositories Spring Data JPA pour `User`, `Project` et `Task`
- query methods métier pour filtrer par statut, projet, assigné ou titre
- `TaskService` migré de l’ancien stockage en mémoire vers la persistance JPA réelle
- seed de données de développement via `CommandLineRunner`

Repositories créés :

- `UserRepository`
- `ProjectRepository`
- `TaskRepository`

Exemples de query methods ajoutées :

- `UserRepository.findByEmail(...)`
- `ProjectRepository.findFirstByNameIgnoreCase(...)`
- `ProjectRepository.findByStatus(...)`
- `TaskRepository.findByStatus(...)`
- `TaskRepository.findByProjectId(...)`
- `TaskRepository.findByAssigneeId(...)`
- `TaskRepository.findByTitleContainingIgnoreCase(...)`
- `TaskRepository.existsByProjectIdAndTitleIgnoreCase(...)`
- `TaskRepository.countByProjectId(...)`

Stratégie de seed :

- seed Java avec `CommandLineRunner`
- activé uniquement avec le profil `dev`
- le seed ne s’exécute que si la base est vide

Lancement conseillé pour le TP 4.2 :

```bash
cp .env.example .env
docker compose up -d
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

Endpoints de test rapides :

```bash
curl -i http://localhost:8080/api/tasks
curl -i http://localhost:8080/api/tasks/1
curl -i -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Verifier la persistance JPA","description":"Creation d une vraie tache en base"}'
```

Mini synthèse TP 4.2 :

- Repositories créés : `UserRepository`, `ProjectRepository`, `TaskRepository`.
- Query methods ajoutées pour préparer les usages métier immédiats et la future auth JWT.
- `TaskService` a été migré vers JPA et n’utilise plus le repository en mémoire.
- La stratégie de seed choisie est `CommandLineRunner`, limitée au profil `dev`.
- Problèmes rencontrés : compatibilité MySQL 8.4 dans Docker Compose et URL JDBC.
- À améliorer avant la séance 5 : brancher une vraie persistance utilisateur/auth et enrichir les endpoints métier.

## TP 5.1 - Authentification JWT

Ce qui a été ajouté :

- `AuthController` avec `POST /api/auth/register` et `POST /api/auth/login`
- `AuthService` pour l’inscription, la vérification des doublons et l’authentification
- `JwtService` pour générer et valider les tokens
- `SecurityBeansConfig` avec `PasswordEncoder` BCrypt et configuration Spring Security minimale
- méthodes de repository pour retrouver un user par email ou username, en ignorant la casse
- hash BCrypt pour les nouveaux comptes et pour les comptes seed `dev`

Lancement conseillé pour le TP 5.1 :

```bash
cp .env.example .env
docker compose up -d
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

Tests rapides :

```bash
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"lydia","email":"lydia@example.com","password":"MotDePasse123!"}'

curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"lydia","password":"MotDePasse123!"}'

curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"lydia@example.com","password":"MotDePasse123!"}'
```

Critères validés :

- inscription d’un nouvel utilisateur via l’API
- mot de passe stocké en BCrypt
- login accepté avec username ou email
- login refusé avec mauvais mot de passe
- génération d’un JWT signé avec date d’expiration

## TP 5.2 - Sécurisation des endpoints

Ce qui a été ajouté :

- `CustomUserDetailsService` pour charger un utilisateur depuis la base
- `JwtAuthenticationFilter` pour lire le header `Authorization: Bearer ...`
- handlers JSON dédiés pour `401 Unauthorized` et `403 Forbidden`
- configuration CORS locale pour un futur front
- `SecurityTestController` avec routes publiques, authentifiées et admin
- protection réelle de `GET/POST/PUT/DELETE /api/tasks/**` via JWT

Routes de test :

- `GET /api/security-test/public` : public
- `GET /api/security-test/common` : token requis
- `GET /api/security-test/user` : rôle `USER`
- `GET /api/security-test/admin` : rôle `ADMIN`
- `GET /api/tasks` : token requis

Exemple de test rapide :

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  --data-binary @- <<'EOF' | jq -r '.token'
{"login":"devuser","password":"dev-password"}
EOF
)

curl -i http://localhost:8080/api/security-test/public
curl -i http://localhost:8080/api/security-test/common -H "Authorization: Bearer $TOKEN"
curl -i http://localhost:8080/api/tasks -H "Authorization: Bearer $TOKEN"
```

Comportement attendu :

- sans token sur une route protégée : `401`
- avec token valide : `200`
- avec token utilisateur sur `/api/security-test/admin` : `403`
- avec token admin sur `/api/security-test/admin` : `200`

## API Tasks (TP 3.1)

Endpoints disponibles :

- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

Exemple rapide :

```bash
curl -i -X POST http://localhost:8080/api/tasks \
   -H "Content-Type: application/json" \
   -d '{"title":"Initialiser le repo","description":"Créer Gradle + README"}'
```

## Validation & erreurs (TP 3.2)

- validation technique des DTOs avec Bean Validation (`@NotBlank`, `@Size`, `@Pattern`)
- activation de validation via `@Valid` sur `POST`/`PUT`
- gestion globale des erreurs via `@RestControllerAdvice`
- format de réponse d’erreur JSON uniforme (`timestamp`, `status`, `error`, `message`, `path`, `details`)

## Explication du code (fichier par fichier)

### Vue d’ensemble du flux (requête -> réponse)

1. Le client envoie une requête HTTP sur `/api/tasks`.
2. `TaskController` reçoit le JSON et valide le DTO (`@Valid`).
3. `TaskService` applique la logique métier et les règles de workflow.
4. `TaskRepository` (Spring Data JPA) persiste/récupère la donnée en base MySQL.
5. `TaskMapper` convertit l’entité `Task` en `TaskResponse`.
6. En cas d’erreur, `GlobalExceptionHandler` construit une réponse JSON standardisée.

### Démarrage et build

- `build.gradle`
   - Rôle : configuration du build Gradle.
   - Contenu clé : plugins Java/Spring Boot, dépendances web/validation/JPA/tests.
   - Impact : détermine ce qui compile, ce qui s’exécute et ce qui est testé.
- `settings.gradle`
   - Rôle : déclare le nom du projet Gradle.
- `.env.example`
   - Rôle : variables d’environnement d’exemple pour Docker Compose et Spring Boot.
- `docker-compose.yml`
   - Rôle : démarre la base MySQL locale du TP 4.1.
- `src/main/resources/application.yml`
   - Rôle : configuration Spring Boot datasource + Hibernate.
- `src/main/java/com/esieeit/projetsi/ProjectSiApplication.java`
   - Rôle : point d’entrée de l’API Spring Boot (`main`).
   - Usage : démarrage via `./gradlew bootRun`.
- `src/main/java/com/esieeit/projetsi/App.java`
   - Rôle : ancien point d’entrée console du TP initial.
   - Note : n’est pas utilisé par Spring Boot mais conservé pour historique pédagogique.

### API REST (couche HTTP)

- `src/main/java/com/esieeit/projetsi/api/controller/TaskController.java`
   - Rôle : expose les endpoints CRUD REST.
   - Endpoints :
      - `GET /api/tasks`
      - `GET /api/tasks/{id}`
      - `POST /api/tasks`
      - `PUT /api/tasks/{id}`
      - `DELETE /api/tasks/{id}`
   - Responsabilité : orchestration HTTP uniquement (pas de logique métier complexe).
   - Entrées : DTOs JSON (`TaskCreateRequest`, `TaskUpdateRequest`).
   - Sorties : `TaskResponse` + codes HTTP cohérents.

- `src/main/java/com/esieeit/projetsi/api/dto/TaskCreateRequest.java`
   - Rôle : contrat d’entrée pour la création.
   - Champs : `title`, `description`.
   - Validations techniques : `@NotBlank`, `@Size`.

- `src/main/java/com/esieeit/projetsi/api/dto/TaskUpdateRequest.java`
   - Rôle : contrat d’entrée pour la mise à jour partielle.
   - Champs : `title`, `description`, `status`.
   - Validations techniques : `@Size`, `@Pattern` pour le statut.

- `src/main/java/com/esieeit/projetsi/api/dto/TaskResponse.java`
   - Rôle : format de sortie stable envoyé au front.
   - Champs : `id`, `title`, `description`, `status`.
   - Intérêt : évite d’exposer directement les entités domaine.

- `src/main/java/com/esieeit/projetsi/api/mapper/TaskMapper.java`
   - Rôle : mapping explicite entre modèle domaine et DTO de réponse.
   - Méthode clé : `toResponse(Task)`.
   - Intérêt : centralise la transformation et simplifie le controller.

### Gestion d’erreurs API

- `src/main/java/com/esieeit/projetsi/api/error/FieldErrorDetail.java`
   - Rôle : représente une erreur de validation sur un champ précis.
   - Champs : `field`, `message`.

- `src/main/java/com/esieeit/projetsi/api/error/ErrorResponse.java`
   - Rôle : enveloppe JSON uniforme pour toutes les erreurs.
   - Champs : `timestamp`, `status`, `error`, `message`, `path`, `details`.

- `src/main/java/com/esieeit/projetsi/api/error/GlobalExceptionHandler.java`
   - Rôle : centralise le traitement des exceptions (`@RestControllerAdvice`).
   - Mapping principal :
      - validation DTO -> `400 VALIDATION_ERROR`
      - données invalides -> `400 INVALID_DATA`
      - ressource absente -> `404 NOT_FOUND`
      - règle métier violée -> `409 BUSINESS_RULE_VIOLATION`
      - erreur inattendue -> `500 INTERNAL_ERROR`
   - Intérêt : réponses homogènes et exploitables côté front.

### Application (logique d’orchestration)

- `src/main/java/com/esieeit/projetsi/application/service/TaskService.java`
   - Rôle : cœur applicatif des cas d’usage Task.
   - Opérations : `create`, `getAll`, `getById`, `update`, `delete`.
   - Règles métier : transitions de statut autorisées/interdites.
   - Exceptions métier : `ResourceNotFoundException`, `InvalidDataException`, `BusinessRuleException`.
   - Note design : crée ou réutilise un projet par défaut persistant pour l’API Tasks.

### Infrastructure (persistance JPA)

- `src/main/java/com/esieeit/projetsi/infrastructure/repository/TaskRepository.java`
   - Rôle : repository JPA principal pour les tâches.
   - Intérêt : persistance réelle MySQL + query methods métier.

- `src/main/java/com/esieeit/projetsi/infrastructure/repository/ProjectRepository.java`
   - Rôle : repository JPA des projets.
   - Intérêt : retrouver le projet par défaut et filtrer par statut/propriétaire.

- `src/main/java/com/esieeit/projetsi/infrastructure/repository/UserRepository.java`
   - Rôle : repository JPA des utilisateurs.
   - Intérêt : préparer la future authentification par email.

- `src/main/java/com/esieeit/projetsi/infrastructure/seed/DataInitializer.java`
   - Rôle : injecte des données de démonstration en profil `dev`.
   - Intérêt : permet de tester immédiatement l’API avec des données réelles.

### Vérification JPA

- `src/test/java/com/esieeit/projetsi/domain/model/JpaMappingSmokeTest.java`
   - Rôle : vérifie que les entités JPA principales et leurs relations se persistent correctement.
   - Intérêt : sécurise le mapping du TP 4.1 même sans lancer manuellement l’application complète.

### Domaine métier

- `src/main/java/com/esieeit/projetsi/domain/model/User.java`
   - Rôle : entité utilisateur.
   - Invariants : email valide, username valide, au moins un rôle.

- `src/main/java/com/esieeit/projetsi/domain/model/Project.java`
   - Rôle : entité projet possédée par un owner.
   - Invariants : nom obligatoire, owner non null.
   - Méthode métier clé : `addTask(Task)` avec contrôle d’appartenance.

- `src/main/java/com/esieeit/projetsi/domain/model/Task.java`
   - Rôle : entité centrale du CRUD API.
   - Invariants : titre valide, projet non null.
   - Workflow : `TODO -> IN_PROGRESS -> DONE -> ARCHIVED` (+ règles additionnelles).
   - Méthodes métier : `start`, `complete`, `moveBackToTodo`, `archive`.

- `src/main/java/com/esieeit/projetsi/domain/model/Comment.java`
   - Rôle : commentaire lié à une tâche et un auteur.
   - Invariants : contenu non vide, task et author obligatoires.

- `src/main/java/com/esieeit/projetsi/domain/enums/TaskStatus.java`
   - Rôle : états de cycle de vie d’une tâche.

- `src/main/java/com/esieeit/projetsi/domain/enums/TaskPriority.java`
   - Rôle : niveau de priorité métier.

- `src/main/java/com/esieeit/projetsi/domain/enums/UserRole.java`
   - Rôle : rôles d’accès applicatif (`USER`, `ADMIN`).

- `src/main/java/com/esieeit/projetsi/domain/validation/Validators.java`
   - Rôle : utilitaires partagés de validation (null, blank, taille, email, positif).
   - Intérêt : éviter la duplication des validations dans toutes les entités/services.

- `src/main/java/com/esieeit/projetsi/domain/exception/DomainException.java`
   - Rôle : classe racine des exceptions domaine.

- `src/main/java/com/esieeit/projetsi/domain/exception/ValidationException.java`
   - Rôle : violation de validation technique/métier de base.

- `src/main/java/com/esieeit/projetsi/domain/exception/BusinessRuleException.java`
   - Rôle : violation de règle métier (workflow, transitions interdites).

- `src/main/java/com/esieeit/projetsi/domain/exception/InvalidDataException.java`
   - Rôle : données requête cohérentes JSON mais invalides pour l’application.

- `src/main/java/com/esieeit/projetsi/domain/exception/ResourceNotFoundException.java`
   - Rôle : ressource absente demandée par un id.

- `src/main/java/com/esieeit/projetsi/domain/Demo.java`
   - Rôle : scénario de démonstration locale du domaine sans HTTP.

### Tests

- `src/test/java/com/esieeit/projetsi/AppTest.java`
   - But : vérifier la base de projet et la chaîne de test.

- `src/test/java/com/esieeit/projetsi/domain/model/TaskWorkflowTest.java`
   - But : valider transitions autorisées/interdites du workflow `TaskStatus`.

- `src/test/java/com/esieeit/projetsi/domain/model/UserValidationTest.java`
   - But : valider les contraintes de création utilisateur (email/roles).

### Documentation

- `BACKLOG.md`
   - Contenu : user stories, priorité (Must/Should/Nice), estimation, critères.

- `docs/DOMAIN_MODEL.md`
   - Contenu : acteurs, cas d’usage, entités, cardinalités, diagramme Mermaid.

- `docs/PACKAGE_STRUCTURE.md`
   - Contenu : règles de dépendances entre couches.

- `docs/DECISIONS.md`
   - Contenu : décisions techniques/architecture (mini ADR).

- `docs/DOMAIN_RULES.md`
   - Contenu : invariants, validations, transitions et règles métier.

- `docs/API_TASKS.md`
   - Contenu : endpoints CRUD, exemples de requêtes/réponses, codes HTTP.

- `docs/API_ERRORS.md`
   - Contenu : catalogue des erreurs, format standard `ErrorResponse`, exemples JSON.

## Structure actuelle

```text
.
├─ build.gradle
├─ settings.gradle
├─ gradlew
├─ gradlew.bat
├─ gradle/wrapper/
└─ src/
   ├─ main/java/com/esieeit/projetsi/App.java
   └─ test/java/com/esieeit/projetsi/AppTest.java
```
