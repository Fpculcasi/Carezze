# THINK — Task 7.2: MedicationReminderWorker

## Context
Worker periodico on-device che legge le terapie attive da Room, calcola le dosi in scadenza
nei prossimi 30 minuti e posta una notifica locale Android per ogni dose non ancora confermata.

### Context Saturation Check
- [x] **File/path target** — DAOs in `data/local/db/dao/`, nuovo `data/worker/`, `CarezzeApplication.kt`, `build.gradle.kts`, `libs.versions.toml`
- [x] **Comportamento atteso** — worker gira ogni 15 min; per ogni terapia attiva controlla scheduledTimes del giorno corrente nella finestra [now, now+30min]; se non esiste un MedicationLog TAKEN/SKIPPED → posta notifica
- [x] **Vincoli** — `hilt-work` è nuova dipendenza (aggiungere `androidx.hilt:hilt-work:1.2.0` + `androidx.hilt:hilt-compiler:1.2.0` ksp); `TherapyDao` e `MedicationLogDao` mancano di query per lettura puntuale (vs Flow)
- [x] **Dipendenze** — `TherapyDao`, `MedicationLogDao`, `NotificationManager`; `hilt-work` non ancora nel Gradle

## Approaches Considered
- **A (scelto):** `@HiltWorker` con `HiltWorkerFactory` in `CarezzeApplication`. Worker legge Room direttamente (nessuna Firestore call — offline-safe). Pro: pattern ufficiale Hilt, testabile, nessuna latenza di rete. Contro: necessita nuova dep.
- **B:** `CoroutineWorker` con factory custom iniettata via Hilt senza `hilt-work`. Pro: evita la dep. Contro: boilerplate elevato per mappare manualmente i worker; non scalabile per 7.3.

## Selected Approach & Risks
Approccio A — costo **med** (7 file, 1 nuova dep). Rischi:
1. Versione `hilt-work` incompatibile con Hilt 2.56.1 → mitigo verificando BOM compatibility (1.2.0 è compatibile con Hilt 2.50+)
2. Query Room errata sui `scheduledTimes` (JSON string in Room) → uso logica Kotlin sul risultato, non SQL JSON
3. Notifica non mostrata su Android 13+ senza permesso `POST_NOTIFICATIONS` → permesso già dichiarato nel manifest

## Pre-mortem
1. **Build KSP fallisce** con hilt-work 1.2.0 — mitigo: uso la stessa versione di hilt-navigation-compose già dichiarata
2. **Worker non schedulato** perché manca `HiltWorkerFactory` in Application — mitigo: la Configuration.Provider è required
3. **Dosi duplicate** se il worker gira ogni 15 min e la notifica viene postata ogni volta — mitigo: uso NotificationManager con `notify(id)` dove id = hash(therapyId+medicationId+scheduledTimeSlot) → Android deduplicano automaticamente

## Devil's Advocate
Alternativa: usare AlarmManager invece di WorkManager per precisione temporale al minuto esatto. Non superiore: più complessa da gestire (intent, registrazione, Doze mode), e WorkManager con `PeriodicWorkRequest` da 15 min è sufficiente per un reminder farmaci.

## Verification Plan
- Build debug compila con hilt-work (nessun errore KSP)
- Worker registrato e schedulato via `WorkManager.getInstance(ctx).getWorkInfosByTag("medication_reminder")` 
- Grep: `@HiltWorker` in MedicationReminderWorker, `HiltWorkerFactory` in CarezzeApplication
- Unit test su `MedicationReminderWorker.computeDueDoses()` (logica pura estraibile)
