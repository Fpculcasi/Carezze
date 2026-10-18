---
task: 2.2
title: Schermata Welcome + setup navigazione
size: Standard
status: done
---

# THINK — 2.2

## Context Saturation Check

| Item | Status |
|---|---|
| `AuthViewModel` + `AuthUiState` available from task 2.1 | ✅ verified |
| `navigation-compose 2.9.0` + `kotlinx-serialization` in deps — type-safe routes usable | ✅ verified (libs.versions.toml) |
| `CarezzeTheme` in `ui/theme/Theme.kt` | ✅ verified |
| `MainActivity` currently shows placeholder `Greeting` — needs NavHost replacement | ✅ verified |
| `ui/auth/` and `ui/dashboard/` dirs exist (`.gitkeep`) | ✅ verified |

Unknown items: 0 — proceeding.

## Approach

- **Routes**: sealed `@Serializable` objects in `AppNavigation.kt` — `Welcome`, `Dashboard`, `Login`, `Register`
- **NavHost** in `AppNavigation.kt`: start = `Welcome`; auth-state observation via `LaunchedEffect` in WelcomeScreen drives `navigate(Dashboard) { popUpTo(Welcome) { inclusive = true } }` so back-press can't return to Welcome once inside the app
- **WelcomeScreen**: two CTAs — "Continua in locale" (calls `continueLocally()`) and "Accedi / Registrati" (navigates to `Login`)
- **Dashboard stub**: minimal `Text("Dashboard — M5")` so NavGraph compiles end-to-end
- **MainActivity**: replace `Greeting` + `Surface` with `AppNavigation()`; `AuthViewModel` is provided by Hilt at Activity scope

## Pre-mortem

1. **Double navigation on recomposition**: `LaunchedEffect(authState)` may fire multiple times if `authState` recomposes while the navigate call is in flight → mitigated by using `popUpTo(inclusive=true)` so the destination stack is clean, and by keying the effect on the sealed state object (stable).
