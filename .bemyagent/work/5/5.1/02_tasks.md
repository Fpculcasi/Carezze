# TASKS — 5.1 Domain: ActivityLog

Delivers: ActivityLog domain model + interfaces + use cases compile and can be injected.

## Checklist
- [x] ActivityLog.kt sealed class + enums (no TemperatureUnit redeclaration)
- [x] ActivityLogRepository.kt interface
- [x] LogActivityUseCase.kt
- [x] ObserveActivityLogsUseCase.kt
- [x] ActivityLogRepositoryImpl.kt stub
- [x] FirestoreModule.kt binding added

## CDM
✅ Validation: `./gradlew :app:compileDebugKotlin` exits 0 — PASS 2026-09-01
