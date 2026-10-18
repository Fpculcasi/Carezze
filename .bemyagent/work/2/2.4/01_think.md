---
task: 2.4
title: Google Sign-In (Credential Manager)
size: Heavy
status: done
---

# THINK — 2.4

## Context Saturation Check

| Item | Status |
|---|---|
| `AuthRepository.signInWithGoogle(idToken)` + `linkWithGoogle(idToken)` implemented in 2.1 | ✅ verified |
| `AuthViewModel` already has `signInWithGoogle`/`linkWithGoogle` stubs needed → will add now | ✅ plan |
| New deps: `credentials:1.3.0`, `credentials-play-services-auth:1.3.0`, `googleid:1.1.1` | ✅ documented in 04-tech-stack.md |
| `CredentialManager.getCredential()` requires `Activity` context — available as `LocalContext.current` in Compose inside an Activity | ✅ verified by Android docs pattern |
| WEB_CLIENT_ID must match the OAuth 2.0 client registered in Firebase Console — developer must set SHA-1 fingerprint; using `BuildConfig` field sourced from `local.properties` | ✅ plan (placeholder value for CI) |
| `buildConfig = true` must be added to `buildFeatures` in `build.gradle.kts` | ✅ required |

Unknown items: 0 — proceeding.

## Approach

**Architecture decision**: `CredentialManager` lives in the UI layer (`GoogleSignInButton.kt`), not in `AuthRepositoryImpl`. Reason: `getCredential()` needs an `Activity` context and is fundamentally a UI interaction (shows the Google account picker). The repository only receives the `idToken` string — no Android context crosses the domain boundary.

**Files**:
1. `libs.versions.toml` + `build.gradle.kts` — add deps + `buildConfigField("WEB_CLIENT_ID")`
2. `domain/usecase/auth/SignInWithGoogleUseCase.kt`
3. `domain/usecase/auth/LinkWithGoogleUseCase.kt`
4. `ui/auth/GoogleSignInButton.kt` — composable that owns the CredentialManager call
5. `ui/auth/LoginScreen.kt` — add GoogleSignInButton
6. `ui/auth/WelcomeScreen.kt` — add GoogleSignInButton
7. `AuthViewModel.kt` — add `signInWithGoogle()` + `linkWithGoogle()`

## Pre-mortem

1. **`NoCredentialException` on first launch / no Google account on device**: CredentialManager throws if no accounts are available. Mitigation: catch `GetCredentialException` in `GoogleSignInButton` and surface via `onError` callback → ViewModel sets `errorMessage`.
2. **SHA-1 not registered**: even if the code compiles, sign-in will fail at runtime without the OAuth client ID + SHA-1 in Firebase Console. Mitigation: add a `BuildConfig.GOOGLE_WEB_CLIENT_ID` placeholder; document the setup step in `04-tech-stack.md`.

## Devil's Advocate

**Alternative**: use the legacy `GoogleSignIn` + `ActivityResultContracts.StartActivityForResult` flow — simpler, no new deps, still works.
**Why not**: Google deprecated `GoogleSignInClient` in favour of Credential Manager; using deprecated API in a portfolio project signals poor currency. Not pivoting.
