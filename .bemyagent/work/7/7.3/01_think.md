# THINK — Task 7.3: InactivityCheckWorker

## Context
Worker periodico che avvisa l'utente quando mancano registrazioni recenti (es. pannolino non
cambiato da >4h). Lavora solo su Room — nessuna Firestore call.

### Context Saturation Check
- [x] **File/path target** — `ActivityLogDao.kt` (nuove query), nuovo `data/worker/InactivityCheckWorker.kt`
- [x] **Comportamento atteso** — ogni 30min: carica personId distinti da `activity_logs` (ultimi 7 giorni), per ciascuno carica l'ultimo log per tipo, se delta > soglia → notifica via NotificationHelper
- [x] **Vincoli** — nessuna PersonEntity Room; soglie hard-coded ma per tipo (diaper=4h, meal=4h, sleep=24h); nessuna nuova dipendenza
- [x] **Dipendenze** — `ActivityLogDao`, `NotificationHelper` già disponibili via Hilt; worker schedulato da `CarezzeApplication.onCreate()`

## Approaches Considered
- **A (scelto):** due query Room nuove (`getDistinctPersonIds` + `getLastLogByType`) → logica Kotlin nel Worker. Pro: offline-safe, semplice. Contro: nomi persona non disponibili → usa personId nell'avviso (task 7.4 risolve la navigazione).
- **B:** query Firestore per i dati persona. Non superiore: rompe offline-safety, aggiunge latenza.

## Selected Approach & Risks
Approccio A — costo **low** (2 file). Rischi:
1. `personId` non leggibile dall'utente in notifica — accettabile per ora (navigazione gestita in 7.4)
2. Worker duplica alert ogni 30min se l'utente non registra — mitigo con `notify(id)` deduplicato (Android rimpiazza la notifica con stesso id)

## Pre-mortem
1. Tabella `activity_logs` vuota → `getDistinctPersonIds` returns empty list → nessun alert (comportamento corretto)
2. Thresholds hard-coded → non configurabili per ora; ok per M7 (configurabilità va in backlog)

## Devil's Advocate
Alternativa: unico Worker per medication + inactivity. Non superiore: responsabilità miste, più difficile testare la logica di inattività separatamente.

## Verification Plan
- Build compila
- Grep: `@HiltWorker` in InactivityCheckWorker.kt
- Grep: `getDistinctPersonIds` in ActivityLogDao.kt
- Unit test verdi
