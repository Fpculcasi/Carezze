# TASKS — 5.3 UI: Dashboard (card/feed toggle + filtro persona)

**Delivers:** DashboardScreen operativa con card e feed view toggle, filtro per persona via FilterChip, FAB per Quick Log, navigazione a History.

## CDM Criteria (Heavy)
- 🎯 **Drift**: dashboard mostra dati stale o non reagisce al cambio di persona selezionata
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` exits 0; DashboardContent ha preview funzionante
- 🔄 **Pivot**: se la logica multi-flow diventa troppo complessa, spostare l'aggregazione log in un UseCase `AggregateActivityLogsUseCase`

## Checklist
- [x] `ui/dashboard/DashboardViewModel.kt` — @HiltViewModel con persons, recentLogs (combine + flatMapLatest), selectedPersonId, viewMode
- [x] `ui/dashboard/DashboardScreen.kt` — refactor stub: TopAppBar, FilterChip row, card/feed toggle, FAB
- [x] `ui/navigation/AppNavigation.kt` — aggiungi HistoryList + HistoryCalendar routes
- [x] `./gradlew :app:compileDebugKotlin` → 0 errori — PASS 2026-09-01
