# TASK — 5.5.7: Filtro Home — card singola su selezione + search bar

**Delivers:** selezionata una persona (chip) → Home mostra solo la sua card; digitando nella search bar → cards filtrate per nome/nickname in tempo reale.

## Checklist

- [ ] **ViewModel** — aggiungere `searchQuery: MutableStateFlow<String>`, `filteredPersons: StateFlow`, `setSearchQuery()`, aggiornare `recentLogs` per usare `filteredPersons`
- [ ] **Screen** — `DashboardContent` riceve `filteredPersons`, `searchQuery`, `onSearchQueryChange`; `CardView`/`FeedView` usano `filteredPersons`
- [ ] **Search bar** — composable `PersonSearchBar` tra `PersonFilterRow` e content area
- [ ] **Empty state** — `CardView` distingue "nessuna persona" da "nessun risultato"
- [ ] **Preview** — aggiornare firma `DashboardContent` nella Preview

## ✅ Validation

- `./gradlew assembleDebug` compila senza errori
- Chip selezionato → `filteredPersons` contiene solo quella persona
- Search su nome/nick → `filteredPersons` filtrata correttamente
- `ktlintCheck` + `detekt` verdi
