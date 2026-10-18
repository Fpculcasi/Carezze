# TASKS — 4.5.5 TherapyLogScreen

**Delivers:** TherapyLogScreen raggiungibile e funzionante — lista dosi con nome farmaco + badge stato, FAB che apre dialog per log manuale con selezione farmaco + data + ora.

## CDM

- 🎯 **Drift**: aggiungere log al therapy/person ID sbagliato; navigation non cablata; dialog che non salva
- ✅ **Validation**: `./gradlew assembleDebug` verde; TherapyLogScreen importata in AppNavigation; `addManualLog` chiama `AddManualMedicationLogUseCase`; nessun crash su Therapy con 0 log
- 🔄 **Pivot**: se `@ExperimentalMaterial3Api` rompe il build → usare solo `OutlinedTextField` per data+ora (già nel piano)

## Checklist

- [ ] 1. Inject `AddManualMedicationLogUseCase` in `TherapyViewModel` + metodo `addManualLog()`
- [ ] 2. Route `@Serializable data class TherapyLog(val personId: String, val therapyId: String)` in AppNavigation
- [ ] 3. `TherapyLogScreen.kt` — schermata con lista logs (LazyColumn) + empty state + FAB
- [ ] 4. `AddManualLogDialog` in TherapyLogScreen — selezione farmaco + data + ora + conferma
- [ ] 5. Wiring navigation: composable TherapyLog in inner NavHost
- [ ] 6. `TherapyDetailScreen` — aggiunge `onNavigateToLog` callback + voce overflow "Storico dosi"
- [ ] 7. `assembleDebug` verde
