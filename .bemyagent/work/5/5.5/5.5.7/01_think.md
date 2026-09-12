# THINK — [Task 5.5.7: Filtro Home — card singola su selezione + search bar]

## Context
R7 della spec ux-rework: selezionata una persona → in vista card appare solo la sua card; search bar per nome/nickname affianca i FilterChip (OQ4).

### Context Saturation Check
- [x] **File/path target** — `DashboardViewModel.kt` + `DashboardScreen.kt`
- [x] **Comportamento atteso** — chip selezionato → solo quella card visibile; search bar filtra per nome/nickname; entrambi operano in AND
- [x] **Vincoli** — OQ4: search bar affianca i chip, non li sostituisce; nessuna nuova dipendenza
- [x] **Dipendenze** — `filteredPersons` derivato da `persons + selectedPersonId + searchQuery`

## Approaches Considered
- **A (scelto)** — `filteredPersons: StateFlow` nel ViewModel (combine di persons + selectedPersonId + searchQuery); `recentLogs` usa `filteredPersons.flatMapLatest` invece di ricalcolare. Pulito, reattivo, testabile.
- **B** — filtrare solo in UI (DashboardContent), mantenere ViewModel invariato. Pro: nessuna modifica al VM. Contro: i log non vengono filtrati correttamente in FeedView senza cambiare anche `recentLogs`; logica di filtro spalmata tra VM e UI.

## Selected Approach & Risks
Approccio A. Costo stimato: **low**. Rischi:
- `filteredPersons` con chip selezionato + query che non matcha → lista vuota → bisogna distinguere "nessuna persona" da "nessun risultato" in `CardView`. Mitigazione: passare `allPersonsCount: Int` a `CardView`.
- `recentLogs` ora dipende da `filteredPersons` (anziché dalla sua combinazione interna) → ordine di inizializzazione dei val nel ViewModel. Mitigazione: `filteredPersons` dichiarato prima di `recentLogs`.

## Pre-mortem
1. Il ViewModel con `filteredPersons` dichiarato dopo `recentLogs` causa NPE → dichiaro `filteredPersons` esplicitamente prima di `recentLogs`.
2. La search bar non si svuota quando si seleziona un chip → comportamento accettabile (AND filter); non è un bug.
3. Preview non compila per firma cambiata di `DashboardContent` → aggiorno la Preview.

## Devil's Advocate
Alternativa: due StateFlow separati (uno per chip-filter, uno per search), combinati solo in UI. Non superiore: introduce duplicazione e spezza la reattività; l'approccio A è più semplice e lineare.

## Verification Plan
- Build `./gradlew assembleDebug` verde
- Selezione chip → CardView mostra solo quella card (visibile nel Preview)
- Search "Vic" senza chip → solo persone con "Vic" nel nome/nick
- `ktlintCheck` + `detekt` verdi
