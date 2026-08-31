# THINK — Task 3.3: UI Person

## Context Saturation Check
- Pattern Navigation: type-safe con `@Serializable object/data class` (da AppNavigation.kt) ✅
- Pattern ViewModel: `@HiltViewModel`, `StateFlow`, `viewModelScope` (da SettingsViewModel) ✅
- Pattern Screen: `Scaffold + TopAppBar`, Material3, `@Composable` (da DashboardScreen) ✅
- AuthRepository per currentUser.id: iniettabile via Hilt ✅
- Route con parametri: `@Serializable data class EditPerson(val personId: String?)` — null = create, non-null = edit ✅

## Scope
- `ui/person/PersonViewModel.kt` — osserva persone, espone state, crea/aggiorna/elimina
- `ui/person/PersonListScreen.kt` — lista con FAB + delete swipe
- `ui/person/EditPersonScreen.kt` — form nome + soprannome
- `ui/person/PersonDetailScreen.kt` — stub (M4/M5 la popoleranno)
- EDIT `AppNavigation.kt` — aggiunge PersonList, EditPerson, PersonDetail
- EDIT `DashboardScreen.kt` — aggiunge pulsante "Persone" → PersonList

## Pre-mortem
1. **PersonViewModel.currentUserId**: AuthRepository non ha un Flow; SettingsViewModel usa `authRepository.currentUser?.id`. Stesso pattern: inject AuthRepository, read id sincrono. Se null (non autenticato), lista vuota.
2. **Navigation arg nullable**: `personId: String?` in rotta Serializable — supportato da Navigation Compose 2.8+ (già in uso nel progetto).

## Files da toccare
- NEW: `ui/person/PersonViewModel.kt`
- NEW: `ui/person/PersonListScreen.kt`
- NEW: `ui/person/EditPersonScreen.kt`
- NEW: `ui/person/PersonDetailScreen.kt` (stub)
- EDIT: `ui/navigation/AppNavigation.kt`
- EDIT: `ui/dashboard/DashboardScreen.kt`

**Size: Heavy** (4 file nuovi + 2 edit)
