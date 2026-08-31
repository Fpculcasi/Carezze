# TASK — 3.3: UI Person

**Delivers:** utente può vedere la lista delle Persone, aggiungere una nuova Persona e modificarla — flusso completo demoabile dalla Dashboard.

## CDM — Drift / Validation / Pivot
- 🎯 Drift: aggiungere logica di business nei Composable invece del ViewModel
- ✅ Validation: `./gradlew :app:compileDebugKotlin` → 0 errori
- 🔄 Pivot: se navigation args con nullable String fallisce → usare stringa vuota come sentinel per "create"

## Checklist
- [ ] `ui/person/PersonViewModel.kt`
- [ ] `ui/person/PersonListScreen.kt`
- [ ] `ui/person/EditPersonScreen.kt`
- [ ] `ui/person/PersonDetailScreen.kt` (stub)
- [ ] `ui/navigation/AppNavigation.kt` — PersonList, EditPerson, PersonDetail routes
- [ ] `ui/dashboard/DashboardScreen.kt` — pulsante Persone
