# VERIFY — 3.5: Unit Tests Person Use Cases

## CDM Criteria

### ✅ Validation: test suite passa

**Evidence:** `./gradlew :app:testDebugUnitTest --tests "com.fpculcasi.carezze.domain.usecase.person.*"` → `BUILD SUCCESSFUL in 13s` (2026-08-31)
Report: `app/build/reports/tests/testDebugUnitTest/classes/` → 5 file HTML presenti:
- `com.fpculcasi.carezze.domain.usecase.person.CreatePersonUseCaseTest.html`
- `com.fpculcasi.carezze.domain.usecase.person.DeletePersonUseCaseTest.html`
- `com.fpculcasi.carezze.domain.usecase.person.GetPersonUseCaseTest.html`
- `com.fpculcasi.carezze.domain.usecase.person.ObservePersonsUseCaseTest.html`
- `com.fpculcasi.carezze.domain.usecase.person.UpdatePersonUseCaseTest.html`

## Verdict: PASS
