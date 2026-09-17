# Verify — 2.5.4

## Verdict: PASS

| Criterio | Evidenza |
|---|---|
| `AuthUiState.PendingEmailVerification` emesso per email non verificata | mapping `!user.isEmailVerified -> PendingEmailVerification` in `AuthViewModel.authState` ✅ |
| Google Sign-In → `Authenticated` (skip verifica) | `isEmailVerified = true` per Google — verificato in `AuthRepositoryImpl.toDomain()` ✅ |
| Anonimi → `Anonymous` (skip verifica) | `user.isAnonymous` check precede `!user.isEmailVerified` ✅ |
| Email inviata subito dopo registrazione | `user.sendEmailVerification().await()` in `createUserWithEmail` + `linkWithEmail` ✅ |
| `checkEmailVerified()` usa `reloadUser()` + `_emailVerified` MutableStateFlow | presente in `AuthViewModel` ✅ |
| Resend con cooldown 60s | `_resendCooldown` countdown in `resendVerificationEmail()` ✅ |
| `EmailVerificationScreen` naviga a Main quando `emailVerified == true` | `LaunchedEffect(emailVerified)` ✅ |
| Route `EmailVerification` in NavHost root | `composable<EmailVerification>` in `AppNavigation` ✅ |
| Build OK | `./gradlew compileDebugKotlin` → BUILD SUCCESSFUL ✅ |
| Test OK | `./gradlew testDebugUnitTest` → BUILD SUCCESSFUL ✅ |
