# TASKS — 4.7 Unit tests: Use Cases Terapia + logica schedule

**Delivers:** All M4 use cases and ScheduleCalculator covered by unit tests — green suite.

## CDM Criteria (Heavy)
- 🎯 **Drift**: tests pass with mocked repo but fail on real Firestore types → only test domain-pure logic
- ✅ **Validation**: `./gradlew :app:test` (unit test suite) → BUILD SUCCESSFUL, all tests pass
- 🔄 **Pivot**: if `Flow` mocking with MockK causes issues → replace with `flowOf()` stubs as test doubles

## Checklist
- [ ] `ScheduleCalculatorTest` — 6 cases
- [ ] `CreateTherapyUseCaseTest` — 2 cases
- [ ] `GetTherapyUseCaseTest` — 2 cases
- [ ] `UpdateTherapyUseCaseTest` — 2 cases
- [ ] `DeleteTherapyUseCaseTest` — 2 cases
- [ ] `ObserveTherapiesUseCaseTest` — 1 case
- [ ] `LogMedicationUseCaseTest` — 2 cases
- [ ] `./gradlew :app:test` → all pass
