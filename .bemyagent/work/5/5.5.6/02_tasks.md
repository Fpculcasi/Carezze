# TASKS — 5.5.6 Distinzione persone: colore locale

**Delivers:** colore locale per-persona visibile in card, chip, feed e lista Persone; picker in PersonDetail.

## CDM
- ✅ Validation: `testDebugUnitTest` PASS; colore renderizzato in card/chip/feed; persiste al riavvio

## Checklist

- [ ] `app/build.gradle.kts` — aggiunge `datastore-preferences:1.1.1`
- [ ] `data/local/PersonColorStore.kt` — DataStore wrapper con `observeColorIndex` + `setColorIndex`
- [ ] `domain/usecase/person/ObservePersonColorUseCase.kt`
- [ ] `domain/usecase/person/SetPersonColorUseCase.kt`
- [ ] `ui/theme/PersonColors.kt` — palette 8 colori + `personColor(index)` helper
- [ ] `ui/person/PersonViewModel.kt` — inject color use cases; `personColors: StateFlow<Map<String,Int>>`; `setPersonColor()`
- [ ] `ui/dashboard/DashboardViewModel.kt` — inject `ObservePersonColorUseCase`; `personColors: StateFlow<Map<String,Int>>`
- [ ] `ui/dashboard/DashboardScreen.kt` — propagate colors; dot in FilterChip; tint icon in PersonCard; dot in FeedItem
- [ ] `ui/person/PersonDetailScreen.kt` — color picker row (8 swatches) + `currentColorIndex` / `onSetPersonColor`
- [ ] `ui/person/PersonListScreen.kt` — colored dot `leadingContent` in PersonItem
- [ ] `03-code-map.md` — aggiornamento Use Cases persona + palette
