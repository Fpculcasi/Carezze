# Carezze — Panoramica del Progetto

## Descrizione

Carezze è un'app mobile Android (Kotlin + Jetpack Compose) per il tracciamento di terapie mediche e parametri neonatali/sanitari. Permette a chiunque di creare profili per se stesso o per altre persone (neonati, pazienti, familiari), definire terapie con farmaci e schedule, registrare eventi giornalieri (pasti, pannolini, sonno, temperatura, peso, igiene) e condividere in tempo reale tutto questo con altri utenti tramite un sistema di inviti sicuri. Nasce da un'esigenza personale (nascita di una figlia) ed è progettato come portfolio GitHub professionale con architettura TDD, MVVM, GitFlow e supporto multi-agente.

## Glossario

| Termine | Definizione |
|---|---|
| **Utente** | Qualsiasi persona con un account nell'app (email, Google, anonimo locale) |
| **Persona** | Il soggetto monitorato: un neonato, un paziente, o l'utente stesso |
| **Terapia** | Un insieme di uno o più Farmaci con relative schedule, associata a una Persona |
| **Farmaco** | Un singolo medicinale all'interno di una Terapia, con dosaggio, frequenza e orari |
| **Orario Schedulato** | Orario calcolato automaticamente per la somministrazione di un Farmaco |
| **Log di Attività** | Una registrazione timestampata di un evento per una Persona (pasto, pannolino, sonno, temperatura, peso, igiene) |
| **Log di Farmaco** | Una registrazione della somministrazione (o skip) di un Farmaco in un Orario Schedulato |
| **Membro** | Un Utente che ha accesso a una Persona o a una Terapia specifica |
| **Invito** | Un codice/QR monouso con scadenza (default 24h) per autorizzare un altro Utente ad accedere a una Persona o Terapia |
| **Accesso Persona** | Permette di vedere e registrare tutti i Log di Attività e le Terapie di una Persona |
| **Accesso Terapia** | Permette di vedere e registrare i Log di Farmaco di una specifica Terapia, senza accesso agli altri dati della Persona |
| **Dashboard** | Schermata principale: vista card per Persona o feed cronologico di tutti gli eventi |
| **Quick Log** | Azione rapida (1 tap) per registrare un evento comune direttamente dalla Dashboard o dal Widget |
| **Widget** | Componente Android Glance sulla home screen del telefono per Quick Log e countdown terapie |
| **Modalità Locale** | Uso dell'app senza account (Firebase Anonymous Auth), con dati solo sul dispositivo e possibilità di migrazione futura |
| **GitFlow** | Strategia di branching: `main`, `develop`, `feature/*`, `release/*`, `hotfix/*` |
| **TTEV** | Think → Task → Execute → Verify — workflow degli agenti AI sul progetto |

## Success Metrics

| Metrica | Target |
|---|---|
| Tap per registrare evento comune | ≤ 2 (con dettagli opzionali aggiuntivi) |
| Latenza sincronizzazione real-time | < 2 secondi |
| Widget operativo senza aprire l'app | ✅ |
| Supporto offline con sync automatica | ✅ |
| Coverage test unitari | ≥ 80% |
| Lingue supportate (v1) | Italiano, Inglese |
| Piattaforme (v1) | Android (iOS futuro) |

## Struttura del Repository

```
Carezze/
├── app/                          # Modulo Android principale
│   ├── src/main/
│   │   ├── java/com/carezze/
│   │   │   ├── data/             # Repository impl, Firestore, Room, FCM
│   │   │   ├── domain/           # Modelli, Use Cases, interfacce Repository
│   │   │   ├── ui/               # Compose screens, ViewModels
│   │   │   │   ├── auth/
│   │   │   │   ├── dashboard/
│   │   │   │   ├── person/
│   │   │   │   ├── therapy/
│   │   │   │   ├── history/
│   │   │   │   ├── invitation/
│   │   │   │   └── settings/
│   │   │   └── widget/           # Glance widgets
│   │   └── res/
│   └── src/test/ + androidTest/
├── functions/                    # Firebase Cloud Functions (Node.js/TypeScript)
├── firestore.rules               # Firestore Security Rules
├── .bemyagent/                   # AI agent workspace
├── .github/                      # CI/CD (GitHub Actions)
└── README.md
```

## Comandi Rapidi

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run lint
./gradlew lint

# Static analysis (Detekt)
./gradlew detekt

# Code style check (Ktlint)
./gradlew ktlintCheck

# Auto-fix code style
./gradlew ktlintFormat

# Build release APK (richiede keystore)
./gradlew assembleRelease
```

> JAVA_HOME deve puntare al JDK 21 di Android Studio se il JDK di sistema non è 21:
> `export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"`

## Variabili d'Ambiente / Firebase Config

| Variabile | Default | Descrizione |
|---|---|---|
| `GOOGLE_SERVICES_JSON` | — | Firebase config per Android (non committare) |
| `FIREBASE_PROJECT_ID` | `carezze-5a3b0` | ID progetto Firebase |
| `FCM_SERVER_KEY` | — | Chiave server FCM per Cloud Functions |

## Note Portfolio

- Documentazione pubblica in **inglese** (README, codice, commenti)
- Documentazione agente interna in **italiano** (`.bemyagent/docs/`)
- Progetto personale: nascita della figlia del maintainer
