# TASKS — 5.8 Test unitari: Use Cases log attività

**Delivers:** 4 test unitari verdi per i due use case di ActivityLog.

## CDM Criteria (Standard)
- ✅ **Validation**: `./gradlew :app:test --tests "*.activity.*"` exits 0; 4 test passano

## Checklist
- [x] `domain/usecase/activity/LogActivityUseCaseTest.kt` — 2 test (success + failure)
- [x] `domain/usecase/activity/ObserveActivityLogsUseCaseTest.kt` — 2 test (emits list, emits empty)
- [x] `./gradlew :app:testDebugUnitTest --tests "com.fpculcasi.carezze.domain.usecase.activity.*"` → 4/4 PASS 2026-09-01
