# VERIFY — 7.2: MedicationReminderWorker

## Criterio 1 — Build compila con hilt-work
**Evidenza:** `./gradlew assembleDebug` → `BUILD SUCCESSFUL in 4m 11s`
**Verdict:** PASS

## Criterio 2 — @HiltWorker in MedicationReminderWorker
**Evidenza:** `grep @HiltWorker MedicationReminderWorker.kt` → riga 16
**Verdict:** PASS (verificato tramite file scritto)

## Criterio 3 — HiltWorkerFactory in CarezzeApplication
**Evidenza:** `grep HiltWorkerFactory CarezzeApplication.kt` → riga 18 (inject) + riga 22 (uso in Configuration)
**Verdict:** PASS

## Criterio 4 — getActiveTherapies in TherapyDao
**Evidenza:** TherapyDao.kt aggiornato con `@Query("SELECT * FROM therapies WHERE isActive = 1")`
**Verdict:** PASS

## Criterio 5 — Unit test verdi
**Evidenza:** `./gradlew testDebugUnitTest` → `BUILD SUCCESSFUL` (34 tasks)
**Verdict:** PASS

## Verdict globale: **PASS**
