---
name: tasks-5.6.1
description: Checklist task 5.6.1 — SyncStatus + Room infrastruttura
metadata:
  type: project
---

# TASK — 5.6.1

Delivers: SyncStatus enum + Room entities + DAO + DB + DI compilabili; colonna syncStatus presente su ActivityLog, Therapy, MedicationLog.

## Checklist

- [ ] domain/model/SyncStatus.kt — enum SYNCED, PENDING, ERROR
- [ ] domain/model/ActivityLog.kt — abstract syncStatus: SyncStatus
- [ ] domain/model/Therapy.kt — syncStatus su Therapy e MedicationLog
- [ ] data/local/db/entity/ActivityLogEntity.kt
- [ ] data/local/db/entity/TherapyEntity.kt
- [ ] data/local/db/entity/MedicationLogEntity.kt
- [ ] data/local/db/dao/ActivityLogDao.kt
- [ ] data/local/db/dao/TherapyDao.kt
- [ ] data/local/db/dao/MedicationLogDao.kt
- [ ] data/local/db/CarezzeDatabase.kt
- [ ] di/RoomModule.kt

## CDM
✅ Validation: il progetto compila senza errori dopo le modifiche
