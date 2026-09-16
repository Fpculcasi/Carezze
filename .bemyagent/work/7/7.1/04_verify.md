# VERIFY — 7.1: FCM Token Management

## Criterio 1 — Build debug compila senza errori
**Evidenza:** `./gradlew assembleDebug` → `BUILD SUCCESSFUL in 3s` (43 tasks, configuration cache reused)
**Verdict:** PASS

## Criterio 2 — `updateFcmToken` usa `arrayUnion`
**Evidenza:** `grep arrayUnion UserRepositoryImpl.kt` → riga 51: `mapOf("fcmTokens" to FieldValue.arrayUnion(token))`
**Verdict:** PASS

## Criterio 3 — `CarezzeMessagingService` ha `@AndroidEntryPoint`
**Evidenza:** `grep @AndroidEntryPoint CarezzeMessagingService.kt` → riga 16: `@AndroidEntryPoint`
**Verdict:** PASS

## Criterio 4 — Unit test esistenti verdi
**Evidenza:** `./gradlew testDebugUnitTest` → `BUILD SUCCESSFUL` (34 tasks)
**Verdict:** PASS

## Side effect positivo — Fix pre-existing build error
`Icons.Default.Group` rimosso dalla base icon set del BOM aggiornato; rimpiazzato con `Icons.Default.Person` in `PersonDetailScreen.kt`.

## Verdict globale: **PASS**
