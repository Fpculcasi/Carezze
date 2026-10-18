# THINK — Task 7.7: Unit Tests M7

## Context
Test unitari per la logica di scheduling (pura) di M7. Target: `MedicationReminderWorker.computeDueDoses()`.

### Context Saturation Check
- [x] **File/path target** — nuovi file in `app/src/test/kotlin/.../data/worker/`
- [x] **Comportamento atteso** — test per `computeDueDoses`: finestra [now, now+30min], bordi, finestra vuota, multiple dosi, wrap ora
- [x] **Vincoli** — JUnit 5 + nessuna dipendenza Android (la funzione è pura e statica)
- [x] **Dipendenze** — framework già configurato (JUnit 5 + mockk + turbine)

## Selected Approach & Risks
Standard (1 file). Rischio: nessuno — funzione pura, nessuna dipendenza esterna.

## Verification Plan
- `./gradlew testDebugUnitTest` verde con i nuovi test inclusi
