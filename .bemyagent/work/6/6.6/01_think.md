# THINK — 6.6: UI RedeemInvitationScreen

**Delivers:** un utente autenticato inserisce un codice 8-char → AC2/AC3 soddisfatti (codice già usato/scaduto → errore; codice valido → utente diventa EDITOR).

## Note
- Scanner QR camera rinviato (richiede CameraX + ML Kit, 5+ nuove dep); input manuale copre AC3
- Implementato insieme a 6.5/6.7 in un'unica sessione di build
- Files: `RedeemInvitationViewModel.kt`, `RedeemInvitationScreen.kt`; AppNavigation e ProfileScreen aggiornati per entry point
- Build: `./gradlew assembleDebug` → exit 0 ✅
