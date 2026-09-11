# TASK — 5.5.3 Evento "Farmaco" nel Quick Log

Delivers: dal QuickLog si vede la lista delle dosi schedulate di oggi per la persona selezionata; un tap su "Segna presa" confirma la dose (status TAKEN, scheduledTime corretto); un CTA secondario "Aggiungi dose extra" apre il flusso manuale preesistente.

## Checklist

- [x] `QuickLogViewModel.kt` — `ScheduledDose` data class + `scheduledDoses`/`showManualDoseFlow` in `QuickLogUiState`
- [x] `QuickLogViewModel.kt` — iniettare `LogMedicationUseCase` + `fun confirmScheduledDose` + `fun enterManualDoseFlow` + `fun exitManualDoseFlow`
- [x] `QuickLogViewModel.kt` — `buildScheduledDoses(therapies)` + aggiornare `loadTherapies` + `clearType`
- [x] `QuickLogSheet.kt` — aggiornare firma `TherapyStepContent` + riscrivere corpo (scheduled view + manual flow toggle)
- [x] `QuickLogSheet.kt` — composable `DoseRow` + aggiornare call-site in `QuickLogSheet`
- [x] `QuickLogSheet.kt` — aggiornare preview esistenti per nuovi parametri
- [x] `QuickLogViewModelTest.kt` — `logMedication` mock in setUp + 4 test nuovi (fix assertion therapyId "t1")
- [x] Build verde: `compileDebugKotlin` ✅ | `ktlintCheck` ✅ | `detekt` ✅ | `testDebugUnitTest` ✅ — BUILD SUCCESSFUL 42/42

## CDM

### ✅ Validation
`JAVA_HOME="/c/Program Files/Android/Android Studio/jbr" ./gradlew :app:compileDebugKotlin :app:ktlintCheck detekt :app:testDebugUnitTest`
→ BUILD SUCCESSFUL, 0 test failures
