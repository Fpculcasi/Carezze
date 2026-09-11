# THINK — 5.5.4 Tab Profilo/Account

## Context Saturation Check
- `ObserveAuthStateUseCase` → `Flow<User?>` (mapper a AuthUiState in AuthViewModel) ✅ letta
- `AuthUiState` sealed (Loading/SignedOut/Anonymous/Authenticated) ✅ letta
- `SyncUserUseCase(user: User)` → `userRepository.syncUser(user)` ✅ letta
- `ObserveUserUseCase` e `SyncUserUseCase` già iniettati in SettingsViewModel ✅
- `AuthRepository.signOut()` esiste sia in interface che in `AuthRepositoryImpl` ✅
- `SignOutUseCase` non esiste → da creare ✅ confermato
- `MainScreen()` non ha parametri → da aggiungere `onNavigateToLogin` + `onNavigateToRegister` ✅
- Route `Welcome`, `Login`, `Register` definite nell'outer NavHost ✅
- `Profile` tab stub `Text("Profilo")` in `AppNavigation.kt` linea 272–275 ✅
- 0 unknown → procedo

## Task
Implementare `ProfileScreen`: se utente anonimo → CTA Registrati/Accedi; se autenticato → vedi displayName (modificabile), email, provider + bottone "Disconnetti". Sign-out causa auto-navigazione a Welcome via `LaunchedEffect` su auth state in `AppNavigation`.

## Approccio

### 1. SignOutUseCase (domain)
Wrapper triviale su `AuthRepository.signOut()` — `@Inject constructor(authRepository)`.

### 2. ProfileViewModel
- Inietta: `ObserveAuthStateUseCase`, `ObserveUserUseCase`, `SyncUserUseCase`, `SignOutUseCase`, `AuthRepository`
- `authState: StateFlow<AuthUiState>` — stessa map di AuthViewModel
- `userState: StateFlow<User?>` — via `observeUser(uid)` se uid noto, else MutableStateFlow(null)
- `fun updateDisplayName(name: String)` → `syncUser(current.copy(displayName = name))`
- `fun signOut()` → `viewModelScope.launch { signOut() }`

### 3. ProfileScreen
- Se `Anonymous`: card centrata, testo "Stai usando l'app come ospite", pulsanti Registrati + Accedi
- Se `Authenticated`: avatar iniziali, displayName (TextField + Salva), email (read-only), provider badge, pulsante "Disconnetti"
- Riceve `onNavigateToLogin` e `onNavigateToRegister` come parametri (per anon flow)

### 4. AppNavigation
- `LaunchedEffect(authState)` in `AppNavigation()`: se diventa `SignedOut` → naviga a Welcome, pop tutto
- `MainScreen(onNavigateToLogin, onNavigateToRegister)` — aggiunge 2 parametri con default `{}`
- `composable<Profile>` → `ProfileScreen(onNavigateToLogin, onNavigateToRegister)`

### Files toccati (Heavy — 4 files, 2 nuovi)
1. `domain/usecase/auth/SignOutUseCase.kt` — nuovo
2. `ui/profile/ProfileViewModel.kt` — nuovo
3. `ui/profile/ProfileScreen.kt` — nuovo
4. `ui/navigation/AppNavigation.kt` — modifica

## CDM
### 🎯 Drift
Aggiungere funzionalità non richieste (ruoli, cambio email, foto profilo).

### ✅ Validation
`JAVA_HOME="/c/Program Files/Android/Android Studio/jbr" ./gradlew :app:compileDebugKotlin :app:ktlintCheck detekt :app:testDebugUnitTest`
→ BUILD SUCCESSFUL, 0 failures

### 🔄 Pivot
Se `SyncUserUseCase` non persiste `displayName` correttamente → verificare `UserRepositoryImpl.syncUser()` prima di procedere.

## Pre-mortem
1. Hilt: `SignOutUseCase` non trovato da injection graph → aggiungere `@Inject constructor` standard, nessun binding custom necessario (stessa catena di esistenti)
2. `authState` in AppNavigation non recompose-stable → usare `collectAsStateWithLifecycle()` da lifecycle-runtime-compose
3. Auth state observer in AppNavigation naviga a Welcome anche all'avvio se l'utente è già SignedOut prima di fare sign-in → guard `LaunchedEffect` con chiave su transizione (SignedOut E back stack contiene Main)

## Devil's Advocate
Alternativa: aggiungere `signOut()` direttamente ad `AuthViewModel` e osservare `authState` già presente. Più semplice, ma `AuthViewModel` è già grande e il profile non appartiene al flusso auth. `ProfileViewModel` separato rispetta SRP. Confermato.
