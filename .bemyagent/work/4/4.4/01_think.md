# THINK — 4.4 UI: Wizard Aggiungi Terapia + Dettaglio Terapia

## Context Saturation Check
| Item | Status |
|---|---|
| Navigation pattern: typed routes `@Serializable data class`, `toRoute<T>()` | ✓ from AppNavigation.kt |
| ViewModel pattern: `@HiltViewModel`, `authRepository.currentUser?.id`, `stateIn` | ✓ from PersonViewModel.kt |
| PersonDetailScreen has TODO stub for M4 therapies | ✓ line 69 |
| Compose + Material3 version: import pattern confirmed | ✓ from PersonDetailScreen.kt |
| Package: `com.fpculcasi.carezze.ui.therapy` | ✓ code-map |

**Unknowns: 0** — proceed.

## Pre-mortem
1. TherapyViewModel injected into PersonDetail creates circular dependency or wrong scope → Mitigation: TherapyViewModel is separate from PersonViewModel; PersonDetailScreen gets therapy list via TherapyViewModel scoped to navGraph or hiltViewModel().
2. Multi-step wizard state lost on back press → Mitigation: wizard state lives in ViewModel (survives recomposition), back press goes to previous step not previous screen.
3. Date picker integration complexity → Mitigation: use simple `OutlinedTextField` with manual ISO date input for now (date picker in M9 polish). Mark with TODO.

## Devil's Advocate
**Alternative:** Single-screen form instead of wizard. Rejected: multiple medications make a single-screen form too dense. Wizard is explicitly specified in task.

## Approach
- `TherapyViewModel`: observes therapies for a personId, creates/deletes therapies, manages wizard form state
- `AddTherapyScreen`: 2-step wizard (Step 1: name+date+duration, Step 2: add medications)
- `TherapyDetailScreen`: shows therapy + medications list + "Mark as taken" button per medication
- `PersonDetailScreen`: replace TODO stub with therapy list using `LazyColumn`
- `AppNavigation`: add `AddTherapy(personId)` and `TherapyDetail(personId, therapyId)` routes

**Files: 5 → Heavy**
