# Verify — 2.5.5

## Verdict: PASS

| Criterio | Evidenza |
|---|---|
| Voce "Privacy & Dati" in Impostazioni | `TextButton("Privacy & Dati", onClick = onNavigateToPrivacy)` in `SettingsScreen` ✅ |
| `PrivacyDataScreen` mostra riepilogo raccolta dati | lista items in `PrivacyDataContent` ✅ |
| Link "Leggi la policy completa" apre browser | `Intent(ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))` ✅ |
| Route `PrivacyData` in inner NavHost | `composable<PrivacyData>` in `MainScreen` NavHost ✅ |
| Build OK | `./gradlew compileDebugKotlin` → BUILD SUCCESSFUL ✅ |
| Test OK | `./gradlew testDebugUnitTest` → BUILD SUCCESSFUL ✅ |
