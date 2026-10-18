---
name: tasks-4.5.1
description: Checklist task 4.5.1 — TherapyStatus enum + Use Cases domain layer
metadata:
  type: project
---

# TASK — 4.5.1

**Delivers:** `TerminateTherapyUseCase` e `AddManualMedicationLogUseCase` invocabili da ViewModel; model `Therapy` espone `status: TherapyStatus` e `endDate`; `MedicationLog` ha flag `isManual`.

## Checklist
- [ ] `TherapyStatus` enum aggiunto in `Therapy.kt`
- [ ] `endDate: LocalDate? = null` aggiunto a `Therapy` data class (dopo `duration`, prima di `members`)
- [ ] computed `val status: TherapyStatus` aggiunto al body di `Therapy`
- [ ] `isManual: Boolean = false` aggiunto a `MedicationLog`
- [ ] `isManual: Boolean = false` param aggiunto a `MedicationLogRepository.logMedication`
- [ ] `TerminateTherapyUseCase.kt` creato
- [ ] `AddManualMedicationLogUseCase.kt` creato
- [ ] build verde (./gradlew assembleDebug)

## Validation (CDM)
✅ `./gradlew assembleDebug` → BUILD SUCCESSFUL (nessun errore di compilazione)
