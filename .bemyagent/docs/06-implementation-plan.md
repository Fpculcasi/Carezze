# Piano di Implementazione — Carezze

> Index ad alto livello. Le tracce di esecuzione vivono in `work/X/X.Y/`.

---

## Milestone 1 — Project Setup & Fondamenta
**Goal**: Repository GitHub funzionante con Android project, Firebase configurato, GitFlow attivo | **Status**: done

| Task | Descrizione | Status |
|---|---|---|
| 1.1 | Init Android project (Kotlin 2.x, Compose BOM, minSdk 26) | done |
| 1.2 | Configurazione Firebase (Auth, Firestore, FCM) + `google-services.json` template | done |
| 1.3 | Setup Hilt, Room, Navigation Compose, WorkManager | done |
| 1.4 | Init GitFlow: branch `main` + `develop`, GitHub Actions CI (build + lint + test) | done |
| 1.5 | Setup Detekt + Ktlint con regole condivise | done |
| 1.6 | Struttura package MVVM: `data/`, `domain/`, `ui/`, `widget/` | done |
| 1.7 | `firestore.rules` skeleton + `firestore.indexes.json` | done |

**Git commit message (proposta):**
```
feat: [M1] project bootstrap — Android + Firebase + CI/CD skeleton

Sets up Kotlin/Compose project, Firebase integration, Hilt DI,
Room, Navigation Compose, GitFlow structure, and GitHub Actions pipeline.
```

| Spec | Status |
|---|---|
| [project-setup](specs/project-setup.md) | todo → specced prima di M1 |

---

## Milestone 1.5 — Firebase App Distribution CI/CD
**Goal**: APK di release firmato distribuito automaticamente ai beta-tester su Firebase App Distribution ad ogni tag `v*.*.*-beta` via GitHub Actions | **Status**: in-progress (1.5.1–1.5.2 richiedono azione manuale umana)

| Task | Descrizione | Status |
|---|---|---|
| 1.5.1 | Firebase Console: abilita App Distribution, crea gruppo tester `beta-testers` | requires-human-action |
| 1.5.2 | Google Cloud: Service Account con ruolo `Firebase App Distribution Admin` → JSON key → GitHub Secrets (`FIREBASE_SERVICE_ACCOUNT`, `FIREBASE_APP_ID`, `GOOGLE_SERVICES_JSON`) | requires-human-action |
| 1.5.3 | Keystore: genera con `keytool`, codifica Base64, configura `build.gradle.kts` signing config da env var, salva secrets GitHub (`KEYSTORE_BASE64`, `KEYSTORE_ALIAS`, `KEYSTORE_STORE_PASSWORD`, `KEYSTORE_KEY_PASSWORD`) — signing config in Gradle: **done** (keytool + secrets: requires-human-action) | partial |
| 1.5.4 | GitHub Actions workflow `firebase-distribute.yml`: trigger su tag `v*.*.*-beta`, build release APK firmato, release notes da `git log` dall'ultimo tag, upload su Firebase App Distribution → gruppo `beta-testers` | done |

**Scelte architetturali:**
- Trigger: tag git `v*.*.*-beta` su qualsiasi branch
- Variante: release APK firmato (R8 abilitato)
- Auth Firebase: Service Account JSON (`FIREBASE_SERVICE_ACCOUNT` secret)
- Tester: gruppo unico `beta-testers` in Firebase Console
- Release notes: estratte automaticamente da `git log --pretty=format:"- %s" <prev-tag>..HEAD`
- Keystore: creato ad-hoc, mai committato — solo in GitHub Secrets come Base64

**Git commit message (proposta):**
```
feat: [M1.5] firebase app distribution — automated beta release on tag

Release APK signed and uploaded to Firebase App Distribution on v*-beta tag.
GitHub Actions handles signing, release notes from git log, and tester notification.
```

---

## Milestone 2 — Auth & Onboarding
**Goal**: Utente può usare l'app in locale (anonimo) o registrarsi, con migrazione dati garantita | **Status**: done

| Task | Descrizione | Status |
|---|---|---|
| 2.1 | Firebase Anonymous Auth + AuthViewModel + flusso locale | done |
| 2.2 | Schermata Welcome (locale vs registrati) | done |
| 2.3 | Registrazione email/password + `linkWithCredential()` migrazione | done |
| 2.4 | Google Sign-In | done |
| 2.5 | Gestione `users/{userId}` documento: creazione e update | done |
| 2.6 | Schermata Settings: lingua, unità temperatura, quiet hours | done |

**Git commit message (proposta):**
```
feat: [M2] auth flow — anonymous, email, Google + data migration

Implements all auth modes with seamless local→account migration.
Settings screen covers language, temperature unit, and quiet hours.
```

---

## Milestone 3 — Gestione Persone
**Goal**: Utente può creare, visualizzare e modificare Profili Persona | **Status**: done

| Task | Descrizione | Status |
|---|---|---|
| 3.1 | Domain: `Person` model + `PersonRepository` interface + Use Cases | done |
| 3.2 | Data: `PersonRepositoryImpl` (Firestore + Room) con snapshot listener | done |
| 3.3 | UI: Lista Persone + schermata Aggiungi/Modifica Persona | done |
| 3.4 | Firestore Security Rules per `persons/{personId}` — regole scritte + deploy completato | done |
| 3.5 | Test unitari: Use Cases Persona (TDD) | done |

**Git commit message (proposta):**
```
feat: [M3] person management — CRUD, real-time sync, security rules

Persons can be created, edited, and observed in real-time via
Firestore snapshot listeners with full offline support.
```

---

## Milestone 4 — Gestione Terapie
**Goal**: Utente può creare terapie con farmaci multipli, schedule automatiche e progresso | **Status**: done

| Task | Descrizione | Status |
|---|---|---|
| 4.1 | Domain: `Therapy`, `Medication`, `TherapyDuration` + Use Cases | done |
| 4.2 | Logica calcolo `scheduledTimes[]` da `frequencyHours` + orario inizio | done |
| 4.3 | Data: `TherapyRepositoryImpl` + `MedicationLogRepositoryImpl` | done |
| 4.4 | UI: Wizard Aggiungi Terapia (multi-step) + Dettaglio Terapia | done |
| 4.5 | Progresso terapia: barra avanzamento + calendario dosi + contatore rimanenti | done |
| 4.6 | Firestore Security Rules per `therapies/` e `medicationLogs/` | done |
| 4.7 | Test unitari: Use Cases Terapia + logica schedule (TDD) | done |

**Git commit message (proposta):**
```
feat: [M4] therapy management — multi-drug schedules, progress tracking

Therapies support multiple medications with auto-calculated schedules.
Progress shown as bar, calendar, and remaining dose counter.
```

---

## Milestone 5 — Activity Logging & Dashboard
**Goal**: Dashboard operativa, Quick Log 1-tap, storico 30 giorni in lista e calendario | **Status**: done

| Task | Descrizione | Status |
|---|---|---|
| 5.1 | Domain: `ActivityLog` sealed class + `ActivityLogRepository` + Use Cases | done |
| 5.2 | Data: `ActivityLogRepositoryImpl` (tutti i tipi: pasto, pannolino, sonno, temperatura, peso, igiene) | done |
| 5.3 | UI: Dashboard — card view per Persona + feed cronologico con toggle | done |
| 5.4 | UI: Quick Log Bottom Sheet (1 tap + dettagli opzionali espandibili) | done |
| 5.5 | UI: Storico Lista (feed 30gg, lazy load per date precedenti) | done |
| 5.6 | UI: Storico Calendario (vista mensile + dettaglio giorno) | done |
| 5.7 | Filtro Dashboard per singola Persona | done |
| 5.8 | Test unitari: Use Cases log attività (TDD) | done |

**Git commit message (proposta):**
```
feat: [M5] activity logging + dashboard — all event types, history views

Quick Log records any event in 1 tap. Dashboard toggles between
card and feed view. History shows 30 days in list or calendar mode.
```

---

## Milestone 6 — Condivisione & Inviti
**Goal**: Utente può condividere Persona o Terapia tramite QR/codice, revocare accesso | **Status**: in-progress

| Task | Descrizione | Status |
|---|---|---|
| 6.1 | Domain: `Invitation` model + `InvitationRepository` + Use Cases | done |
| 6.2 | Firestore transaction: `redeemInvitation` (atomica lato client, single-use + scadenza) | todo |
| 6.3 | Client-side cascade: `onMemberRevoked` (transazione revoca + cancellazione dati membro) | todo |
| 6.4 | Data: `InvitationRepositoryImpl` (generazione codice + QR bitmap) | todo |
| 6.5 | UI: Genera Invito (QR + codice testo, condivisibile via Intent) | todo |
| 6.6 | UI: Riscatta Invito (scanner QR camera + input manuale) | todo |
| 6.7 | UI: Gestione Membri (lista + revoca con dialog conferma) | todo |
| 6.8 | Firestore Security Rules aggiornate per sharing granulare | todo |
| 6.9 | Test: Cloud Functions (emulatore Firebase) | todo |

**Git commit message (proposta):**
```
feat: [M6] secure sharing — QR/code invites, member management, revocation

Single-use 8-char codes with 24h expiry. Cloud Function validates
atomically. Revocation cascades to delete shared member's data.
```

---

## Milestone 7 — Notifiche FCM
**Goal**: Notifiche push per terapie, inattività e conferma familiare | **Status**: next

| Task | Descrizione | Status |
|---|---|---|
| 7.1 | FCM token management: salvataggio + refresh in `users/{id}.fcmTokens` | todo |
| 7.2 | WorkManager: `MedicationReminderWorker` (periodic, dosi imminenti on-device) | todo |
| 7.3 | WorkManager: `InactivityCheckWorker` (periodic 30min, soglia configurabile per tipo) | todo |
| 7.4 | FCM Service Android: routing notifiche → deep link schermata corretta | todo |
| 7.5 | Conferma familiare: tap "Preso" → update `MedicationLog` → dismiss notifica tutti | todo |
| 7.6 | Rispetto quiet hours: Cloud Function legge `quietHoursStart/End` utente | todo |
| 7.7 | Test: logica scheduling (unit) + FCM service (integration) | todo |

**Git commit message (proposta):**
```
feat: [M7] FCM notifications — medication reminders, inactivity alerts, family ack

Push notifications for all members on shared therapies. Family
confirmation dismisses the alert on all devices in real-time.
```

---

## Milestone 8 — Widget Android
**Goal**: 3 widget Glance operativi sulla home screen del telefono | **Status**: next

| Task | Descrizione | Status |
|---|---|---|
| 8.1 | Setup Glance + configurazione AppWidget provider | todo |
| 8.2 | Widget Terapia: countdown prossima dose + bottone "Preso" per farmaco | todo |
| 8.3 | Widget Pannolino: bottoni rapidi (pipì / pupù / entrambi) per Persona | todo |
| 8.4 | Widget Pasto: bottone rapido con selezione tipo (seno/formula/solido) | todo |
| 8.5 | Widget configurazione: scelta Persona/Terapia al pinning | todo |
| 8.6 | Test: Glance widget actions (integration) | todo |

**Git commit message (proposta):**
```
feat: [M8] home screen widgets — therapy countdown, diaper, meal quick-log

Three Glance widgets allow logging without opening the app.
Each widget is configurable per Person or Therapy at pin time.
```

---

## Milestone 9 — Localizzazione & Polish
**Goal**: App completamente localizzata IT/EN, accessibilità base, icon e branding | **Status**: next

| Task | Descrizione | Status |
|---|---|---|
| 9.1 | String resources: tutte le stringhe in `strings.xml` (IT) + `strings-en.xml` | todo |
| 9.2 | Language toggle in Settings (in-app, non solo system locale) | todo |
| 9.3 | Content descriptions per screen reader (accessibilità base) | todo |
| 9.4 | Touch target size ≥ 48dp su tutti i controlli interattivi | todo |
| 9.5 | App icon, splash screen, colori pastello definiti nel tema Compose | todo |
| 9.6 | README.md in inglese (portfolio-grade) | todo |

**Git commit message (proposta):**
```
feat: [M9] localization + polish — IT/EN, accessibility, branding

Full Italian and English support with in-app language switch.
Pastel color theme, app icon, and portfolio-grade README.
```

---

## Milestone 10 — Testing & Release Preparation
**Goal**: Coverage ≥ 80%, CI verde, release candidate su Play Store internal track | **Status**: next

| Task | Descrizione | Status |
|---|---|---|
| 10.1 | Audit coverage con JaCoCo, colmare gap fino a 80% | todo |
| 10.2 | UI test suite Compose (golden path per ogni schermata) | todo |
| 10.3 | Firestore Security Rules test (Firebase Emulator) | todo |
| 10.4 | GitHub Actions: matrix build (debug + release), test report, coverage badge | todo |
| 10.5 | Signing + ProGuard/R8 config per release build | todo |
| 10.6 | Play Store: internal track release + screenshot | todo |

**Git commit message (proposta):**
```
feat: [M10] release prep — 80%+ coverage, CI matrix, Play Store internal track

Full test suite passing. Security rules validated against emulator.
R8 optimized release build published to internal testing.
```

---

## Backlog (non schedulato)

| Idea | Draft |
|---|---|
| Export PDF/CSV per il pediatra | [drafts/export-data.md](drafts/export-data.md) |
| Dark mode | [drafts/dark-mode.md](drafts/dark-mode.md) |
| iOS (Flutter migration o KMP) | [drafts/ios-support.md](drafts/ios-support.md) |
| Onboarding wizard overlay (primo avvio) | [drafts/onboarding-wizard.md](drafts/onboarding-wizard.md) |
| Snooze notifica farmaco | [drafts/notification-snooze.md](drafts/notification-snooze.md) |
| Apple Sign-In (quando iOS attivo) | [drafts/apple-signin.md](drafts/apple-signin.md) |
| Database farmaci predefiniti | [drafts/drug-database.md](drafts/drug-database.md) |
| Ruoli utente granulari (viewer/editor/admin) | [drafts/granular-roles.md](drafts/granular-roles.md) |
| i18n lingue aggiuntive (FR, DE, ES) | [drafts/more-languages.md](drafts/more-languages.md) |
| Post Medium/LinkedIn per milestone | [drafts/content-strategy.md](drafts/content-strategy.md) |
