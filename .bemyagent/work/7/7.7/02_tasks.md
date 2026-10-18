# TASK — 7.7: Unit Tests M7

Delivers: test unitari per la logica di calcolo dosi imminenti, verificabili con `./gradlew testDebugUnitTest`.

## Checklist

- [ ] `app/src/test/.../data/worker/MedicationReminderWorkerTest.kt` — test per `computeDueDoses()`: finestra esatta, dose fuori finestra, finestra vuota, dosi multiple, tempo esatto = now, formato HH:mm invalido

## CDM

### ✅ Validation
- `./gradlew testDebugUnitTest` verde (numero test aumentato rispetto a baseline di 34)
