---
name: 4.5.4-think
description: THINK — TherapyDetailScreen FAB + overflow menu Termina/Elimina
metadata:
  type: task
---

# THINK — 4.5.4

## Task
`TherapyDetailScreen` — FAB "Modifica" apre `EditTherapyScreen`; menu overflow con "Termina" (dialog soft, naviga indietro) e "Elimina" (dialog hard con warning perdita dati, naviga indietro).

## Context Saturation Check

| Item | Status |
|---|---|
| `TerminateTherapyUseCase` signature | ✅ `invoke(personId, therapyId): Result<Unit>` — `TherapyViewModel` non ce l'ha ancora iniettata |
| `DeleteTherapyUseCase` già iniettata nel VM | ✅ `fun deleteTherapy(personId, therapyId)` fire-and-forget |
| Firma attuale di `TherapyDetailScreen` | ✅ `personId, therapyId, onNavigateBack` — manca `onNavigateToEdit` |
| Route `EditTherapy(personId, therapyId)` in `AppNavigation` | ✅ esiste, già wired su `composable<EditTherapy>` |
| `therapy.isActive` / `therapy.status` | ✅ `TherapyStatus.ACTIVE` quando `isActive=true` |

0 incognite — si procede.

## Sizing
3 file (TherapyDetailScreen, TherapyViewModel, AppNavigation) — nessuna nuova dipendenza → **Standard**

## Pre-mortem
1. **`TerminateTherapyUseCase` non iniettata** → fix: aggiungerla al costruttore `@HiltViewModel`; Hilt la risolve automaticamente.
2. **Stato dialog perso su configuration change** → fix: `rememberSaveable` invece di `remember` per i flag booleani dei dialog.
3. **Back navigation prima del completamento async** → accettabile: pattern fire-and-forget già usato per `deleteTherapy`; `onNavigateBack()` chiamato subito dopo enqueue.

## Devil's Advocate
Alternativa: `Channel<UiEvent>` nel VM per navigazione post-azione (più robusto, testabile). Overkill per scope di questo task e incongruente con il pattern già adottato — scartata.
