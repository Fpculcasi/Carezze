# THINK — 5.5.2 Quick Log per-persona

## Context Saturation Check
- ✅ DashboardScreen.kt letto: FAB globale in `Scaffold.floatingActionButton`, `onOpenQuickLog: () -> Unit`
- ✅ QuickLogSheet.kt letto: header `"Registra evento per $personId"` (mostra ID, non nome)
- ✅ PersonCard: Row con Icon+Column, nessun Spacer.weight → aggiungere Spacer + IconButton "+"
- ✅ OQ1 risolto: FAB eliminato; "+" sulla card

0 incognite → nessun blocco.

## Pre-mortem

1. **Signature mismatch a cascata**: `onOpenQuickLog` cambia da `() -> Unit` a `(String) -> Unit` — tutte le chiamate (DashboardScreen, previews) devono aggiornarsi o il build fallisce.
2. **QuickLogSheet instanziata senza nome**: se `persons.find { it.id == quickLogPersonId }` restituisce null (race condition al momento del dismiss), il nome è stringa vuota. Mitigazione: safe default con `?:  ""` e la sheet non viene mostrata se `quickLogPersonId == null`.

## Devil's Advocate
Alternativa: tenere il FAB ma aprire un dialog di selezione persona → più tap, UX peggiore. Scartato.

## Approcci Considerati
- **Scelto**: "+" come IconButton nell'angolo destro della PersonCard; DashboardContent accetta `onOpenQuickLog: (String) -> Unit`.
- PersonCard diventa `ElevatedCard` non cliccabile sul "+" (il click dell'intera card va a history, il "+" usa `stopPropagation` tramite IconButton che ha il proprio click handler — Compose lo gestisce correttamente senza stopPropagation esplicito).

## Files da toccare
1. `app/src/main/kotlin/com/carezze/app/ui/dashboard/DashboardScreen.kt`
2. `app/src/main/kotlin/com/carezze/app/ui/dashboard/QuickLogSheet.kt`
