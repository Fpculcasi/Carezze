# THINK — 4.7 Unit tests: Use Cases Terapia + logica schedule (TDD)

## Context Saturation Check
| Item | Status |
|---|---|
| Test stack: JUnit 5 + MockK + `runTest` (kotlinx-coroutines-test) | ✓ from existing tests |
| Pattern: `mockk<Repository>()`, `coEvery { ... } returns Result.success(...)` | ✓ from CreatePersonUseCaseTest |
| Test path: `app/src/test/kotlin/com/carezze/app/` | ✓ confirmed |
| `ScheduleCalculator` is a pure object — no mock needed, direct test | ✓ |
| Use cases to test: Create, Get, Update, Delete, ObserveTherapies, LogMedication | ✓ |

**Unknowns: 0** — proceed.

## Pre-mortem
1. `observeTherapies` returns `Flow<List<Therapy>>` — need `flowOf(...)` stub not `coEvery`. Mitigation: use `every { repo.observeTherapies(any()) } returns flowOf(list)` (not `coEvery` — it's not a suspend function).
2. `ScheduleCalculator.computeScheduledTimes` edge case: non-divisible frequencyHours (e.g. 7) — `floor(24/7)=3` doses at 08:00, 15:00, 22:00. Test this case.
3. JUnit 5 requires `junit-jupiter` engine in test runner config — already configured since existing tests pass.

## Test coverage plan
- `ScheduleCalculatorTest` — 5 cases: 24h, 8h, 6h, 12h, 0h (exception), >24h
- `CreateTherapyUseCaseTest` — success, failure
- `GetTherapyUseCaseTest` — success, failure
- `UpdateTherapyUseCaseTest` — success, failure
- `DeleteTherapyUseCaseTest` — success, failure
- `ObserveTherapiesUseCaseTest` — emits list
- `LogMedicationUseCaseTest` — success, failure

**Files: 7 test files → Heavy**
