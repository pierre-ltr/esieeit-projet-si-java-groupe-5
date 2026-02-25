# Package Structure

## Base package
`com.esieeit.projetsi`

## Structure proposee (architecture en couches simplifiee)
```
com.esieeit.projetsi
├── api
│   ├── controller     # REST controllers
│   └── dto            # request/response DTO
├── domain
│   ├── model          # entites metier (User, Project, Task, Comment)
│   └── enums          # UserRole, TaskStatus
├── service            # orchestration des cas d'usage (metier applicatif)
├── repository         # interfaces d'acces aux donnees (et/ou adaptateurs au debut)
└── exception          # exceptions applicatives/metier
```

## Role de chaque package
- `api` : exposition HTTP (controllers, DTO, mapping).
- `domain` : coeur metier (entites, invariants, enums). Aucun framework ici.
- `service` : coordination des cas d'usage et regles applicatives.
- `repository` : acces aux donnees (abstraction ou implementation simple selon la seance).
- `exception` : exceptions fonctionnelles/techniques partagees.

## Regles de dependances
- `api` depend de `service` et `dto`.
- `service` depend de `domain` et `repository`.
- `repository` peut dependre de `domain`.
- `domain` ne depend de personne (pas de Spring, pas de JPA).
- Interdit : acces direct `controller -> repository`.

## Evolution possible (seances suivantes)
Si le projet grossit (Spring/JPA), `service` et `repository` pourront etre remappes vers une structure plus "clean" :
- `application/service`
- `application/port`
- `infrastructure/persistence`
- `infrastructure/config`
