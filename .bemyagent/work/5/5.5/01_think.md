# THINK — 5.5 UI: Storico Lista

## Context Saturation Check
| Item | Status |
|---|---|
| `ObserveActivityLogsUseCase(personId, from, to)` | ✓ |
| `SavedStateHandle["personId"]` pattern | ✓ da 04-tech-stack (standard Hilt nav) |
| LazyColumn sticky header via `stickyHeader {}` | ✓ Compose API |

**Unknowns: 0** — proceed.

## Pre-mortem
1. **personId nullo in SavedStateHandle** → `checkNotNull()` causa crash; il NavGraph deve garantire che il parametro sia sempre presente.
2. **Sticky headers con lazy column** → richiedono `@OptIn(ExperimentalFoundationApi::class)`.

## Approach
1. `HistoryViewModel`: osserva gli ultimi 30 giorni, raggruppa per data
2. `HistoryListScreen`: LazyColumn con stickyHeader per data
