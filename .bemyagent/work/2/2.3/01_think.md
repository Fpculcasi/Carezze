---
task: 2.3
title: Registrazione email/password + linkWithCredential migrazione
size: Heavy
status: done
---

# THINK — 2.3

## Context Saturation Check

| Item | Status |
|---|---|
| `AuthRepository.createUserWithEmail`, `linkWithEmail`, `signInWithEmail` already implemented in 2.1 | ✅ verified |
| `AuthViewModel` available with `authState: StateFlow<AuthUiState>` | ✅ verified |
| `Login` + `Register` routes exist in `AppNavigation.kt` as stubs | ✅ verified |
| `WelcomeScreen` already navigates to `Login` on "Accedi / Registrati" | ✅ verified |
| No new Gradle dependency required (Firebase Auth + Compose already present) | ✅ verified |

Unknown items: 0 — proceeding.

## Approach

**Flow:**
- `Login` route → `LoginScreen`: email + password fields + "Accedi" button + "Registrati" link
- `Register` route → `RegisterScreen`: email + password + confirm-password fields + "Registrati" button
- If the current user is **anonymous** → registration calls `linkWithEmail()` (migrates local data to the new account); if **signed out** → calls `createUserWithEmail()`
- Both screens share `AuthViewModel`; on `Authenticated` the `LaunchedEffect` guard in `WelcomeScreen` already navigates to Dashboard (since auth state changes propagate through the shared ViewModel)

**New use cases** (domain layer — no direct Firebase dependency):
- `SignInWithEmailUseCase`
- `CreateUserWithEmailUseCase`
- `LinkWithEmailUseCase`

**AuthViewModel** gets two new methods:
- `signInWithEmail(email, password)`
- `registerOrLink(email, password)` — decides between `createUserWithEmail` and `linkWithEmail` based on `currentUser.isAnonymous`

**ViewModel state** gets an `error: String?` field for showing inline error messages (Firebase error codes mapped to human-readable strings).

## Pre-mortem

1. **Anonymous → email migration race**: `linkWithEmail` fails if the email is already registered — Firebase throws `FirebaseAuthUserCollisionException`. Mitigation: catch in `AuthRepositoryImpl.linkWithEmail` (already wrapped in `runCatching`); surface the error via `AuthUiState` error field in the ViewModel.
2. **Password confirm mismatch handled client-side only**: if the user somehow bypasses the UI validation, `createUserWithEmail` will succeed with a valid password regardless. Mitigation: validation is done in the composable before calling the ViewModel; no server-side confirm needed.

## Devil's Advocate

**Alternative**: put all auth logic (login + register + link) in a single `AuthScreen` with tabs instead of two separate routes. Less navigation code, fewer files.
**Why not**: the spec explicitly names `LoginScreen` and `RegisterScreen` as separate destinations (code-map); separate routes make deep-linking possible in M7. Not pivoting.
