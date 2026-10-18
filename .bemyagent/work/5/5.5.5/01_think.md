# THINK — 5.5.5 Navigazione: no back su tab radice; PersonDetail unica pagina persona

## Context Saturation Check
- Struttura nav graph: letta (AppNavigation.kt) ✅
- PersonDetailScreen, EditPersonScreen, PersonListScreen: letti ✅
- SettingsScreen: letto ✅
- PersonViewModel API (createPerson, updatePerson): verificato ✅
- Distinzione tab radice vs detail screen: nota (bottomNavItems governa showBottomBar) ✅

0 unknown → si procede.

## Scope

R5 richiede:
1. Rimuovere la freccia back da `PersonList` e `Settings` (tab radice)
2. `PersonDetail` assorbe `EditPersonScreen`: edit inline via dialog (name/nickname)
3. `EditPersonScreen` viene eliminata
4. Creazione persona da `PersonList` via dialog interno (non più nav verso EditPerson)

## Approccio

- **Edit inline in PersonDetail**: AlertDialog con campi nome/soprannome; l'icona Edit nella TopAppBar apre il dialog invece di navigare a EditPerson. Nessuna nuova schermata.
- **Crea da PersonList**: AlertDialog interno con campi nome/soprannome; FAB apre dialog. Creazione via viewModel.createPerson() direttamente nel Screen.
- **Rimozione back arrow**: eliminare il parametro `onNavigateBack` e il `navigationIcon` da PersonListContent e SettingsContent.
- **Rimozione EditPerson**: eliminare route, file, composable nel nav graph.

## File toccati (Heavy: 4 modifiche + 1 eliminazione)

| File | Tipo |
|---|---|
| `ui/person/PersonListScreen.kt` | modifica |
| `ui/person/PersonDetailScreen.kt` | modifica |
| `ui/settings/SettingsScreen.kt` | modifica |
| `ui/navigation/AppNavigation.kt` | modifica |
| `ui/person/EditPersonScreen.kt` | eliminazione |

## CDM

- 🎯 **Drift**: non toccare TherapyDetail, TherapyLog, HistoryList/Calendar, Dashboard. Scope = solo flusso persona.
- ✅ **Validation**: `./gradlew assembleDebug` verde; nessun riferimento a `EditPerson` nel codebase (grep); no ArrowBack in PersonListScreen né SettingsScreen; PersonDetailScreen ha `showEditDialog` dialog invece di `onNavigateToEdit`.
- 🔄 **Pivot**: se il dialog di edit in PersonDetailContent causa problemi di stato con `person` nullable, spostare il dialog a livello Screen (dove la nullabilità è già gestita).

## Pre-mortem

1. **PersonViewModel non accessibile nel content layer**: mitigazione → passare `onUpdatePerson: (String, String?) -> Unit` come lambda, non il ViewModel direttamente.
2. **Riferimenti residui a EditPerson in altri file**: mitigazione → grep sistematico prima del commit.

## Devil's Advocate

Alternativa: ModalBottomSheet per edit invece di AlertDialog. Più visivamente coerente con Material3, ma aggiunge SheetState e richiede ExperimentalMaterial3Api. Per 2 campi, AlertDialog è sufficiente e più semplice — non si pivota.
