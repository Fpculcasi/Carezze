---
task: 2.6
title: Schermata Settings
size: Standard
---

# TASK — 2.6

**Delivers:** user can change language (IT/EN), temperature unit (°C/°F), and quiet hours from a Settings screen accessible from the Dashboard; changes persist to `users/{uid}` in Firestore and survive app restart.

## CDM Criteria

- ✅ **Validation**: `./gradlew assembleDebug test` succeeds; Settings route renders without crash

## Checklist

- [x] `ui/settings/SettingsViewModel.kt`
- [x] `ui/settings/SettingsScreen.kt`
- [x] `AppNavigation.kt` — add `Settings` route
- [x] `DashboardScreen.kt` — add settings icon in top bar
- [x] `03-code-map.md` updated
