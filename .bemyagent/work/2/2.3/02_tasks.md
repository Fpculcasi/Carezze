---
task: 2.3
title: Registrazione email/password + linkWithCredential migrazione
size: Heavy
---

# TASK — 2.3

**Delivers:** a new user can register with email/password (or sign in with an existing account); an anonymous user who registers has their local session promoted to a full account via `linkWithCredential` — all demoable end-to-end without a backend other than Firebase.

## CDM Criteria

- 🎯 **Drift**: adding Google Sign-In logic (that belongs to task 2.4); adding Firestore user document writes (task 2.5)
- ✅ **Validation**: `./gradlew assembleDebug` succeeds; unit tests for the 3 new use cases pass (`./gradlew test`)
- 🔄 **Pivot**: if `linkWithEmail` Firebase collision error handling proves too complex for UI → simplify to always calling `createUserWithEmail` and surfacing "email already in use" error, deferring migration UX polish

## Checklist

- [x] `domain/usecase/auth/SignInWithEmailUseCase.kt`
- [x] `domain/usecase/auth/CreateUserWithEmailUseCase.kt`
- [x] `domain/usecase/auth/LinkWithEmailUseCase.kt`
- [x] `AuthViewModel` — add `signInWithEmail()`, `registerOrLink()`, `errorMessage: StateFlow`
- [x] `ui/auth/LoginScreen.kt`
- [x] `ui/auth/RegisterScreen.kt`
- [x] `ui/navigation/AppNavigation.kt` — replace Login/Register stubs with real screens
- [x] Unit tests: `SignInWithEmailUseCaseTest`, `CreateUserWithEmailUseCaseTest`, `LinkWithEmailUseCaseTest`
- [x] `03-code-map.md` updated
