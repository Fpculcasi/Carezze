# THINK — 6.5: UI GenerateInvitationScreen

**Delivers:** l'OWNER di una Persona può toccare "Condividi Persona" in PersonDetailScreen → vede schermata con QR bitmap + codice testo + pulsante "Condividi" che apre Android Share Sheet. AC1 completo.

## Context Saturation Check
- `GenerateInvitationUseCase` verificato: delega a `InvitationRepository.generateInvitation` ✅
- `QrCodeGenerator.generate(code)` disponibile in `data/util/` ✅
- Schema `Invitation` verificato: ha campo `code` (8 char) ✅
- Pattern ViewModel (StateFlow + viewModelScope.launch) verificato ✅
- `AuthRepository.currentUser` fornisce `id` + `displayName` ✅
- 0 unknown → procedo

## Files
1. `ui/invitation/GenerateInvitationViewModel.kt` — nuovo
2. `ui/invitation/GenerateInvitationScreen.kt` — nuovo
3. `ui/navigation/AppNavigation.kt` — aggiungi route `GenerateInvitation` + composable
4. `ui/person/PersonDetailScreen.kt` — aggiungi overflow menu "Condividi Persona"

Size: Heavy (4 file, no new dep)

## Design
- Route: `@Serializable data class GenerateInvitation(val personId: String, val personName: String)`
- ViewModel: `init {}` lancia `generateInvitation`, produce `UiState.Loading/Success(invitation)/Error`
- Screen: `when(uiState)` → LinearProgressIndicator | QR (Image + asImageBitmap()) + codice Text + "Condividi" Button | Text errore
- Share: `Intent.ACTION_SEND` con testo "Ti invito su Carezze! Usa il codice: {code}"
- PersonDetailScreen: IconButton Share nell'action bar (usa `Icons.Default.Share`)

## CDM
- ✅ Validation: screen compila; toccando "Condividi Persona" si apre la schermata; il QR è visibile; il pulsante "Condividi" apre Share Sheet
- 🎯 Drift: query Firestore nel Composable (no — deve stare nel ViewModel)
- 🔄 Pivot: se `asImageBitmap()` richiede import non disponibile → usare `AndroidView` con `ImageView`
