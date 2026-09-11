# VERIFY — 5.5.4 Tab Profilo/Account

## CDM Criteria

### ✅ Validation
```
JAVA_HOME="/c/Program Files/Android/Android Studio/jbr" ./gradlew :app:compileDebugKotlin :app:ktlintCheck detekt :app:testDebugUnitTest
```
→ **BUILD SUCCESSFUL** — 42 actionable tasks, 0 test failures

## Acceptance Criteria (da spec R4)
- ✅ Tab "Profilo" non mostra più `Text("Profilo")` — sostituito con `ProfileScreen`
- ✅ Utente anonimo (`AuthUiState.Anonymous`) → `AnonymousProfileContent`: "Stai usando l'app come ospite" + Button "Registrati" + OutlinedButton "Accedi"
- ✅ Utente autenticato (`AuthUiState.Authenticated`) → `AuthenticatedProfileContent`: avatar iniziali, displayName (TextField + Salva), email (read-only), provider, TextButton "Disconnetti"
- ✅ `signOut()` → `SignOutUseCase` → `AuthRepository.signOut()` → Firebase emette SignedOut → `LaunchedEffect` in `AppNavigation` naviga a Welcome (pop tutto il back stack)
- ✅ CTA "Registrati"/"Accedi" → `rootNavController.navigate(Register/Login)` via callback in `MainScreen`
- ✅ `updateDisplayName(name)` → `syncUser(user.copy(displayName = name))` — stessa catena di SettingsViewModel
- ✅ `SignOutUseCase` con `@Inject constructor` — nessun binding custom necessario
- ✅ ktlintCheck PASS, detekt PASS, testDebugUnitTest PASS

## Verdict: PASS
