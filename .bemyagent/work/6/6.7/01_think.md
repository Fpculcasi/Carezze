# THINK — 6.7: UI MembersScreen

**Delivers:** OWNER vede lista membri di una Persona + revoca singolo membro con dialog conferma → AC4/AC5 soddisfatti.

## Note
- Files: `MembersViewModel.kt`, `MembersScreen.kt`; PersonDetailScreen aggiornato con icona "Membri"
- MembersViewModel osserva persone via `ObservePersonsUseCase`, filtra per `personId`, espone `MembersUiState`
- Build: `./gradlew assembleDebug` → exit 0 ✅
