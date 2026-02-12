# Package Structure

## Base package
`com.esieeit.projetsi`

## Structure proposee (Clean Architecture simplifiee)
```
com.esieeit.projetsi
├── domain
│   ├── model          # entites metier (User, Project, Task, Comment)
│   ├── enums          # UserRole, TaskStatus
│   └── exception      # exceptions metier (evolution)
├── application
│   ├── service        # orchestration des cas d'usage
│   └── port           # interfaces (repositories/services) si besoin
├── api
│   ├── controller     # REST controllers
│   └── dto            # request/response DTO
└── infrastructure
    ├── persistence    # JPA entities + repositories
    └── config         # configuration (security, etc.)
```

## Role de chaque package
- `domain` : coeur metier (entites, invariants, enums). Aucun framework ici.
- `application` : coordination des cas d'usage, regles applicatives.
- `api` : exposition HTTP (controllers, DTO, mapping).
- `infrastructure` : persistence, configuration, integrations techniques.

## Regles de dependances
- `api` depend de `application` (controller -> service).
- `application` depend de `domain`.
- `domain` ne depend de personne (pas de Spring, pas de JPA).
- `infrastructure` depend de `application` et `domain`.
- Interdit : acces direct `controller -> repository`.
