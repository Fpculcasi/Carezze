# THINK — 5.4 UI: Quick Log Bottom Sheet

## Context Saturation Check
| Item | Status |
|---|---|
| `LogActivityUseCase(personId, ActivityLog)` → `Result<ActivityLog>` | ✓ |
| `ModalBottomSheet` disponibile in Material3 | ✓ |
| `authRepository.currentUser?.id` per userId | ✓ |
| 7 tipi di ActivityLog con campi diversi | ✓ da `ActivityLog.kt` |

**Unknowns: 0** — proceed.

## Pre-mortem
1. **personId vuoto se non selezionata persona** — il sheet riceve `personId: String`; chiamante deve garantire un valore non vuoto (primo della lista o selezionato).
2. **Form state complesso** — 7 tipi con campi diversi; usare `QuickLogUiState` con campi nullable per tutti i tipi, non un sealed state per ogni tipo.
3. **ModalBottomSheet experimental** — `@OptIn(ExperimentalMaterial3Api::class)` richiesto.

## Approach
- `QuickLogViewModel`: `selectedType`, `isLoading`, `isSaved` — un fun per tipo
- `QuickLogSheet`: griglia iniziale di pulsanti tipo → al tap mostra form contestuale
