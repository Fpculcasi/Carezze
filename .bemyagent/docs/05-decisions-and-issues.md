# Decisioni & Problemi — Carezze

## Decisions Index

| # | Decisione | Sintesi | File |
|---|---|---|---|
| D-01 | Stack Android | Kotlin + Jetpack Compose (non Flutter) | inline |
| D-02 | Database | Firestore + Room (dual-layer offline-first) | inline |
| D-03 | Auth anonima | Firebase Anonymous Auth per modalità locale | inline |
| D-04 | Farmaci come array | `medications[]` nel documento Therapy, non sub-collection | inline |
| D-05 | Condivisione a due livelli | Persona O Terapia condivisibili separatamente | inline |
| D-06 | Invito monouso | Codice 8 char alfanumerico, scadenza 24h, single-use | inline |
| D-07 | Revoca con cancellazione dati | Rimozione Membro cancella i suoi dati dal profilo | inline |
| D-08 | Validazione invito lato client | Firestore transaction atomica (no Cloud Function) | inline |
| D-09 | Indici denormalizzati | `personAccess[]` e `therapyAccess[]` su `users/{id}` | inline |
| D-10 | Dose saltata: logica lazy | Dosi non confermate → marcate `SKIPPED` alla successiva somministrazione | inline |
| D-15 | No Cloud Functions (Spark plan) | M6 → Firestore transactions; M7 → WorkManager on-device | inline |
| D-11 | Regione Firebase | `europe-west1` per GDPR | inline |
| D-12 | Nome app | Carezze (working name, può cambiare) | inline |
| D-13 | GitFlow | `main`, `develop`, `feature/*`, `release/*`, `hotfix/*` | inline |
| D-14 | TDD | Test-Driven Development obbligatorio, coverage ≥ 80% | inline |
| D-16 | Bottom navigation shell | NavHost annidato: root (auth) + `MainScreen` con NavigationBar 4 tab | inline |

## Decisioni Inline

### D-01 — Stack Android: Kotlin + Jetpack Compose
- **Problema**: iOS potrebbe essere necessario in futuro; Flutter avrebbe permesso code sharing
- **Decisione**: Kotlin + Compose per v1
- **Trade-off**: iOS richiederà rewrite o wrapper; in compenso si sfrutta l'ecosistema Android nativo (Hilt, Room, Glance, WorkManager) senza friction layer

### D-02 — Database Dual-Layer: Firestore + Room
- **Problema**: Firestore ha offline cache nativa ma limitata nelle query locali
- **Decisione**: Room come source of truth locale, Firestore come source of truth remota; sincronizzazione via WorkManager
- **Trade-off**: doppia complessità di mapping; in compenso query locali veloci, offline totale garantito, e widget Android che legge da Room senza rete

### D-03 — Modalità Locale con Anonymous Auth
- **Problema**: Utenti che non vogliono registrarsi devono comunque usare l'app
- **Decisione**: Firebase Anonymous Auth — Firestore rules trattano l'utente anonimo come qualsiasi altro; al momento della registrazione si usa `linkWithCredential()` e i dati migrano automaticamente
- **Trade-off**: Firestore usato anche per utenti locali (piccolo costo); in compenso migrazione trasparente senza perdita dati

### D-04 — Farmaci come Array nel Documento Therapy
- **Problema**: Sub-collection vs array per i farmaci di una terapia
- **Decisione**: Array `medications[]` nel documento Therapy
- **Trade-off**: Limite 1MB per documento Firestore; accettabile perché N farmaci per terapia è tipicamente < 10 (raramente > 20). Vantaggio: lettura terapia + farmaci in un solo round-trip; condivisione terapia include automaticamente tutti i farmaci

### D-05 — Condivisione a Due Livelli (Persona e Terapia)
- **Problema**: L'utente vuole condividere selettivamente
- **Decisione**: Condivisione indipendente a livello Persona (tutti i dati) o Terapia (solo log farmaci di quella terapia)
- **Trade-off**: Security rules più complesse; in compenso privacy granulare (es. condivido la terapia antibiotico col pediatra senza dargli accesso ai pannolini del bambino)

### D-06 — Invito Monouso con Scadenza 24h
- **Problema**: Link permanenti sono un rischio sicurezza (forwarding non autorizzato)
- **Decisione**: Codice 8 char alfanumerico (≈ 36^8 ≈ 2.8 trilioni combinazioni), monouso, scadenza 24h dall'emissione
- **Trade-off**: L'utente deve ricreare il codice se scade; accettabile perché la condivisione è un'azione deliberata non frequente

### D-07 — Revoca con Cancellazione Dati
- **Problema**: Cosa succede ai Log inseriti da un Membro rimosso?
- **Decisione**: I dati si cancellano lato client nella stessa transazione Firestore che rimuove il membro (vedi D-15)
- **Trade-off**: Perdita dati irreversibile; accettabile perché l'utente che revoca l'accesso si aspetta di "annullare" la condivisione; avvisare con dialog di conferma prima della revoca

### D-08 — Validazione Invito: Firestore Transaction (no Cloud Function)
- **Problema**: Garantire atomicità del riscatto codice (single-use, scadenza, scrittura accesso) senza server-side logic
- **Decisione**: Firestore transaction sul client: legge il documento invito, verifica `used=false` + scadenza, scrive `used=true` + aggiorna `personAccess[]`/`therapyAccess[]` in un'unica operazione atomica; Security Rules bloccano scritture non autorizzate
- **Trade-off**: Un client malevolo con SDK custom potrebbe tentare abusi; mitigato dalle Security Rules (il documento invito è write-once su `used`) e dal codice 8-char monouso con entropia sufficiente. Upgrade futuro a Cloud Function è trasparente.

### D-09 — Indici Denormalizzati su users/{userId}
- **Problema**: Firestore non supporta `JOIN`; per listare "tutte le Persone a cui ho accesso" serve un indice
- **Decisione**: Array `personAccess: [personId, ...]` e `therapyAccess: ["personId_therapyId", ...]` nel documento utente, aggiornati nella transaction di riscatto invito lato client (vedi D-08)
- **Trade-off**: Duplicazione dati; in compenso query O(1) per il listing della Dashboard

### D-10 — Dosi Saltate: Logica Lazy On-Device
- **Problema**: Se l'utente non conferma una dose, quando marcarla `SKIPPED`?
- **Decisione**: Non marcare automaticamente a orario fisso; alla successiva apertura app (o tick WorkManager), il repository crea retrospettivamente i `MedicationLog` con `status=SKIPPED` per tutti gli Orari Schedulati passati non confermati
- **Trade-off**: Lo storico non è aggiornato in tempo reale se il dispositivo è spento; accettabile perché l'importante è che il progresso totale della terapia sia sempre corretto alla riapertura

### D-15 — No Cloud Functions: Spark Plan + Scelta Architetturale
- **Problema**: Cloud Functions richiedono piano Blaze (pay-as-you-go); ma c'è anche una ragione di design
- **Decisione**: Zero Cloud Functions per v1. M6 usa Firestore transactions atomiche lato client; M7 usa WorkManager per scheduling on-device. La logica server-side è garantita dalle Security Rules.
- **Trade-off**: Enforcement meno forte rispetto a una Function server-side (ma sufficiente con Rules ben scritte); in compenso: piano gratuito, app funziona offline totale, nessun cold start, nessun timeout Functions, architettura più portabile. Migrabile a Functions in futuro senza cambiare il domain layer.

### D-11 — Regione Firebase: europe-west1
- **Problema**: Compliance GDPR per dati sanitari (anche se non è un dispositivo medico certificato)
- **Decisione**: Tutti i servizi Firebase in `europe-west1` (Belgio)
- **Trade-off**: Latenza leggermente maggiore per utenti extra-EU; accettabile per un'app pensata per il mercato italiano/europeo

### D-12 — Nome App: Carezze
- **Problema**: Scegliere un nome warm/familiare, bilingue, portfolio-friendly
- **Decisione**: Carezze (working name — può cambiare prima del lancio). In italiano: tenerezza, gesto d'affetto. In inglese: contiene "Care" — cura, attenzione. Doppio significato senza spiegazioni.
- **Trade-off**: Potrebbe sembrare troppo neonatale per utenti adulti in terapia; in compenso è unico, memorabile, e racconta già la storia dell'app nel nome stesso

### D-13 — GitFlow
- **Problema**: Più agenti AI lavorano in parallelo su worktree separati
- **Decisione**: GitFlow standard con branch naming `bma/X.Y` per task agente
- **Trade-off**: Overhead merge; in compenso history pulita, milestones chiaramente isolate, PR review possibile a ogni task

### D-16 — Bottom Navigation Shell (NavHost annidato)
- **Problema**: la navigazione piatta metteva Dashboard, Persone e Impostazioni sullo stesso livello con back-stack confuso (freccia "indietro" da schermate che sono pari-livello)
- **Decisione**: shell `MainScreen` post-auth con `NavigationBar` Material 3 a 4 tab (Home, Persone, Profilo, Impostazioni) e inner NavHost; root NavHost gestisce solo il flusso auth. Tab switch con `saveState`/`restoreState` + `launchSingleTop`; bottom bar nascosta sulle schermate di dettaglio
- **Trade-off**: due NavController da coordinare; in compenso back-stack corretto per tab e gerarchia visiva chiara

### D-14 — TDD Obbligatorio
- **Problema**: Progetto portfolio deve dimostrare qualità ingegneristica
- **Decisione**: Test scritti prima dell'implementazione (red-green-refactor); coverage minima 80%
- **Trade-off**: Velocità di sviluppo iniziale ridotta; in compenso codebase testabile, domain layer puro (no Android SDK), refactoring sicuro

## Engineering Learnings

> Aggiornare durante lo sviluppo con pattern e gotcha scoperti.

- **Firestore Timestamp**: mai esporre `com.google.firebase.Timestamp` al domain layer — mappare sempre in `java.time.Instant` nel data layer
- **Glance + Hilt**: usare `EntryPointAccessors.fromApplication()` per inject in GlanceAppWidget
- **Anonymous → Linked Auth**: `FirebaseAuth.currentUser.linkWithCredential()` preserva l'UID e quindi tutti i documenti Firestore esistenti
- **Versioni Gradle/Kotlin (M1)**: la macchina ha versioni più recenti del training LLM. Versioni verified in cache locale: AGP 8.9.1, Kotlin 2.2.10, KSP 2.2.10-2.0.2, Hilt 2.56.1. Aggiornare `libs.versions.toml` controllando sempre `~/.gradle/caches/modules-2/files-2.1/` prima di specificare versioni.
- **Detekt FunctionNaming + Compose**: le funzioni `@Composable` iniziano con maiuscola per convenzione. Aggiungere `ignoreAnnotated: ['Composable', 'Preview']` nella regola `FunctionNaming` di `config/detekt/detekt.yml`.
- **Ktlint Composable naming**: aggiungere `ktlint_function_naming_ignore_when_annotated_with = Composable,Preview` in `.editorconfig`.

- **Lint allineato (ktlint ↔ detekt)**: `max_line_length = 120` in `.editorconfig` è obbligatorio — senza, `ktlintFormat` ri-unisce le righe che detekt (MaxLineLength 120) boccia. Config detekt Compose-aware: `ignoreAnnotated: [Composable]` su LongMethod/LongParameterList, `[Preview]` su UnusedPrivateMember, `ignoreAnnotatedFunctions` su TooManyFunctions, `[Inject]` sui costruttori DI. `ReturnCount.excludeGuardClauses: true` esclude SOLO le guardie in testa alla funzione: gli elvis-return dentro un'espressione (es. argomenti di un costruttore) contano — hoistarli in `val x = ... ?: return null` iniziali. `InvalidPackageDeclaration` disattivata (mismatch dir/package documentato in 03). Niente `@Suppress`: preferire refactor (helper `enumOrNull`, mapper per tipo) o taratura config.
- **ViewModel UseCase shadowing → ANR**: se un `@HiltViewModel` ha una `private val` iniettata con lo stesso nome di una `fun` membro pubblica (es. `val updatePerson: UpdatePersonUseCase` + `fun updatePerson(...)`), Kotlin risolve la chiamata alla funzione membro — non all'invoke del use case — causando ricorsione infinita sul `viewModelScope`, CPU al 100% e ANR dopo 10 s. **Regola:** rinominare sempre le proprietà iniettate con suffisso `UseCase` (es. `updatePersonUseCase`) quando il nome collide con un metodo pubblico del ViewModel.

## Known Issues

- **QuickLogSheet senza selezione persona**: il sheet riceve `selectedPersonId ?: persons.first()` — con filtro "Tutti" l'evento viene attribuito silenziosamente alla prima persona. Manca un picker esplicito. (UX rework in pianificazione)
- **QuickLog senza evento "farmaco"**: `ActivityLogType` non include l'assunzione farmaco; le dosi si confermano solo da TherapyDetail. (UX rework in pianificazione)
- **Titolo QuickLogSheet temporaneo**: mostra il `personId` grezzo ("Registra evento per $personId") — placeholder in attesa del rework.
- **Tab radice con freccia back**: `PersonListScreen` e `SettingsScreen` espongono ancora `onNavigateBack` pur essendo destinazioni radice della bottom bar.
- **Preview duplicate in DashboardScreen**: `DashboardContentPreview2` e `DashboardContentPreviewQuickLog` sono identiche — residuo di sperimentazione, da ripulire nel rework.
- **Tab Profilo placeholder**: `composable<Profile>` renderizza solo `Text("Profilo")`.
