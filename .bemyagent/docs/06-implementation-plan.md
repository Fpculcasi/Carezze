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

## Milestone 4.5 — Gestione Terapie: Edit / Termina / Elimina + Log Somministrazioni
**Goal**: Utente può modificare tutti i campi di una terapia, terminarla (soft-stop con storico conservato), eliminarla (hard delete con cascade), e visualizzare/aggiungere manualmente dosi al di fuori del Quick Log | **Status**: done

| Task | Descrizione | Status |
|---|---|---|
| 4.5.1 | Domain: `TherapyStatus` enum (ACTIVE, COMPLETED) + Use Cases `updateTherapy`, `terminateTherapy`, `deleteTherapy`, `addManualMedicationLog` | done |
| 4.5.2 | Data: `TherapyRepositoryImpl` — update (tutti i campi) + terminate (endDate=today, status=COMPLETED) + delete (hard, cascade `MedicationLog`) | done |
| 4.5.3 | UI: `EditTherapyScreen` — wizard pre-popolato, tutti i campi modificabili | done |
| 4.5.4 | UI: `TherapyDetailScreen` — FAB "Modifica" + menu overflow con "Termina" (dialog soft) e "Elimina" (dialog hard con warning perdita dati) | done |
| 4.5.5 | UI: `TherapyLogScreen` — lista cronologica dosi prese (da `MedicationLog`) + FAB aggiunta manuale con DateTimePicker e selezione farmaco | done |
| 4.5.6 | Firestore Security Rules: permesso `delete` su `therapies/{id}` + `medicationLogs` cascade | done |
| 4.5.7 | UI: QuickLogSheet — tile "Farmaci", flusso terapia→farmaco→azione (segna presa / vai storico), nessuna terapia→naviga a AddTherapy | done |
| 4.5.8 | Test unitari: Use Cases `updateTherapy`, `terminateTherapy`, `deleteTherapy`, `addManualMedicationLog` + `QuickLogViewModel` metodi terapia (`selectType(THERAPY)`, `logMedication`, `loadTherapies` con fakes) | done |

**Scelte architetturali:**
- Termina = soft-stop: `endDate = today`, `status = COMPLETED`; terapia rimane visibile in lista con badge "Conclusa"; log esistenti conservati
- Elimina = hard delete: dialog con warning esplicito "I log saranno cancellati"; cancella `therapy` doc + tutti i `medicationLogs` correlati (batch Firestore)
- Log manuale: riutilizza il model `MedicationLog` esistente con flag `isManual = true`

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

## Milestone 5.5 — UX Rework: Dashboard, Navigazione, Persone
**Goal**: Quick Log per-persona (incluso farmaco), pagina Account, navigazione coerente, persone distinguibili | **Status**: in-progress | **Spec**: [ux-rework](specs/ux-rework.md) — ⚠️ open questions OQ1–OQ5 da chiudere prima di 5.5.2+

| Task | Descrizione | Status |
|---|---|---|
| 5.5.1 | Bottom navigation shell 4 tab (Home, Persone, Profilo, Impostazioni) + inner NavHost | done |
| 5.5.2 | Quick Log per-persona: azione "+" nella card, sheet vincolato alla persona, header con nome (R2) | done |
| 5.5.3 | Evento "Farmaco" nel Quick Log: dosi schedulate terapie attive → conferma TAKEN (R3) | done |
| 5.5.4 | Tab Profilo/Account: vedi/modifica profilo se loggato, CTA login se anonimo (R4) | done |
| 5.5.5 | Navigazione: no back su tab radice; PersonDetail unica pagina persona, EditPersonScreen assorbita (R5) | todo |
| 5.5.6 | Distinzione persone: colore locale per-utente e/o icona fascia d'età (R6) | todo |
| 5.5.7 | Filtro Home: card singola su selezione + search bar nome/nickname (R7) | todo |
| 5.5.8 | Pulizia: preview duplicate Dashboard, titolo QuickLogSheet con nome, test unit nuovi ViewModel | todo |

---

## Milestone 5.6 — Salvataggio Non Bloccante + Pending Indicator
**Goal**: Tutte le operazioni di salvataggio (activity log, creazione terapia, log terapia) sono fire-and-forget: l'UI si chiude immediatamente con un toast, lo stato di sincronizzazione è visibile nell'app, gli errori sono gestiti con retry silenzioso | **Status**: todo

| Task | Descrizione | Status |
|---|---|---|
| 5.6.1 | Infrastruttura: `SyncStatus` enum (SYNCED, PENDING, ERROR) + colonna `syncStatus` nelle Room entities `ActivityLog`, `Therapy`, `MedicationLog` | todo |
| 5.6.2 | Repository pattern: scrittura Room-first → Firestore in coroutine background; in caso di errore: 3 retry esponenziali silenziosi → imposta `syncStatus = ERROR` | todo |
| 5.6.3 | UI: indicatore pending — icona/badge discreta sull'elemento in lista quando `syncStatus != SYNCED` (es. orologio o dot colorato) | todo |
| 5.6.4 | Activity Log non bloccante: Quick Log Bottom Sheet si chiude al tap "Salva" + toast "Registrato"; log appare in lista immediatamente con stato PENDING | todo |
| 5.6.5 | Creazione terapia non bloccante: ultimo step wizard chiude la schermata immediatamente + toast "Terapia salvata"; terapia appare in lista con stato PENDING | todo |
| 5.6.6 | Log terapia non bloccante: `TherapyLogScreen` aggiunta manuale chiude dialog immediatamente + toast + entry in lista con PENDING | todo |

**Scelte architetturali:**
- Room è la source of truth locale; Firestore è il target di sync
- Nessun WorkManager per ora: retry gestito in-process con `retry` su coroutine (3 tentativi, backoff 1s/2s/4s)
- Se tutti i retry falliscono → `syncStatus = ERROR` → l'utente vede l'indicatore ma non viene disturbato
- Toast: `Snackbar` breve (2s), nessun tasto Retry esposto in UI

---

## Milestone 5.7 — Branding: Tema Allineato al Logo
**Goal**: Il tema Compose riflette l'identità visiva del logo (navy, gradiente blu-viola); `dynamicColor` disabilitato per coerenza su tutti i dispositivi | **Status**: todo

| Task | Descrizione | Status |
|---|---|---|
| 5.7.1 | Estrai palette M3 dal logo: primary navy `~#1B2763`, secondary blue-cyan `~#40C8F5`, tertiary violet `~#B868E8` → genera token M3 completi (container, on*, surface) | todo |
| 5.7.2 | `Color.kt`: sostituisci token default Material3 purple con palette logo; `Theme.kt`: imposta `dynamicColor = false` | todo |
| 5.7.3 | Verifica visiva: bottom nav, card, FAB, dialog, QuickLog sheet — nessun colore fuori palette | todo |

**Note:**
- Colori logo (da `ic_launcher.webp` xxxhdpi): sfondo **navy** `~#1B2763`, cuore **gradiente** `~#40C8F5 → #B868E8`, testo bianco
- Dark scheme: primary → versione chiara del navy (`~#8BB4FF`), secondary/tertiary chiariti proporzionalmente
- M9.5 ("colori pastello nel tema") rimpiazzato da questa milestone — aggiornare M9.5 a "verifica accessibilità contrasto + touch target" quando M5.7 è done

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
