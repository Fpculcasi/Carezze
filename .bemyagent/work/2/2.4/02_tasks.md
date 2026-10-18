---
task: 2.4
title: Google Sign-In (Credential Manager)
size: Heavy
---

# TASK — 2.4

**Delivers:** a "Accedi con Google" button on both WelcomeScreen and LoginScreen; tapping it shows the system account picker; on success the user is signed in (or anonymous session promoted) and navigated to Dashboard.

## CDM Criteria

- 🎯 **Drift**: adding Firestore `users/` document logic (task 2.5); touching WorkManager or FCM
- ✅ **Validation**: `./gradlew assembleDebug` succeeds with new Credential Manager deps; `SignInWithGoogleUseCaseTest` and `LinkWithGoogleUseCaseTest` pass
- 🔄 **Pivot**: if Credential Manager API signature changed in `1.3.0` → fall back to `GetGoogleIdOption` with `filterByAuthorizedAccounts(false)` only

## Checklist

- [x] `libs.versions.toml` — add `credentials`, `credentials-play-services-auth`, `googleid` versions
- [x] `app/build.gradle.kts` — add deps + `buildConfig = true` + `buildConfigField("WEB_CLIENT_ID")`
- [x] `domain/usecase/auth/SignInWithGoogleUseCase.kt`
- [x] `domain/usecase/auth/LinkWithGoogleUseCase.kt`
- [x] `ui/auth/GoogleSignInButton.kt`
- [x] `ui/auth/WelcomeScreen.kt` — add GoogleSignInButton
- [x] `ui/auth/LoginScreen.kt` — add GoogleSignInButton
- [x] `AuthViewModel.kt` — add `signInWithGoogle()`, `linkWithGoogle()`
- [x] Unit tests: `SignInWithGoogleUseCaseTest`, `LinkWithGoogleUseCaseTest`
- [x] `04-tech-stack.md` already updated
