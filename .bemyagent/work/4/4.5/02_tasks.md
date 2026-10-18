# TASKS — 4.5 Progress: barra avanzamento + contatore rimanenti

**Delivers:** TherapyDetailScreen shows a progress bar + "X dosi rimanenti" counter computed from MedicationLog records in real-time.

## CDM Criteria (Standard)
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL

## Checklist
- [ ] `TherapyViewModel.kt` — add `logsFor(personId, therapyId)` returning `StateFlow<List<MedicationLog>>`; add `progressFor(therapy, logs)` function returning 0f..1f
- [ ] `TherapyDetailScreen.kt` — add `TherapyProgressSection` composable (LinearProgressIndicator + remaining counter text); wire into detail content
- [ ] Compile check passes
