# THINK — 4.3 Data: TherapyRepositoryImpl + MedicationLogRepositoryImpl

## Context Saturation Check
| Item | Status |
|---|---|
| Firestore path for therapies: `persons/{personId}/therapies/{therapyId}` | ✓ code-map |
| Firestore path for medicationLogs: `persons/{pid}/therapies/{tid}/medicationLogs/{logId}` | ✓ code-map |
| Pattern: `callbackFlow` + `whereArrayContains("memberIds", userId)` | ✓ from PersonRepositoryImpl |
| `Medication` is stored as `List<Map>` inside the therapy document | ✓ code-map |
| `TherapyDuration` serialised as `Map{type,days}` in Firestore | ✓ code-map |
| `startDate` / `Medication.startDate`: stored as Firestore `Timestamp`, domain uses `LocalDate` | ✓ must convert via `Timestamp.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()` |
| `MedicationLogRepository` needs `DI binding` in `FirestoreModule` | ✓ will add |

**Unknowns: 0** — proceed.

## Pre-mortem
1. `Timestamp` → `LocalDate` conversion: wrong timezone → off-by-one day. Mitigation: always use `ZoneOffset.UTC` consistently for both serialisation and deserialisation.
2. `medications` is a `List<Map>` in Firestore — unsafe cast. Mitigation: guard each map field with `as? Type ?: return@mapNotNull null` pattern from PersonRepositoryImpl.
3. Missing `MedicationLogRepositoryImpl` DI binding → Hilt fails if any future use case injects it. Mitigation: add binding alongside TherapyRepository in same module update.

## Devil's Advocate
**Alternative:** Use Room for local cache like PersonRepositoryImpl pattern. Checking PersonRepositoryImpl — it actually does NOT use Room (Firestore-only with snapshot listener). So same pattern applies. No Room for therapies.

## Approach
- Replace TODO stubs in `TherapyRepositoryImpl` with full Firestore implementation
- Nested subcollection helper: `personsCollection().document(personId).collection("therapies")`
- `observeTherapies(personId)`: listen on `persons/{personId}/therapies` with no filter (user is already a person member)
- Add `MedicationLogRepositoryImpl` (new file)
- Update `FirestoreModule` to bind `MedicationLogRepository`
- Add `LogMedicationUseCase` + `ObservePendingDosesUseCase` (domain use cases that inject MedicationLogRepository)

**Files touched:** TherapyRepositoryImpl.kt (modify), MedicationLogRepositoryImpl.kt (new), FirestoreModule.kt (update), LogMedicationUseCase.kt (new), ObservePendingDosesUseCase.kt (new) = 5 files → Heavy
