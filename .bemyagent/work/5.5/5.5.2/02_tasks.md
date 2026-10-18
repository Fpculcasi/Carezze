# TASKS — 5.5.2 Quick Log per-persona

**Delivers:** Toccando "+" sulla card di una persona si apre il Quick Log Sheet con header "Registra evento per [nome/nick]" senza ambiguità sulla persona; il FAB globale è rimosso.

## CDM Criteria (Standard)
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` exits 0

## Checklist

### DashboardScreen.kt
- [x] Rimuovere `FloatingActionButton` da `Scaffold` in `DashboardContent`
- [x] Cambiare `onOpenQuickLog: () -> Unit` → `onOpenQuickLog: (personId: String) -> Unit` in `DashboardContent`
- [x] Aggiornare `CardView` per accettare e passare `onOpenQuickLog: (personId: String) -> Unit`
- [x] Aggiornare `PersonCard` per accettare `onOpenQuickLog: () -> Unit` + aggiungere `Modifier.weight(1f)` su Column + `IconButton` "+" nel Row
- [x] `DashboardScreen` stateful: sostituire `showQuickLog: Boolean` con `quickLogPersonId: String?`
- [x] Passare personName (nickname ?: name) a `QuickLogSheet`
- [x] Rimuovere preview duplicate `DashboardContentPreview2` e `DashboardContentPreviewQuickLog`
- [x] Aggiornare `DashboardContentPreview` con nuova firma `onOpenQuickLog`

### QuickLogSheet.kt
- [x] Aggiungere `personName: String` al parametro di `QuickLogSheet`
- [x] Sostituire header `"Registra evento per $personId"` → `"Registra evento per $personName"`

### Verifica build
- [x] `./gradlew :app:compileDebugKotlin :app:ktlintCheck detekt :app:testDebugUnitTest` → BUILD SUCCESSFUL
