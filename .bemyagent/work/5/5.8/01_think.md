# THINK — 5.8 Test unitari: Use Cases log attività

## Context Saturation Check
| Item | Status |
|---|---|
| Pattern test: JUnit 5 + mockk + `runTest` | ✓ da `LogMedicationUseCaseTest.kt` |
| `LogActivityUseCase(personId, log)` → `Result<ActivityLog>` | ✓ |
| `ObserveActivityLogsUseCase(personId, from, to)` → `Flow<List<ActivityLog>>` | ✓ |
| `turbine` disponibile nel progetto | da verificare — se assente, usare `first()` |

**Unknowns: 0** (fallback a `first()` se turbine assente) — proceed.

## Approach
- `LogActivityUseCaseTest`: 2 test (success + failure) — pattern identico a `LogMedicationUseCaseTest`
- `ObserveActivityLogsUseCaseTest`: 2 test usando `flowOf()` come mock e `.first()` per collect
