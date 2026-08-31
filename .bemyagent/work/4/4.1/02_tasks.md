# TASKS — 4.1 Domain: Therapy, Medication, TherapyDuration + Use Cases

**Delivers:** App compiles (`compileDebugKotlin` passes) with a fully injectable therapy domain layer — models, repository interface, use cases, and DI-bound stub — ready for TDD tests in 4.7.

## CDM Criteria (Heavy)
- 🎯 **Drift**: domain models diverge from the Firestore schema in `03-code-map.md` (field names, types, nesting)
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` exits 0
- 🔄 **Pivot**: if `java.time.LocalDate` causes unexpected build error → replace with `String` (ISO-8601) and log the trade-off; re-evaluate in 4.3

## Checklist
- [ ] `domain/model/Therapy.kt` — Therapy, TherapyDuration (Indefinite/Fixed), Medication, MedicationLog, MedicationStatus
- [ ] `domain/repository/TherapyRepository.kt` — interface with 5 methods
- [ ] `domain/repository/MedicationLogRepository.kt` — interface stub (closed domain ring)
- [ ] `domain/usecase/therapy/ObserveTherapiesUseCase.kt`
- [ ] `domain/usecase/therapy/GetTherapyUseCase.kt`
- [ ] `domain/usecase/therapy/CreateTherapyUseCase.kt`
- [ ] `domain/usecase/therapy/UpdateTherapyUseCase.kt`
- [ ] `domain/usecase/therapy/DeleteTherapyUseCase.kt`
- [ ] `data/repository/TherapyRepositoryImpl.kt` — DI stub (TODO bodies)
- [ ] `di/FirestoreModule.kt` — add TherapyRepository @Binds binding
- [ ] `./gradlew :app:compileDebugKotlin` → verify 0 errors
