---
task: 2.5.4
Delivers: Email verification wall post-registrazione; Google e anonimi esclusi; resend con cooldown 60s.
---

## Checklist

- [ ] `User.kt` — aggiungere `isEmailVerified: Boolean`
- [ ] `AuthRepository.kt` — aggiungere `sendEmailVerification()` e `reloadUser()`
- [ ] `AuthRepositoryImpl.kt` — implementare entrambi; aggiornare `toDomain()`; chiamare `sendEmailVerification` in `createUserWithEmail` + `linkWithEmail`
- [ ] `UserRepositoryImpl.kt` — aggiungere `isEmailVerified = false` nel `toDomain()` Firestore
- [ ] `AuthViewModel.kt` — `AuthUiState.PendingEmailVerification`; `_emailVerified`; `_resendCooldown`; `checkEmailVerified()`; `resendVerificationEmail()`
- [ ] `AppNavigation.kt` — route `EmailVerification`; gestione `PendingEmailVerification` in `LaunchedEffect`; composable
- [ ] `EmailVerificationScreen.kt` — nuova schermata

## CDM Validation
- Dopo registrazione email: `authState = PendingEmailVerification` → navigazione a `EmailVerification`
- Google Sign-In: `authState = Authenticated` → nessuna verifica richiesta
- Anonimi: `authState = Anonymous` → nessuna verifica
- Tap "Ho verificato" con email verificata → naviga a Main
- Resend con cooldown 60s → "Rinvia (60s)" countdown
