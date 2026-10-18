# TASK — 6.5: UI GenerateInvitationScreen

**Delivers:** OWNER naviga a GenerateInvitationScreen da PersonDetail → vede QR + codice + condivide via Android Share Sheet.

## Checklist

- [ ] `GenerateInvitationViewModel.kt` — UiState (Loading/Success/Error), init genera invito
- [ ] `GenerateInvitationScreen.kt` — mostra QR (Image) + codice + pulsante Condividi
- [ ] `AppNavigation.kt` — route + composable `GenerateInvitation`
- [ ] `PersonDetailScreen.kt` — IconButton Share in TopAppBar actions

## CDM

- ✅ Validation: `./gradlew assembleDebug` → exit 0; share icon visibile in PersonDetail; schermata mostra QR + codice
