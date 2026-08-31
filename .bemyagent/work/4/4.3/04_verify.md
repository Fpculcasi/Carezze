# VERIFY — 4.3 TherapyRepositoryImpl + MedicationLogRepositoryImpl

## CDM Results

### 🎯 Drift check — Timestamp↔LocalDate + medications[] deserialization
- `LocalDate.toTimestamp()` uses `atStartOfDay().toInstant(ZoneOffset.UTC)` — consistent with `Timestamp.toLocalDate()` which also uses `ZoneOffset.UTC` → no timezone drift ✓
- `medications[]` deserialization guards each field with `?: return@mapNotNull null` — corrupt entries are silently dropped, not crashing ✓

### ✅ Validation — compile
Evidence: `./gradlew :app:compileDebugKotlin` → `BUILD SUCCESSFUL in 8s` ✓

### 🔄 Pivot — subcollection query not testable at compile time
Subcollection path correctness (`persons/{pid}/therapies/{tid}`) is structurally correct per code-map. Runtime validation deferred to integration tests in M10.

## Verdict: PASS
