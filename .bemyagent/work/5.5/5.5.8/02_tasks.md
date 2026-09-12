---
task: 5.5.8
title: Pulizia — preview duplicate, titolo QuickLogSheet, test unit nuovi ViewModel
---

Delivers: `detekt`, `ktlintCheck`, `testDebugUnitTest` verdi con test per DashboardViewModel e ProfileViewModel; spec ux-rework.md allineata.

## Checklist

- [ ] Creare `DashboardViewModelTest.kt` con test per `filteredPersons` e `toggleViewMode`
- [ ] Creare `ProfileViewModelTest.kt` con test per `authState` (null→SignedOut, anon→Anonymous, auth→Authenticated)
- [ ] Aggiornare `specs/ux-rework.md`: spuntare checkbox "Preview duplicate rimosse" e "titolo sostituito"
- [ ] Aggiornare `06-implementation-plan.md`: task 5.5.8 → done
- [ ] Aggiornare `05-decisions-and-issues.md`: chiudere issue previews/titolo

## ✅ Validation
- `testDebugUnitTest` verde (0 failing)
- `DashboardViewModelTest`: min 5 test per filteredPersons/toggleViewMode
- `ProfileViewModelTest`: min 3 test per authState
