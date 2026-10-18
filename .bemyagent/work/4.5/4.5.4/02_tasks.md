---
name: 4.5.4-tasks
description: TASK checklist — TherapyDetailScreen FAB + overflow menu Termina/Elimina
metadata:
  type: task
---

# TASK — 4.5.4

**Delivers:** l'utente apre `TherapyDetailScreen`, vede un FAB "Modifica" che naviga a `EditTherapyScreen`, e un menu overflow con "Termina" (dialog soft → terapia COMPLETED, naviga indietro) e "Elimina" (dialog hard con warning dati → hard delete, naviga indietro).

## Checklist

- [ ] `TherapyViewModel`: inject `TerminateTherapyUseCase`, add `terminateTherapy(personId, therapyId)` fire-and-forget
- [ ] `TherapyDetailScreen`: add param `onNavigateToEdit: () -> Unit`; FAB Edit; overflow menu; dialog Termina (solo se `isActive`); dialog Elimina
- [ ] `AppNavigation`: passa `onNavigateToEdit = { navController.navigate(EditTherapy(route.personId, route.therapyId)) }` a `TherapyDetailScreen`

## CDM — Validation

| Criterio | Metodo |
|---|---|
| `compileDebugKotlin` BUILD SUCCESSFUL | build Gradle |
| `TherapyDetailScreen` ha FAB che chiama `onNavigateToEdit` | grep source |
| Overflow menu mostra "Termina" solo se `therapy.isActive == true` | grep source |
| Dialog Elimina contiene testo warning perdita dati | grep source |
