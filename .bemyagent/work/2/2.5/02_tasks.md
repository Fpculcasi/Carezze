---
task: 2.5
title: Gestione users/{userId} documento Firestore
size: Heavy
---

# TASK — 2.5

**Delivers:** after every sign-in (anonymous, email, Google), a `users/{uid}` document is created/updated in Firestore with the user's default settings — independently verifiable by checking the Firebase Console after tapping "Continua in locale".

## CDM Criteria

- 🎯 **Drift**: adding Room entity for User; modifying Settings UI (task 2.6); adding PersonRepository
- ✅ **Validation**: `./gradlew assembleDebug test` succeeds; `SyncUserUseCaseTest`, `GetUserUseCaseTest`, `ObserveUserUseCaseTest` pass
- 🔄 **Pivot**: if `FirebaseFirestore` cannot be mocked → pivot to a `FakeUserRepository` in tests

## Checklist

- [x] `domain/repository/UserRepository.kt` interface
- [x] `domain/usecase/user/SyncUserUseCase.kt`
- [x] `domain/usecase/user/GetUserUseCase.kt`
- [x] `domain/usecase/user/ObserveUserUseCase.kt`
- [x] `data/repository/UserRepositoryImpl.kt`
- [x] `di/FirestoreModule.kt` — provides FirebaseFirestore; binds UserRepositoryImpl
- [x] `AuthViewModel.kt` — call `syncUser` on auth state transitions
- [x] Unit tests: `SyncUserUseCaseTest`, `GetUserUseCaseTest`, `ObserveUserUseCaseTest`
- [x] `03-code-map.md` updated
