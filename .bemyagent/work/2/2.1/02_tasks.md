---
task: 2.1
title: Firebase Anonymous Auth + AuthViewModel + flusso locale
size: Heavy
---

# TASK — 2.1

**Delivers:** `AuthViewModel.continueLocally()` triggers Firebase anonymous sign-in; `authState` emits `AuthUiState.Anonymous` with the signed-in user — independently demoable by calling `continueLocally()` and collecting `authState` in a test or a temporary UI.

## CDM Criteria

- 🎯 **Drift**: touching UI screens (WelcomeScreen, navigation graph) — those belong to task 2.2
- ✅ **Validation**: `SignInAnonymouslyUseCaseTest` and `ObserveAuthStateUseCaseTest` pass (`./gradlew test`); `AuthModule` satisfies Hilt graph (`./gradlew assembleDebug`)
- 🔄 **Pivot**: if MockK cannot mock `AuthRepository` cleanly → replace with a hand-written `FakeAuthRepository` in tests

## Checklist

- [x] `domain/model/User.kt` — data class + Language + TemperatureUnit enums
- [x] `domain/repository/AuthRepository.kt` — interface (all auth methods, incl. 2.3/2.4)
- [x] `domain/usecase/auth/SignInAnonymouslyUseCase.kt`
- [x] `domain/usecase/auth/GetCurrentUserUseCase.kt`
- [x] `domain/usecase/auth/ObserveAuthStateUseCase.kt`
- [x] `data/repository/AuthRepositoryImpl.kt` — Firebase impl
- [x] `ui/auth/AuthViewModel.kt` + `AuthUiState` sealed interface
- [x] `di/AuthModule.kt` — provides FirebaseAuth, binds AuthRepositoryImpl
- [x] `src/test/…/SignInAnonymouslyUseCaseTest.kt`
- [x] `src/test/…/ObserveAuthStateUseCaseTest.kt`
- [x] `03-code-map.md` updated (Use Cases + Domain Models sections)
