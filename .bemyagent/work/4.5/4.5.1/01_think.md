---
name: think-4.5.1
description: THINK per task 4.5.1 — TherapyStatus enum + Use Cases domain layer
metadata:
  type: project
---

# THINK — 4.5.1

## Context Saturation Check
- TherapyStatus: non esiste — solo `isActive: Boolean`. ✅ verificato in Therapy.kt
- Use cases `updateTherapy` e `deleteTherapy`: esistono già. ✅
- `terminateTherapy` e `addManualMedicationLog`: non esistono. ✅
- `MedicationLog.isManual`: non esiste — da aggiungere. ✅
- MedicationLogRepository.logMedication: firma nota, nessun param `isManual`. ✅
- 0 unknown → proseguo.

## Approcci considerati

**A — Replace `isActive` con `status: TherapyStatus`**  
Rompe la build in 7 callsite (impl + UI + 4 test) che passano `isActive = true` nel costruttore.
Viola "surgical scope" (andrebbe a toccare data/UI/test, fuori task).

**B (scelto) — Aggiunta backward-compatible**  
- Aggiungo `TherapyStatus` enum (ACTIVE, COMPLETED)
- Aggiungo `endDate: LocalDate? = null` a `Therapy`
- Aggiungo `val status: TherapyStatus get() = if (isActive) TherapyStatus.ACTIVE else TherapyStatus.COMPLETED`
- Aggiungo `isManual: Boolean = false` a `MedicationLog` (default → nessun callsite esistente si rompe)
- Aggiungo `isManual: Boolean = false` a `MedicationLogRepository.logMedication`
- Creo `TerminateTherapyUseCase` (chiama getTherapy → copy(isActive=false, endDate=today) → updateTherapy)
- Creo `AddManualMedicationLogUseCase` (chiama logMedication con isManual=true)

Task 4.5.2 migrerà il data layer a scrivere `status` nativo; lì si rimuoverà `isActive` definitivamente.

## Pre-mortem
1. `TerminateTherapyUseCase` chiama `getTherapy` + `updateTherapy` — due suspend call incatenati; se getTherapy fallisce, updateTherapy non viene chiamato (gestito da `mapCatching`).
2. `endDate` aggiunto in posizione NON-ultima nel data class: Kotlin richiede che i param con default vengano DOPO quelli senza default — verifico ordine.
