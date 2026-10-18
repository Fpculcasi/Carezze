# THINK — 5.5.3 Evento "Farmaco" nel Quick Log

## Context Saturation Check
- `LogMedicationUseCase(personId, therapyId, medicationId, scheduledTime: Instant, status, userId)` ✅ letta
- `Medication.scheduledTimes: List<String>` → stringhe "HH:00" già calcolate alla creazione ✅
- `QuickLogUiState` + `QuickLogViewModel` constructor ✅ letti
- `TherapyStepContent` → wizard 3-step attuale ✅ letto
- Test file `QuickLogViewModelTest` costruttore + mock ✅ letti
- Nessun unknown → 0 unknown. Procedo.

## Task
Rework del tile "💊 Farmaci" nel QuickLog: mostrare le dosi schedulate di oggi per le terapie attive della persona, con bottone 1-tap "Segna presa" per ciascuna (usa `LogMedicationUseCase`). Mantiene il flusso manuale (una tantum) come CTA secondaria.

## Approccio

### Dati
- Aggiungere `data class ScheduledDose(therapyId, therapyName, medicationId, medicationName, timeLabel, scheduledTime: Instant)` in `QuickLogViewModel.kt`
- Aggiungere a `QuickLogUiState`: `scheduledDoses: List<ScheduledDose>`, `showManualDoseFlow: Boolean`
- `loadTherapies` chiama anche `buildScheduledDoses(therapies)` che flatmappa `therapy.medications[].scheduledTimes` → `ScheduledDose` (oggi + ora locale)

### VM
- Iniettare `LogMedicationUseCase`
- `fun confirmScheduledDose(personId, dose)` → `logMedicationUseCase(personId, dose.therapyId, dose.medicationId, dose.scheduledTime, TAKEN, uid)`
- `fun enterManualDoseFlow()` → `showManualDoseFlow = true`
- `fun exitManualDoseFlow()` → `showManualDoseFlow = false, selectedTherapyId = null, selectedMedicationId = null`
- `clearType()` resetta anche `scheduledDoses` e `showManualDoseFlow`

### UI: TherapyStepContent
- Se `showManualDoseFlow`: mostra il vecchio wizard 3-step + "← Dosi di oggi"
- Altrimenti:
  - therapies vuote → "Nessuna terapia attiva" + "Aggiungi terapia"
  - scheduledDoses vuote (ma terapie attive) → "Nessuna dose pianificata" + "Aggiungi dose extra"
  - else → lista flat raggruppata per terapia: ogni riga [medName · timeLabel] + Button "Segna" + TextButton "+ Aggiungi dose extra"
- Composable `DoseRow` separato per renderizzare ogni dose

### Files toccati (Heavy)
1. `QuickLogViewModel.kt` — ScheduledDose, state, logMedication injection, 3 nuovi metodi, buildScheduledDoses
2. `QuickLogSheet.kt` — TherapyStepContent rework + DoseRow composable + nuovi callback
3. `QuickLogViewModelTest.kt` — 4 test nuovi (confirmScheduledDose success/failure, selectType computes doses, enterManualDoseFlow toggle)

## Pre-mortem
1. Hilt binding `LogMedicationUseCase`: ha `@Inject constructor` + `MedicationLogRepository` già bound → nessun problema atteso
2. Parse di `scheduledTimes` con formato non "HH:00" → uso `toIntOrNull() ?: 0` come guard
3. Conflitto stato `showManualDoseFlow=true` + `selectedTherapyId` stale → `exitManualDoseFlow` resetta entrambi, `clearType` resetta tutto

## Devil's Advocate
Alternativa: lasciare il wizard 3-step e aggiungere solo le etichette orario nell'ultimo step (informative). Più semplice ma non soddisfa "1-tap conferma" del requisito R3 né "LogMedicationUseCase" con scheduledTime corretto. Scartata.
