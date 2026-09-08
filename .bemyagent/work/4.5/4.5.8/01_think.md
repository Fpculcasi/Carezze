# THINK — 4.5.8 Unit Tests: Use Cases Terapia + QuickLogViewModel

## Context Saturation Check
| Item | Status |
|---|---|
| Use Cases da testare (UpdateTherapy, TerminateTherapy, DeleteTherapy, AddManualMedicationLog) | ✅ verificati |
| QuickLogViewModel — dipendenze e comportamento | ✅ verificato |
| Pattern test esistenti (JUnit5, MockK, runTest) | ✅ verificato da UpdateTherapyUseCaseTest, DeleteTherapyUseCaseTest |
| Nessun Fake* in progetto — tutti i test usano MockK direttamente | ✅ verificato |
| Turbine disponibile in testImplementation | ✅ verificato in build.gradle.kts |
| TerminateTherapyUseCase dipende da TherapyRepository + GetTherapyUseCase (concrete) | ✅ verificato |
| QuickLogViewModel usa viewModelScope → richiede Dispatchers.setMain | ✅ analizzato |

**0 unknown** — si procede.

## Scope
**Size: Standard** — 3 nuovi file test, nessuna nuova dipendenza.

## Pre-mortem
1. **Dispatchers.setMain non resettato** → altri test falliscono. Mitigazione: @AfterEach resetMain.
2. **MockK su classe concreta** → se UseCase ha costruttore non mockable. Mitigazione: preferire istanziazione reale con mock del repository (pattern già usato nel progetto).
3. **isManual default param in logMedication** → MockK potrebbe non matchare `any()` sul parametro default. Mitigazione: passare esplicitamente `any()` in posizione.

## Devil's Advocate
Alternativa: usare Fake repositories hand-written al posto di MockK. Più leggibili ma richiederebbe un nuovo file per fake. MockK è lo standard consolidato nel progetto → si mantiene MockK.

## Approccio
- `TerminateTherapyUseCaseTest`: mock `TherapyRepository`, istanziare `GetTherapyUseCase(repo)` reale, catturare il therapy passato a `updateTherapy` con `slot<Therapy>()` per verificare `isActive=false` e `endDate != null`.
- `AddManualMedicationLogUseCaseTest`: mock `MedicationLogRepository`, verificare con `coVerify` che `status=TAKEN` e `isManual=true`.
- `QuickLogViewModelTest`: `UnconfinedTestDispatcher` come main, 5 test: null user no-op, success, failure, filter active, clearType reset.
