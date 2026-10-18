# TASKS — 5.5 UI: Storico Lista

**Delivers:** HistoryListScreen con feed cronologico degli ultimi 30 giorni raggruppato per giorno, raggiungibile dalla Dashboard.

## CDM Criteria (Standard)
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` exits 0

## Checklist
- [x] `ui/history/HistoryViewModel.kt` — @HiltViewModel + SavedStateHandle, osserva 30 giorni
- [x] `ui/history/HistoryListScreen.kt` — LazyColumn con stickyHeader per data, back navigation
- [x] `./gradlew :app:compileDebugKotlin` → 0 errori — PASS 2026-09-01
