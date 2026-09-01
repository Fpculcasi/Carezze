# THINK — 5.3 UI: Dashboard (card/feed toggle + filtro persona)

## Context Saturation Check
| Item | Status |
|---|---|
| Package root (`com.fpculcasi.carezze`) | ✓ |
| Pattern ViewModel: `authRepository.currentUser?.id`, `stateIn(WhileSubscribed(5_000))` | ✓ PersonViewModel |
| `ObservePersonsUseCase(userId)` → `Flow<List<Person>>` | ✓ |
| `ObserveActivityLogsUseCase(personId, from, to)` → `Flow<List<ActivityLog>>` | ✓ creata in 5.1 |
| Stub DashboardScreen.kt esiste, usa `Scaffold + TopAppBar` | ✓ |
| Navigation: `onNavigateToSettings`, `onNavigateToPersons` già presenti | ✓ |

**Unknowns: 0** — proceed.

## Pre-mortem
1. **combine con lista di Flow vuota** → `combine(emptyList())` crasha. Mitigation: guard `if (relevant.isEmpty()) flowOf(emptyList())`.
2. **selectedPersonId non reattivo in flatMapLatest** → usare `combine(persons, selectedPersonId) { ... }.flatMapLatest { ... }` per reagire a entrambi.
3. **Import hiltViewModel in preview** → le preview non usano il ViewModel reale; usare `DashboardContent(...)` statefull separata con parametri.

## Devil's Advocate
**Alternativa:** aggregare i log lato Firestore con una collection-group query invece di N query per persona. Rifiutato: richiederebbe un indice collection-group e il Firestore schema non lo prevede ora; l'approccio N-flow-combine è corretto per il numero di persone atteso (2–5).

## Approach
1. `DashboardViewModel.kt` — combine persons + selectedPersonId → flatMapLatest sui log
2. `DashboardScreen.kt` — card view / feed view, FilterChip row, FAB (showSheet state)
3. `AppNavigation.kt` — aggiungi route HistoryList + HistoryCalendar (stub screen per ora)
