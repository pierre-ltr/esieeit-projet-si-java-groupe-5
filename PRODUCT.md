# Produit - Définition (1 page)

## Pitch produit (30 secondes)
Notre produit permet de gérer des projets et des tâches simplement, avec un suivi clair de l’avancement.
Il s’adresse aux étudiants et petites équipes qui veulent organiser leur travail sans outils complexes.
Le résultat attendu est une API Java sécurisée qui expose les opérations clés (auth, projets, tâches) de manière fiable et cohérente.

## Problème
Les équipes étudiantes ont souvent des outils éparpillés et peu adaptés à un suivi structuré des projets et tâches, ce qui provoque une perte de visibilité et d’efficacité.

## Public cible
Étudiants et petites équipes qui veulent une solution légère pour organiser et suivre leur travail.

## Proposition de valeur
Une API claire, sécurisée et bien structurée, facile à intégrer dans un front ou des outils internes, avec un périmètre fonctionnel ciblé.

## Périmètre MVP
1. Authentification: register, login.
1. Ressource métier 1: CRUD.
1. Ressource métier 2: CRUD, liée à la ressource 1.
1. Recherche/filtre simple.

## Hors périmètre
1. UI complète.
1. Fonctionnalités avancées d’analytics.
1. Gestion complexe des rôles (au-delà de l’auth basique).

## Hypothèses & contraintes (L2)
1. Projet Java (API).
1. Backlog orienté fonctionnel (pas “faire une DB”).
1. MVP réalisable en 9 séances.
1. Auth obligatoire (au minimum login/register).

## Critères de succès
1. Endpoints principaux opérationnels et documentés.
1. Tests unitaires présents sur les services critiques.
1. Build et tests automatisés via Gradle.

## Risques
1. Périmètre trop large pour 9 séances.
1. Dette technique si l’architecture n’est pas cadrée dès le début.

# Étape 2 - Identifier les acteurs (personas)

## 4.1 Acteurs minimum
Vous devez au moins avoir :
- Visiteur : pas connecté.
- Utilisateur : connecté (fonctionnalités principales).
- Admin : modération / gestion.

Cartographie des acteurs :
- Visiteur : découvrir landing, s’inscrire, voir doc API.
- Utilisateur : se connecter, se déconnecter, modifier profil, CRUD projets, CRUD tâches.
- Admin : voir tous les comptes, bloquer utilisateur, supprimer utilisateur, consulter statistiques.

## 4.2 Tableau acteurs -> objectifs -> permissions

| Acteur | Objectifs | Permissions |
| --- | --- | --- |
| Visiteur | Découvrir l’app | Voir landing, s’inscrire, voir doc API |
| Utilisateur | Gérer projets/tâches | CRUD projets, CRUD tâches, modifier profil |
| Admin | Superviser | Voir tous les comptes, bloquer/supprimer |

# Étape 3 - Découper en modules/features

Vous devez regrouper vos stories par module.

## Modules classiques (exemples)
- Authentification & profil
- Gestion des projets
- Gestion des tâches
- Recherche/filtre
- Administration (bonus)
- Qualité/observabilité (logs, erreurs) (bonus)

## Architecture modulaire du projet

| Module | Stories (US) |
| --- | --- |
| Socle / Setup | US-01 Init repo + Gradle + structure |
| Authentification & profil | US-02 Auth: register, US-03 Auth: login, US-07 Auth: logout (bonus), US-08 Profil: modifier profil (bonus) |
| Gestion des projets | US-04 Ressource métier 1: CRUD |
| Gestion des tâches | US-05 Ressource métier 2: CRUD (liée à ressource 1) |
| Recherche/filtre | US-06 Filtre / recherche simple |
| Administration (bonus) | US-09 Admin: lister comptes, bloquer/supprimer |
| Qualité/observabilité (bonus) | US-10 Logs applicatifs, gestion des erreurs |

# Étape 4 - Rédiger 10 à 15 user stories (atelier)

## 6.1 Liste de stories (exemple complet prêt à utiliser)

### Module A - Authentification & Profil

US-01 (Must)  
En tant que Visiteur, je veux créer un compte afin de pouvoir accéder à l’application.

US-02 (Must)  
En tant que Utilisateur, je veux me connecter afin de retrouver mes projets.

US-03 (Should)  
En tant que Utilisateur, je veux me déconnecter afin de sécuriser ma session.

US-04 (Should)  
En tant que Utilisateur, je veux modifier mon profil afin de mettre à jour mes informations.

### Module B - Gestion des projets

US-05 (Must)  
En tant que Utilisateur, je veux créer un projet afin de structurer mon travail.

US-06 (Must)  
En tant que Utilisateur, je veux lister mes projets afin de voir tout ce que je gère.

US-07 (Must)  
En tant que Utilisateur, je veux modifier un projet afin de corriger ou compléter ses infos.

US-08 (Should)  
En tant que Utilisateur, je veux supprimer un projet afin de nettoyer ma liste.

### Module C - Gestion des tâches

US-09 (Must)  
En tant que Utilisateur, je veux ajouter une tâche dans un projet afin de planifier les actions à faire.

US-10 (Must)  
En tant que Utilisateur, je veux changer le statut d’une tâche afin de suivre l’avancement.

US-11 (Should)  
En tant que Utilisateur, je veux modifier une tâche afin de ajuster son contenu.

US-12 (Should)  
En tant que Utilisateur, je veux supprimer une tâche afin de enlever les tâches inutiles.

### Module D - Recherche / Filtre

US-13 (Nice)  
En tant que Utilisateur, je veux filtrer les tâches par statut afin de me concentrer sur les tâches urgentes.

US-14 (Nice)  
En tant que Utilisateur, je veux rechercher une tâche par mot-clé afin de retrouver rapidement une information.

### Module E - Administration (bonus)

US-15 (Nice)  
En tant que Admin, je veux lister tous les utilisateurs afin de surveiller la plateforme.

## MVP (résumé)
Le MVP couvre les US Must suivantes : US-01, US-02, US-05, US-06, US-07, US-09, US-10, ainsi que l’auth obligatoire.

# Étape 4 - Suite : critères d’acceptation & split

## 6.2 Critères d’acceptation (exemples)

US-01 (inscription)
- Given je suis visiteur
- When je fournis email valide + mdp valide
- Then un compte est créé
- And je peux me connecter

US-09 (ajout tâche)
- Given je suis connecté
- When je crée une tâche dans un projet existant
- Then la tâche apparaît dans la liste
- And elle est associée au bon projet

## 6.3 Découpage “story trop grosse” (split)

Une story doit tenir dans une séance ou moins. Si elle est estimée L/XL ou perçue complexe, il faut la découper.

Exemple de story trop grosse :
- “En tant qu’utilisateur, je veux gérer toutes mes tâches…”

Découpage recommandé :
- Créer tâche
- Modifier tâche
- Supprimer tâche
- Changer statut

# Étape 5 - Prioriser (MoSCoW)

## Définition : Méthode MoSCoW
La méthode MoSCoW est une technique de priorisation des fonctionnalités utilisée dans les projets Agile.
Elle permet de classer les user stories en 4 catégories selon leur importance.

Pourquoi prioriser ?
- Le temps est limité (9 séances de 3h)
- On doit livrer un MVP fonctionnel
- Toutes les features ne sont pas égales en valeur
- Mieux vaut un MVP complet qu'un projet à moitié fini

Pyramide de priorisation MoSCoW :
- Nice to have : bonus si temps (US-13, US-14, US-15)
- Should have : important mais pas bloquant (US-03, US-04, US-08, US-11, US-12)
- Must have - MVP : sans ça, ça ne marche pas (US-01, US-02, US-05, US-06, US-07, US-09, US-10)
- Valeur faible → valeur moyenne → valeur critique

L'acronyme MoSCoW :
- M ust have (Doit avoir)
- o
- S hould have (Devrait avoir)
- C ould have (Pourrait avoir)
- o
- W on't have this time (N'aura pas cette fois-ci)

Pour simplifier en L2, on utilise 3 catégories.

## 7.1 Must / Should / Nice (définition)

Must (Obligatoire - MVP)
- Sans cette story, le produit ne fonctionne pas
- C'est le cœur du projet
- À développer en priorité absolue (sprints 1-2)
- Exemples : Register, Login, CRUD principal

Should (Important - Itération 2)
- Très utile mais pas bloquant
- Améliore significativement l'expérience utilisateur
- À faire après le MVP
- Exemples : Modifier profil, Filtrer par statut, Pagination

Nice (Bonus - Si temps restant)
- "Cerise sur le gâteau"
- N'apporte pas de valeur critique
- Uniquement si tout le reste est terminé
- Exemples : Dashboard avec graphiques, Notifications email, Mode sombre

Rappel :
- Must Have : indispensable au MVP (sinon le projet ne marche pas)
- Should Have : important mais le MVP peut vivre sans
- Nice to Have : bonus, si on a le temps

## 7.2 Grille de priorisation rapide

Posez 2 questions :
- Sans ça, le produit marche ?
- Est-ce qu’un utilisateur gagnerait vraiment quelque chose ?

Décision :
- Si réponse 1 = non → Must
- Si réponse 1 = oui mais utile → Should
- Sinon → Nice

# Étape 6 - Estimer (simple)

## 8.1 T-shirt sizing (S/M/L)
- S : facile (1-2h)
- M : moyen (3-5h)
- L : gros (plus d’une séance) → à découper

Si une story est L, vous devez la découper.

## 8.2 Définition “Done” (DoD)
Pour qu’une story soit “Done” :
- code OK
- tests OK
- README/backlog mis à jour si besoin
- PR faite vers develop

# Étape 7 - Implémentation dans le repo : BACKLOG.md + Issues

## 9.1 Créer BACKLOG.md
À la racine :

```bash
touch BACKLOG.md
```

Remplir avec le template annexe A (déjà présent dans ce repo).

## 9.2 Option GitHub/GitLab Issues/Projects
Si vous utilisez la plateforme :
- Créer une Issue par story
- Ajouter labels : must, should, nice
- Ajouter labels : module/auth, module/project, module/task
- Mettre dans un board (To do / Doing / Done)

C’est exactement ce qu’on fait en entreprise.
