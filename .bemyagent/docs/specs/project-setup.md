# Spec — Project Setup (Milestone 1)

## Goal
Repository con Android project compilabile, Firebase configurato (template), GitFlow attivo, CI verde.

## Acceptance Criteria

| # | Criterio | Verificabile via |
|---|---|---|
| AC1 | `./gradlew assembleDebug` → BUILD SUCCESSFUL | terminal |
| AC2 | `./gradlew lint` → 0 errori | terminal |
| AC3 | `./gradlew test` → nessun test fallito | terminal |
| AC4 | Firebase BoM in `gradle/libs.versions.toml` | `grep firebase gradle/libs.versions.toml` |
| AC5 | `app/google-services.json` escluso da git | `git check-ignore app/google-services.json` |
| AC6 | `app/google-services.json.template` presente | `ls app/google-services.json.template` |
| AC7 | Hilt, Room, Navigation Compose, WorkManager in `app/build.gradle.kts` | grep |
| AC8 | `.github/workflows/ci.yml` esegue build + lint + test | ls + cat |
| AC9 | `./gradlew detekt` → BUILD SUCCESSFUL | terminal |
| AC10 | `./gradlew ktlintCheck` → BUILD SUCCESSFUL | terminal |
| AC11 | Struttura package `com.fpculcasi.carezze` con `data/`, `domain/`, `ui/`, `widget/` | ls |
| AC12 | `firestore.rules` e `firestore.indexes.json` presenti | ls |
| AC13 | Branch `develop` creato da `main` | `git branch` |

## Note
- `google-services.json` reale: creare manualmente nella Firebase Console (project `carezze-app`)
- CI può richiedere secrets Firebase per girare completamente; la struttura del file è corretta
