# TASK — 4.5.8 Unit Tests: Use Cases Terapia + QuickLogViewModel

**Delivers:** Suite di test unitari eseguibile via `./gradlew test` che copre i 4 use case terapia e QuickLogViewModel — evidence: output gradle con 0 failure.

## Checklist

- [ ] `TerminateTherapyUseCaseTest` — success (slot verifica isActive=false, endDate!=null), failure getTherapy, failure updateTherapy
- [ ] `AddManualMedicationLogUseCaseTest` — verifica status=TAKEN e isManual=true, failure propagation
- [ ] `QuickLogViewModelTest` — logMedication no-op con currentUser null, isSaved su success, error su failure, filtra therapie attive, clearType reset

## Validation (CDM)
✅ `./gradlew :app:test` termina con BUILD SUCCESSFUL e 0 test failure nei 3 file nuovi.
