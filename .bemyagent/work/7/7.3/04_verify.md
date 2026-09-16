# VERIFY — 7.3: InactivityCheckWorker

## Criterio 1 — Build compila
**Evidenza:** `./gradlew assembleDebug` → `BUILD SUCCESSFUL in 1m 8s`
**Verdict:** PASS

## Criterio 2 — @HiltWorker in InactivityCheckWorker
**Evidenza:** InactivityCheckWorker.kt, riga 24: `@HiltWorker`
**Verdict:** PASS

## Criterio 3 — getDistinctPersonIds in ActivityLogDao
**Evidenza:** ActivityLogDao.kt aggiornato con `@Query("SELECT DISTINCT personId ...")`
**Verdict:** PASS

## Criterio 4 — Worker schedulato in CarezzeApplication
**Evidenza:** CarezzeApplication.kt — `enqueueUniquePeriodicWork(TAG_INACTIVITY_CHECK, ..., 30 TimeUnit.MINUTES)`
**Verdict:** PASS

## Verdict globale: **PASS**
