---
task: 2.2
title: Schermata Welcome + setup navigazione
size: Standard
---

# TASK — 2.2

**Delivers:** tapping "Continua in locale" on WelcomeScreen triggers anonymous sign-in and navigates to the Dashboard stub, with no back-stack entry for Welcome — independently demoable on any emulator/device.

## CDM Criteria

- ✅ **Validation**: `./gradlew assembleDebug` succeeds; NavHost compiles with all routes; Welcome screen has both CTAs wired to correct actions

## Checklist

- [x] `ui/navigation/AppNavigation.kt` — type-safe routes + NavHost
- [x] `ui/auth/WelcomeScreen.kt` — two CTAs, auth-state observer, navigation
- [x] `ui/dashboard/DashboardScreen.kt` — placeholder stub
- [x] `MainActivity.kt` — replace Greeting with AppNavigation()
- [x] `03-code-map.md` navigation table updated
