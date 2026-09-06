# TASKS — 4.5.7 QuickLogSheet: accesso rapido alle terapie

**Delivers:** Tile "💊 Farmaci" nel QuickLogSheet + flusso 3-step (terapia→farmaco→azione) demoabile end-to-end dalla Home.

## Checklist

- [ ] **QuickLogViewModel.kt**
  - [ ] Aggiungere `THERAPY` a `ActivityLogType`
  - [ ] Aggiungere a `QuickLogUiState`: `therapies: List<Therapy>`, `selectedTherapyId: String?`, `selectedMedicationId: String?`
  - [ ] Iniettare `ObserveTherapiesUseCase` + `AddManualMedicationLogUseCase`
  - [ ] `selectType(type, personId)` — overload; se THERAPY chiama `loadTherapies(personId)`
  - [ ] `loadTherapies(personId)` — private, observe + filter isActive, aggiorna state
  - [ ] `selectTherapy(id)` / `clearTherapy()`
  - [ ] `selectMedication(id)` / `clearMedication()`
  - [ ] `logMedication(personId, therapyId, medicationId)` — chiama use case con `Instant.now()`, aggiorna `isSaved`
  - [ ] `clearType()` resetta anche therapies/selectedTherapyId/selectedMedicationId

- [ ] **QuickLogSheet.kt**
  - [ ] Aggiungere params: `onNavigateToAddTherapy: (String) -> Unit`, `onNavigateToTherapyLog: (String, String) -> Unit`
  - [ ] Tile "💊 Farmaci" nella griglia
  - [ ] Step terapie (selectedTherapyId == null): lista terapie attive O empty state con pulsante "Aggiungi terapia"
  - [ ] Step farmaci (selectedTherapyId != null, selectedMedicationId == null): lista farmaci + back
  - [ ] Step azione (selectedMedicationId != null): "Segna presa" + "Vai allo storico" + back
  - [ ] Chiamata `viewModel.selectType(THERAPY, personId)` dalla griglia
  - [ ] Aggiornare chiamate esistenti a `viewModel.selectType` (passano solo il type, senza personId)

- [ ] **DashboardScreen.kt**
  - [ ] Aggiungere params: `onNavigateToAddTherapy: (String) -> Unit`, `onNavigateToTherapyLog: (String, String) -> Unit`
  - [ ] Passare i callback a QuickLogSheet con chiusura del sheet prima della navigazione

- [ ] **AppNavigation.kt**
  - [ ] Passare `onNavigateToAddTherapy` e `onNavigateToTherapyLog` al composable Dashboard

## CDM: ✅ Validation
1. `compileDebugKotlin` zero errori
2. 8 tile nella griglia QuickLogSheet
3. Flusso completo "Segna presa" dimette il sheet (isSaved)
4. "Vai allo storico" naviga a TherapyLog e chiude il sheet
5. Empty state con "Aggiungi terapia" naviga a AddTherapy
6. Back navigation funziona a ogni step
