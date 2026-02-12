# Decisions (mini ADR)

1. Base package `com.esieeit.projetsi` pour rester coherent avec le code existant.
2. Identifiants de toutes les entites en `Long` pour compatibilite JPA future.
3. `TaskStatus` et `UserRole` en enums pour simplifier le modele L2.
4. Dates d'audit en `Instant` pour un horodatage precis et unifie.
5. Mot de passe stocke uniquement via `passwordHash` (jamais en clair).
6. Pas de gestion de membres de projet au MVP (proprietaire unique via `ownerId`).
7. Pas de Many-to-Many au depart (simplicite et JPA future).
8. `AuthSession` modele pour couvrir login/logout sans exposer de details techniques.
9. `Comment` present comme evolution, pas dans le MVP pour rester cadre.
