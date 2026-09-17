---
task: 2.5.4
title: Verifica email post-registrazione
size: Heavy
---

## Context Saturation Check
- AuthRepository/Impl: letti ✅
- AuthViewModel / AuthUiState: letti ✅
- AppNavigation: letto ✅
- User model: letto ✅
- Decisione blocco totale pre-verifica: confermata dall'utente ✅
- Google Sign-In è già verificato: verificato (Firebase garantisce isEmailVerified=true per Google) ✅
- 0 unknowns → procedo.

## Delivers
Dopo registrazione email/password, l'utente viene bloccato su `EmailVerificationScreen` fino alla verifica; Google = skip; anonimi = esclusi.

## Approccio
1. `User.kt`: aggiungere `isEmailVerified: Boolean`.
2. `AuthRepository.kt`: aggiungere `sendEmailVerification(): Result<Unit>` e `reloadUser(): Result<Unit>`.
3. `AuthRepositoryImpl.kt`: implementare entrambi; `toDomain()` imposta `isEmailVerified = firebaseUser.isEmailVerified`; chiamare `sendEmailVerification` dentro `createUserWithEmail` e `linkWithEmail`.
4. `UserRepositoryImpl.kt`: aggiungere `isEmailVerified = false` in `toDomain()` (Firestore non lo memorizza).
5. `AuthViewModel.kt`: aggiungere `AuthUiState.PendingEmailVerification`, `_emailVerified: MutableStateFlow<Boolean>`, `_resendCooldown: MutableStateFlow<Int>`, metodi `checkEmailVerified()` e `resendVerificationEmail()`.
6. `AppNavigation.kt`: route `EmailVerification`; `LaunchedEffect(authState)` che gestisce `PendingEmailVerification`; composable `EmailVerificationScreen`.
7. Nuova `EmailVerificationScreen.kt`.

## Pre-mortem
- `reload()` non triggera `AuthStateListener` → navigazione basata su `_emailVerified: MutableStateFlow`, non su `authState`.
- `isEmailVerified` aggiunto a `User` non va scritto su Firestore — verificato che `syncUser` non include il campo.
- Anonymous → `isAnonymous` check precede `!isEmailVerified` → non bloccati.
- Utenti esistenti email non verificati → bloccati su `EmailVerificationScreen` (accettabile pre-launch).

## Devil's Advocate
Alt: usare solo `authState` per navigare, facendo reload + re-sign-in. Meno buono: ri-autenticazione richiede password che non abbiamo. La MutableStateFlow locale è affidabile.

## CDM
- **Drift**: modificare logica di navigation oltre auth flow
- **Validation**: dopo registrazione email, `PendingEmailVerification` emesso; tap "Ho verificato" con email verificata naviga a Main; Google Sign-In non passa per EmailVerification
- **Pivot**: se `sendEmailVerification` inside `createUserWithEmail` causa side effects inattesi → spostarlo in `AuthViewModel` subito dopo il successo
