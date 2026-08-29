# Carezze

> *Carezze* means *caresses* in Italian — gentle, caring touches. It also starts with **Care**.

A mobile app for tracking medical therapies and neonatal parameters. Built for families, caregivers, and anyone managing ongoing health routines.

---

## Why This Exists

My daughter was born. A few weeks in, my partner and I kept asking each other the same question at 2am: *"Did you already give her the drops?"*

Lot of sticky notes all around the kitchen, and a forgot shared Google Sheet. Just two exhausted parents with two phones and zero coordination.

Carezze is the app I wished I had. It tracks therapies (medications, schedules, progress) and neonatal parameters (feeds, diapers, sleep, temperature) across everyone caring for the same person — in real time, privately, and **without requiring an account**.

---

## Core Principles

### No account required
You can use Carezze entirely offline, without signing up. Your data stays on your device. If you later decide to share with family or sync across devices, you register — and your existing data migrates automatically.

### Privacy by design
- Data stored in **Europe (Firebase `europe-west12 (Turin)`)** — GDPR compliant
- Sharing requires an **explicit single-use invite** (8-char code, 24h expiry)
- Revoking access **deletes** the other user's contributed data
- No analytics, no ads, no third-party data sharing

### Open source
Every architectural decision is visible in the code and documented in commit history. Fork it, contribute, or just read it.

---

## Features

- **Therapy management** — define multi-drug therapies with automatic dose scheduling; track progress as a bar, calendar, and remaining-dose counter
- **Activity logging** — meals (ml / minutes / grams), diapers, sleep intervals, temperature, weight, hygiene — all in 1 tap
- **Real-time family sync** — Firestore snapshot listeners propagate every update across all shared devices instantly
- **Medication confirmation** — when one family member marks a dose as taken, the notification dismisses on everyone's phone
- **Inactivity alerts** — configurable alerts per activity type (e.g. "no feed logged in 4 hours")
- **Home screen widgets** — therapy countdown, diaper quick-log, meal quick-log — without opening the app
- **Granular sharing** — share an entire person profile *or* just a single therapy (e.g. share the antibiotic schedule with the pediatrician, not the diaper log)
- **Offline first** — works without a connection; syncs automatically when back online
- **Multilingual** — Italian and English, switchable in-app

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose |
| Architecture | MVVM + Clean Architecture (Use Cases, Repository pattern) |
| Local storage | Room (SQLite) |
| Remote database | Cloud Firestore (offline-first) |
| Authentication | Firebase Auth (Email, Google, Anonymous) |
| Push notifications | Firebase Cloud Messaging |
| Server-side logic | Firebase Cloud Functions (Node.js) |
| Dependency injection | Hilt |
| Background sync | WorkManager |
| Home screen widgets | Jetpack Glance |
| Testing | JUnit 5 + MockK + Turbine |
| CI/CD | GitHub Actions |
| Code quality | Detekt + Ktlint |

---

## Architecture

```
┌─────────────────────────────────────────────┐
│              Android App                     │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  │
│  │ UI Layer │  │  Domain  │  │   Data    │  │
│  │ Compose  │→ │Use Cases │→ │Firestore  │  │
│  │ViewModels│  │  Models  │  │  + Room   │  │
│  └──────────┘  └──────────┘  └───────────┘  │
│  ┌──────────┐                               │
│  │ Widgets  │  (Glance, reads from Room)    │
│  └──────────┘                               │
└─────────────────────────────────────────────┘
         │                    │
   Firebase Auth        Cloud Firestore
   (anonymous ok)       (europe-west1)
                              │
                    Firebase Cloud Functions
                    (notifications, invite validation)
                              │
                    Firebase Cloud Messaging
                    (push to all shared devices)
```

The domain layer has **zero Android or Firebase dependencies** — all use cases and models are pure Kotlin, fully testable with JUnit 5 and MockK without an emulator.

---

## Data Model (Firestore)

```
users/{userId}
persons/{personId}
  └── therapies/{therapyId}
        └── medicationLogs/{logId}
  └── activityLogs/{logId}
invitations/{inviteId}
```

Sharing is enforced by a `members` map on each `persons` and `therapies` document. Firestore Security Rules ensure users can only read and write documents they are explicitly listed in. Invite redemption is handled by a Cloud Function (HTTPS callable) to guarantee atomicity — no client-side bypass is possible.

---

## Getting Started

> ⚠️ Project is under active development. Setup instructions will be updated as the codebase grows.

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+
- A Firebase project on the Blaze plan (required for Cloud Functions)

### Setup

```bash
git clone https://github.com/your-username/carezze.git
cd carezze

# Copy the Firebase config (obtain from Firebase Console)
cp google-services.json.template app/google-services.json
# Fill in your Firebase project values

# Install Firebase CLI (for Cloud Functions)
npm install -g firebase-tools
firebase login

# Deploy Cloud Functions
cd functions && npm install && cd ..
firebase deploy --only functions
```

### Run

Open the project in Android Studio and run the `app` configuration on a device or emulator (API 26+).

### Test

```bash
./gradlew test              # Unit tests
./gradlew connectedTest     # Instrumented tests (requires emulator)
```

---

## Project Structure

```
carezze/
├── app/
│   └── src/
│       ├── main/java/com/carezze/
│       │   ├── data/         # Repository implementations, Firestore, Room, FCM
│       │   ├── domain/       # Models, Use Cases, Repository interfaces
│       │   ├── ui/           # Compose screens and ViewModels
│       │   └── widget/       # Glance widgets
│       └── test/ + androidTest/
├── functions/                # Firebase Cloud Functions
├── firestore.rules           # Firestore Security Rules
└── .github/workflows/        # GitHub Actions CI
```

---

## Branching Strategy

This project follows **GitFlow**:

| Branch | Purpose |
|---|---|
| `main` | Production-ready releases only |
| `develop` | Integration branch for completed features |
| `feature/X` | Individual features |
| `release/X.Y` | Release preparation |
| `hotfix/X` | Critical fixes on main |

AI agents working on this project use worktrees with branch naming `bma/X.Y` (one per task), merged into `develop` via PR.

---

## Development Blog

This project is documented publicly as it's built:

| Post | Topic | Status |
|---|---|---|
| [#1 — The Idea](https://medium.com) | Why Carezze exists, the Firestore schema, architecture decisions | planned |
| [#2 — Foundations](https://medium.com) | Firebase, offline-first, anonymous auth | planned |
| [#3 — Core Features](https://medium.com) | Therapies, neonatal logging, TDD in practice | planned |
| [#4 — Sharing & Security](https://medium.com) | Real-time sync, invite system, Firestore rules | planned |
| [#5 — Launch & Retrospective](https://medium.com) | Widgets, AI-assisted development, lessons learned | planned |

---

## Roadmap

**v1.0**
- [x] Project design and architecture
- [ ] Android project setup + CI/CD
- [ ] Authentication (anonymous, email, Google)
- [ ] Person and therapy management
- [ ] Activity logging and dashboard
- [ ] Family sharing and invitations
- [ ] Push notifications (FCM)
- [ ] Home screen widgets (Glance)
- [ ] Italian / English localization

**Future**
- iOS support
- Data export (PDF / CSV)
- Dark mode
- Onboarding wizard
- Additional languages

---

## Contributing

Contributions are welcome. Please open an issue before submitting a PR so we can discuss the approach.

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/your-idea`)
3. Commit with clear messages
4. Open a pull request against `develop`

---

## License

[MIT](LICENSE)

---

*Built by a parent who needed it. Open to anyone who does.*