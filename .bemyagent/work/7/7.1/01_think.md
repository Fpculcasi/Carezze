# THINK — Task 7.1: FCM Token Management

## Context
Persistere il token FCM in `users/{uid}.fcmTokens` (array) ogni volta che Android lo rinnova,
così le Cloud Functions (7.6) possono inviare push al dispositivo corretto.

### Context Saturation Check
- [x] **File/path target** — `CarezzeMessagingService.kt`, `UserRepository.kt`, `UserRepositoryImpl.kt` (verificati)
- [x] **Comportamento atteso** — `onNewToken` → `arrayUnion(token)` su Firestore; skip se utente anonimo o non loggato
- [x] **Vincoli** — nessuna nuova dipendenza (firebase-messaging già dichiarato); `@AndroidEntryPoint` necessario per Hilt in Service
- [x] **Dipendenze** — `FirebaseAuth` + `UserRepository` già iniettabili via Hilt

## Approaches Considered
- **A (scelto):** `@AndroidEntryPoint` su `CarezzeMessagingService` + inject `FirebaseAuth` e `UserRepository`; in `onNewToken` lancia coroutine `lifecycleScope` → `updateFcmToken`. Pro: pattern Hilt standard. Contro: `lifecycleScope` non disponibile in Service → usare `CoroutineScope(SupervisorJob() + Dispatchers.IO)` manuale o `ServiceScope`.
- **B:** WorkManager one-shot in `onNewToken`. Pro: retry automatico. Contro: overhead eccessivo per un singolo write Firestore; complexity non giustificata.

## Selected Approach & Risks
Approccio A con `CoroutineScope(SupervisorJob() + Dispatchers.IO)` definito a livello di companion/proprietà del service — costo **low** (3 file, ~20 righe nette).
Rischio principale: token refresh prima del login (utente non ancora autenticato) → mitigato con null-check su `FirebaseAuth.currentUser?.uid`.

## Pre-mortem
1. **Token scritto prima del login** → `currentUser` è null → skip silenzioso ✓ (corretto by design; `onNewToken` successivo al login non viene richiamato — ma `getToken()` viene chiamato in `SyncUserUseCase` a login avvenuto, task futuro).
2. **`@AndroidEntryPoint` sul Service** manca dal manifest → non necessario lato manifest (è il codice Hilt a gestirlo, non il manifest tag).
3. **Scope coroutine non cancellato** → `SupervisorJob` viene cancellato in `onDestroy` per evitare leak.

## Devil's Advocate
Alternativa: `SaveFcmTokenUseCase` dedicato invocato dal `CarezzeMessagingService`. Più testabile ma introduce un layer extra (UseCase) per logica che è puramente infrastrutturale. Non superiore: il Service comunica direttamente col repository, pattern usato anche per `AuthRepositoryImpl`. Non facciamo pivot.

## Verification Plan
- `./gradlew testDebugUnitTest` verde
- Build debug compilato senza errori
- Logcat su emulatore: log "FCM token updated" dopo avvio app
