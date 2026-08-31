# TASKS — 4.3 Data: TherapyRepositoryImpl + MedicationLogRepositoryImpl

**Delivers:** Therapies and medication logs read/write to Firestore with real-time snapshot listeners — fully replacing the 4.1 stubs.

## CDM Criteria (Heavy)
- 🎯 **Drift**: `Timestamp` ↔ `LocalDate` conversion uses wrong timezone; `medications[]` map deserialization silently drops records
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL
- 🔄 **Pivot**: if nested subcollection query fails at runtime (not detectable at compile time) → log the issue in decisions and fall back to top-level `therapies` collection with `personId` field filter

## Checklist
- [ ] `TherapyRepositoryImpl.kt` — replace TODO stubs with full Firestore impl
  - [ ] `personsCollection()` helper → `persons/{personId}/therapies`
  - [ ] `observeTherapies(personId)` — callbackFlow on subcollection
  - [ ] `getTherapy(therapyId)` — UNSUPPORTED without personId; signature kept, throw meaningful error
  - [ ] `createTherapy(...)` — UUID docId, write medications[] as List<Map>, Timestamp for dates
  - [ ] `updateTherapy(therapy)` — SetOptions.merge()
  - [ ] `deleteTherapy(therapyId)` — requires personId; adapt signature or store personId in therapy
  - [ ] `toDomain()` extension on DocumentSnapshot
- [ ] `MedicationLogRepositoryImpl.kt` — new file, Firestore subcollection under therapy
- [ ] `di/FirestoreModule.kt` — add MedicationLogRepository @Binds
- [ ] `domain/usecase/therapy/LogMedicationUseCase.kt` — new
- [ ] `domain/usecase/therapy/ObservePendingDosesUseCase.kt` — new (delegates to MedicationLogRepository)
- [ ] Compile check passes
