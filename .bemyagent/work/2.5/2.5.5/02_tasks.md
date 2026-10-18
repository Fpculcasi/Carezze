---
task: 2.5.5
Delivers: Schermata "Privacy & Dati" accessibile da Impostazioni con riepilogo raccolta dati e link policy.
---

## Checklist

- [ ] `PrivacyDataScreen.kt` — nuova schermata
- [ ] `AppNavigation.kt` — route `PrivacyData`; composable; callback da `SettingsScreen`
- [ ] `SettingsScreen.kt` — aggiungere voce "Privacy & Dati" con navigazione

## CDM Validation
- Tap "Privacy & Dati" in Impostazioni → apre schermata dedicata
- Tap "Leggi la policy completa" → apre browser con URL GitHub Pages
