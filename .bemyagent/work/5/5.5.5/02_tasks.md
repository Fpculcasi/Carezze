# TASK — 5.5.5 Navigazione: no back su tab radice; PersonDetail unica pagina persona

**Delivers:** L'utente naviga nelle tab Persone e Impostazioni senza vedere la freccia back; aprendo una persona trova nome/terapie + dialog di modifica inline; crea una persona dal dialog nella lista — tutto senza passare per EditPersonScreen (eliminata).

## Checklist

- [ ] PersonListScreen.kt — rimuovi `onNavigateBack` + navigationIcon; sostituisci `onNavigateToAdd` con dialog interno; aggiungi `onCreatePerson` lambda al content
- [ ] PersonDetailScreen.kt — rimuovi `onNavigateToEdit`; aggiungi `onUpdatePerson` lambda; dialog edit inline con campi nome/soprannome
- [ ] SettingsScreen.kt — rimuovi `onNavigateBack` + navigationIcon da screen e content; rimuovi import inutilizzati
- [ ] AppNavigation.kt — rimuovi route `EditPerson`, import `EditPersonScreen`, composable<EditPerson>; aggiorna PersonList (rimuovi onNavigateBack/onNavigateToAdd), PersonDetail (rimuovi onNavigateToEdit), Settings (rimuovi onNavigateBack)
- [ ] Elimina `EditPersonScreen.kt`
- [ ] Verifica grep: nessun riferimento residuo a `EditPerson`
- [ ] `./gradlew assembleDebug` verde

## CDM

- ✅ **Validation**: build verde; grep `EditPerson` → 0 hit; no `ArrowBack` in PersonListScreen/SettingsScreen; `showEditDialog` presente in PersonDetailScreen
