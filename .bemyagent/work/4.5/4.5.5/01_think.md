# THINK — 4.5.5 TherapyLogScreen

## Context Saturation Check (0-1 unknown → proceed)

- ✅ `AddManualMedicationLogUseCase` firma: `personId, therapyId, medicationId, takenAt: Instant, userId`
- ✅ `ObserveLogsUseCase` + `logsFor()` già nel ViewModel
- ✅ `MedicationLog` ha campo `isManual: Boolean = false`
- ✅ Pattern navigazione type-safe (`@Serializable data class`) confermato
- ✅ Pattern date: `OutlinedTextField` testo (no DatePickerDialog) — usato in AddTherapyScreen
- Assunzione: entry point da `TherapyDetailScreen` via voce overflow "Storico dosi"

## Delivers

`TherapyLogScreen` raggiungibile da TherapyDetailScreen: mostra lista dosi (TAKEN/SKIPPED/PENDING) con nome farmaco, data/ora, badge stato; FAB apre dialog con selezione farmaco + data + ora → salva log manuale.

## Pre-mortem

1. **ID farmaco non trovato nel log** → nome mostrato come "Farmaco sconosciuto" (fallback sicuro)
2. **userId null** → `addManualLog` fa early return senza side-effect (pattern già in use)
3. **Therapy non ancora caricata** → log screen mostra lista vuota, dropdown farmaci vuoto con messaggio

## Devil's Advocate

Alternativa: Dialog bottom sheet (come QuickLog). Rifiutato — TherapyLogScreen è una schermata navigabile, non un overlay, coerente con il design delle altre schermate dettaglio.

## Files tocchi

| File | Azione |
|---|---|
| `ui/therapy/TherapyLogScreen.kt` | nuovo |
| `ui/therapy/TherapyViewModel.kt` | modifica — inject `AddManualMedicationLogUseCase`, add `addManualLog()` |
| `ui/navigation/AppNavigation.kt` | modifica — route `TherapyLog` + composable |
| `ui/therapy/TherapyDetailScreen.kt` | modifica — add `onNavigateToLog` + voce overflow |

**Size: Heavy** (4 file, logica UI non banale per il dialog). CDM completo richiesto.
