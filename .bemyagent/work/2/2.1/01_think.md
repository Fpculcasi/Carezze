---
task: 2.1
title: Firebase Anonymous Auth + AuthViewModel + flusso locale
size: Heavy
status: done
---

# THINK — 2.1

## Context Saturation Check

| Item | Status |
|---|---|
| Package convention: `com.fpculcasi.carezze.[layer].[sub]` in physical dir `com/carezze/app/` | ✅ verified (CarezzeMessagingService.kt) |
| Firebase Auth SDK in dependencies (`firebase-auth-ktx`) | ✅ verified (build.gradle.kts) |
| Hilt already configured (`@HiltAndroidApp` on Application) | ✅ verified |
| No existing DI modules — AuthModule must be created from scratch | ✅ verified (no `di/` dir) |
| `lifecycle-viewmodel-ktx` available transitively via `hilt-navigation-compose` | ✅ inferred from deps |
| No test source set exists yet — must create `src/test/kotlin/…` tree | ✅ verified (only `src/main/` present) |
| `google-services.json` template exists (M1 task 1.2) — CI uses placeholder | ✅ assumed from plan |

Unknown items: 0 — proceeding.

## Approach

Build a clean domain/data separation:
- **Domain**: `User` model, `AuthRepository` interface, 3 use cases
- **Data**: `AuthRepositoryImpl` wraps Firebase Auth; maps `FirebaseUser → User`; all auth methods stubbed now so 2.3/2.4 only add callers, not new repo methods
- **DI**: `AuthModule` provides `FirebaseAuth.getInstance()` and binds the implementation
- **UI**: `AuthViewModel` exposes `StateFlow<AuthUiState>` driven by `ObserveAuthStateUseCase`; `continueLocally()` triggers anonymous sign-in

## Pre-mortem

1. **`callbackFlow` listener leak**: `observeAuthState()` adds an `AuthStateListener` — if `awaitClose` is missing or the scope is cancelled before `awaitClose` runs, the listener accumulates. Mitigation: always pair `addAuthStateListener` with `removeAuthStateListener` in `awaitClose`.
2. **No `google-services.json` at build time**: `FirebaseAuth.getInstance()` initialises at app start; a missing/invalid JSON causes `IllegalStateException` in any instrumented test or emulator run. Mitigation: Hilt provides `FirebaseAuth` lazily in `SingletonComponent`; the placeholder JSON committed in M1 satisfies the build; unit tests mock the repo interface, never touching Firebase directly.

## Devil's Advocate

**Alternative**: inject `FirebaseAuth` directly into `AuthViewModel` (no repository, no use cases). Faster to write; zero indirection.
**Why not**: `FirebaseAuth` is final and cannot be mocked by MockK without `@MockK(relaxed = true)` + `mockkStatic` — brittle in unit tests. The interface + use-case pattern keeps all test friction in data layer (which has no unit tests in 2.1). Not pivoting.
