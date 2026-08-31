# TASKS — 4.4 UI: Wizard Aggiungi Terapia + Dettaglio Terapia

**Delivers:** User can navigate to a Person's detail, see their therapies in a list, tap to add a new therapy via a 2-step wizard (name/dates on step 1, medications on step 2), and open a therapy detail to see the medication schedule.

## CDM Criteria (Heavy)
- 🎯 **Drift**: navigation routes not wired → screens unreachable; TherapyViewModel not scoped correctly → wrong data shown
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL
- 🔄 **Pivot**: if Hilt ViewModel scoping with nested nav graph is complex → use `hiltViewModel()` default (activity scope) and accept that therapies reload on nav

## Checklist
- [ ] `ui/therapy/TherapyViewModel.kt` — therapies flow, create/delete, wizard form state
- [ ] `ui/therapy/AddTherapyScreen.kt` — 2-step wizard composable
- [ ] `ui/therapy/TherapyDetailScreen.kt` — detail + medication list
- [ ] `ui/person/PersonDetailScreen.kt` — replace TODO stub with therapy list
- [ ] `ui/navigation/AppNavigation.kt` — add AddTherapy + TherapyDetail routes
- [ ] Compile check passes
