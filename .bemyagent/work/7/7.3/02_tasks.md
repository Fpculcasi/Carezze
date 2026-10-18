# TASK — 7.3: InactivityCheckWorker

Delivers: l'app avvisa l'utente quando per una persona non vengono registrate attività (pannolino, pasto, ecc.) oltre la soglia configurata (4h diaper/meal, 24h sleep).

## Checklist

- [ ] `data/local/db/dao/ActivityLogDao.kt` — aggiungere `getDistinctPersonIds(since: Long): List<String>` e `getLastLogByType(personId, type): ActivityLogEntity?`
- [ ] `data/worker/InactivityCheckWorker.kt` — `@HiltWorker` CoroutineWorker: carica personId → per tipo controlla delta vs soglia → posta notifica inattività
- [ ] `CarezzeApplication.kt` — schedulare `InactivityCheckWorker` ogni 30 min in `scheduleWorkers()`

## CDM

### ✅ Validation
- Build debug compila
- Grep: `@HiltWorker` in InactivityCheckWorker.kt
- Grep: `getDistinctPersonIds` in ActivityLogDao.kt
- `InactivityCheckWorker` schedulato in CarezzeApplication
- Unit test verdi
