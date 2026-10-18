---
task: 2.6
title: Schermata Settings (lingua, unità temperatura, quiet hours)
size: Standard
status: done
---

# THINK — 2.6

## Context Saturation Check

| Item | Status |
|---|---|
| `UserRepository.syncUser()` available for persisting setting changes | ✅ verified (2.5) |
| `ObserveUserUseCase` drives the live Settings state | ✅ verified (2.5) |
| `Language` and `TemperatureUnit` enums defined in `domain/model/User.kt` | ✅ verified |
| Settings route exists in `AppNavigation` routes list (from code-map) | ⚠️ not yet — must add `Settings` route to `AppNavigation.kt` and a navigation trigger from `Dashboard` |
| No new dependency needed | ✅ |

Unknown items: 0 — proceeding.

## Approach

- `SettingsViewModel`: injects `ObserveUserUseCase`, `SyncUserUseCase`; exposes `settingsState: StateFlow<User?>`; provides update functions `setLanguage()`, `setTemperatureUnit()`, `setQuietHours()`
- `SettingsScreen`: observes `settingsState`; shows dropdowns/pickers for language (IT/EN), temperature unit (°C/°F), quiet hours start/end (time pickers via `TimePickerDialog`)
- `AppNavigation.kt`: add `Settings` route; `DashboardScreen` stub gets a settings nav button for discoverability
- Navigation from Dashboard → Settings: `DashboardScreen` is a stub — add a top bar with a settings icon
