# THINK — 4.5.7 QuickLogSheet: accesso rapido alle terapie

## Delivers
L'utente apre il QuickLogSheet dal tasto "+" su un PersonCard della Home, tocca "💊 Farmaci", seleziona la terapia e il farmaco, e può segnare la dose come presa oppure navigare allo storico — tutto senza uscire dalla Home.

## Context Saturation Check
- QuickLogViewModel: letto ✅
- QuickLogSheet: letto ✅
- DashboardScreen: letto ✅ (wiring interno, param già noti dalla navigation)
- AppNavigation Dashboard block: letto ✅ (linea ~246, passa onNavigateTo* come lambda)
- TherapyRepository + Therapy model: letti ✅
- AddManualMedicationLogUseCase: letto ✅
- ObserveTherapiesUseCase: confermata esistenza ✅
- DashboardScreen signature esatta: da verificare al momento dell'edit ✅ (non bloccante)

Unknowns < 2 → procedo con assunzione esplicita: DashboardScreen riceve onNavigateTo* come param, aggiungo due nuovi in coda.

## Pre-mortem
1. **Stale therapy state nel ViewModel**: QuickLogViewModel è `@HiltViewModel`, vive finché il composable è nello stack. Se l'utente apre il sheet, chiude, riapre per una persona diversa, le terapie del vecchio personId potrebbero essere mostrate un frame. Mitigazione: cancellare `therapies`/`selectedTherapyId`/`selectedMedicationId` in `clearType()`.
2. **Callback navigation mancanti in DashboardScreen**: se DashboardScreen non espone i nuovi param, AppNavigation non sa come passarli. Mitigazione: leggere la firma esatta prima di toccare AppNavigation.
3. **Grid a 8 tile**: attualmente 7 tile in griglia 2-col = 3 righe + 1 solitaria. Con 8 = 4 righe esatte. Layout migliorato, nessun problema.

## Devil's Advocate
Alternativa: creare un nuovo bottom sheet `TherapyQuickSheet` separato, triggerable da un secondo pulsante sulla PersonCard.
→ Respinta: introduce un secondo entry point UI non richiesto; il requisito è usare il QuickLogSheet esistente.

## Approcci Considerati
- **Approccio scelto**: aggiungere `THERAPY` a `ActivityLogType`; lo state del sheet gestisce i 3 step tramite `selectedTherapyId`/`selectedMedicationId` nello `UiState`; caricamento terapie triggerato da `selectType(THERAPY, personId)`.
- **Alternativa scartata**: ViewModel separato `TherapyQuickLogViewModel` → overhead di injection senza beneficio concreto; QuickLogViewModel è già in scope.

## CDM Criteri

### 🎯 Drift
Toccare file fuori da: `QuickLogViewModel.kt`, `QuickLogSheet.kt`, `DashboardScreen.kt`, `AppNavigation.kt`. In particolare: NON modificare `TherapyLogViewModel`, `TherapyDetailScreen`, o qualsiasi schermata di terapia esistente.

### ✅ Validation
1. `./gradlew compileDebugKotlin` → zero errori
2. La griglia del QuickLogSheet mostra 8 tile (7 esistenti + "💊 Farmaci")
3. Con terapie attive: flusso completo terapia→farmaco→"Segna presa" si completa; `isSaved` dimette il sheet
4. Con terapie attive: "Vai allo storico" naviga a `TherapyLog(personId, therapyId)` e il sheet si chiude
5. Con nessuna terapia attiva: compare il pulsante "Aggiungi terapia" che naviga a `AddTherapy(personId)` e chiude il sheet
6. Back navigation interna funziona a ogni step (farmaco→terapia, azione→farmaco, terapia→griglia)

### 🔄 Pivot
Se `QuickLogUiState` supera 8 campi totali O il blocco THERAPY in `QuickLogSheet` supera 100 righe → estrarre `TherapyStepState` data class + composable `TherapyStepContent` separato nello stesso file.
