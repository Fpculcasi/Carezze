# TASK — 5.5.4 Tab Profilo/Account

Delivers: il tab "Profilo" mostra al posto dello stub `Text("Profilo")` una schermata funzionante: l'utente anonimo vede i CTA login/registrazione; l'utente autenticato vede displayName (modificabile), email, provider e può disconnettersi; sign-out riporta automaticamente a Welcome.

## Checklist

- [ ] `SignOutUseCase.kt` — nuovo wrapper su `AuthRepository.signOut()`
- [ ] `ProfileViewModel.kt` — nuovo HiltViewModel con `authState`, `userState`, `updateDisplayName`, `signOut`
- [ ] `ProfileScreen.kt` — nuovo composable: anon view (CTA) + auth view (profilo + disconnetti)
- [ ] `AppNavigation.kt` — `LaunchedEffect(authState)` per auto-nav a Welcome su SignedOut
- [ ] `AppNavigation.kt` — `MainScreen(onNavigateToLogin, onNavigateToRegister)` + wiring in `composable<Profile>`
- [ ] Build verde: `compileDebugKotlin` | `ktlintCheck` | `detekt` | `testDebugUnitTest`

## CDM

### ✅ Validation
`JAVA_HOME="/c/Program Files/Android/Android Studio/jbr" ./gradlew :app:compileDebugKotlin :app:ktlintCheck detekt :app:testDebugUnitTest`
→ BUILD SUCCESSFUL, 0 test failures
