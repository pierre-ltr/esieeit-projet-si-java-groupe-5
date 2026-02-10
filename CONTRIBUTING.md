# Contributing

Merci de contribuer au projet. Ce guide décrit le flux de travail attendu.

## Prérequis

- JDK 17+
- Gradle (via `./gradlew`)

## Setup local

```bash
./gradlew build
```

## Lancer l'application

```bash
./gradlew run
```

## Tests

```bash
./gradlew test
```

## Structure du projet

- `app/` : application principale
- `utilities/` : utilitaires partagés
- `list/` : implémentations de structures de données

## Règles de contribution

- Créer une branche par feature ou correctif.
- Garder des commits petits et descriptifs.
- Ajouter ou mettre à jour les tests quand c’est pertinent.
- Vérifier que `./gradlew build` passe avant la PR.

## Pull Request

- Décrire clairement le but et le contexte.
- Lier l’US concernée si possible (ex: `US-03`).
- Ajouter captures/logs si besoin.

## Style & Qualité

- Respecter les packages (`com.esieeit.projetsi`).
- Éviter le code mort et les warnings.
- Préférer des noms explicites et cohérents.
