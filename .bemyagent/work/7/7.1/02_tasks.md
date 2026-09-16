# TASK — 7.1: FCM Token Management

Delivers: quando Android rinnova il token FCM, il token viene scritto in `users/{uid}.fcmTokens` (arrayUnion) senza sovrascrivere i token di altri dispositivi.

## Checklist

- [ ] `UserRepository.kt` — aggiungere `suspend fun updateFcmToken(userId: String, token: String): Result<Unit>`
- [ ] `UserRepositoryImpl.kt` — implementare `updateFcmToken` con `FieldValue.arrayUnion(token)` + `SetOptions.merge()`
- [ ] `CarezzeMessagingService.kt` — aggiungere `@AndroidEntryPoint`, iniettare `FirebaseAuth` e `UserRepository`, implementare `onNewToken` con null-check uid + coroutine scope

## CDM

### ✅ Validation
- Build debug compila senza errori Hilt/KSP
- `UserRepositoryImpl.updateFcmToken` chiama `arrayUnion` (verificabile a grep)
- `CarezzeMessagingService` ha `@AndroidEntryPoint` (grep)
- Unit test esistenti ancora verdi (`./gradlew testDebugUnitTest`)
