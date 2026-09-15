# Tech Stack — Carezze

## Tecnologie

| Tecnologia | Versione | Ruolo | Perché scelto |
|---|---|---|---|
| **Kotlin** | 2.x | Linguaggio principale | Null-safety, coroutines, sealed class per domain model |
| **Jetpack Compose** | BOM 2025.x | UI declarativa | Unico toolkit ufficiale Google per nuovi progetti Android |
| **Firebase Auth** | latest | Autenticazione | Email/password, Google Sign-In, Anonymous (locale) — tutti in un SDK |
| **Credential Manager** | `1.3.0` | Google Sign-In moderno | `androidx.credentials` + `credentials-play-services-auth` + `googleid:1.1.1`; sostituisce il deprecato `GoogleSignInClient` |
| **Cloud Firestore** | latest | Database real-time | Offline-first nativo, snapshot listeners, scalabilità orizzontale |
| **Firebase Cloud Messaging** | latest | Push notifications | Integrato con Firebase, delivery garantito, supporto multi-device |
| **ZXing Core** | 3.5.3 | Generazione QR bitmap | Libreria pura Java, converte codice invito in `BitMatrix` → `Bitmap` |
| **Hilt** | 2.x | Dependency Injection | Standard Android DI, integrato con Compose + ViewModel |
| **Room** | 2.x | Cache locale SQLite | Offline-first: source of truth locale, query tipizzate, migration support |
| **WorkManager** | 2.x | Sync in background | Sincronizzazione Room ↔ Firestore garantita anche dopo reboot |
| **Navigation Compose** | 2.x | Navigazione | Graph dichiarativo, type-safe routes con Kotlin serialization |
| **Kotlin Coroutines** | 1.x | Async | Integrazione nativa con Flow, Firestore SDK, Room |
| **Kotlin Flow** | — | Stream dati reattivi | StateFlow nei ViewModel, cold Flow nei Repository |
| **Glance** | 1.x | Android Widgets | API Compose-like per widget, unica soluzione moderna su Android |
| **Coil** | 3.x | Caricamento immagini | Compose-first, lightweight |
| **Kotlinx Serialization** | 1.x | Serializzazione JSON | Per routes type-safe e Cloud Functions payload |
| **JUnit 5** | 5.x | Test framework | Parametrized tests, nested classes, più espressivo di JUnit 4 |
| **MockK** | 1.x | Mocking Kotlin | Kotlin-first, supporto coroutines e sealed class |
| **Turbine** | 1.x | Test per Flow | Utility per assertare emissions di Kotlin Flow nei test |
| **Espresso / Compose Testing** | latest | UI tests | Test end-to-end su schermate Compose |
| **Firebase Test Lab** | — | Device testing cloud | Test su dispositivi fisici in CI (futuro) |
| **GitHub Actions** | — | CI/CD | Build, test, lint automatici su ogni PR |
| **Detekt** | 1.x | Static analysis | Code quality, integrato in CI |
| **Ktlint** | 1.x | Formatter | Stile codice consistente tra agenti |

## Problemi di Compatibilità Noti

| Problema | Workaround |
|---|---|
| Firestore Timestamp ↔ `java.time.Instant` | Usare extension functions di mapping nel data layer; mai esporre `Timestamp` al domain |
| Glance widget e Hilt | Glance non supporta Hilt injection diretta; iniettare via `EntryPointAccessors` |
| Room + Kotlin sealed class | Room non serializza sealed class nativamente; usare `@TypeConverter` custom |
| Anonymous Auth → account reale | Firebase `linkWithCredential()` per migrare; Room data da migrare manualmente nel use case |

## Servizi Esterni

| Servizio | Utilizzo | Endpoint/Config |
|---|---|---|
| Firebase Auth | Autenticazione utenti | `google-services.json` |
| Cloud Firestore | Database | Progetto: `carezze-5a3b0` (placeholder) |
| Firebase Cloud Messaging | Notifiche push | Token salvati in `users/{id}.fcmTokens` |

## Infrastruttura

| Risorsa | Dettaglio |
|---|---|
| Firebase Piano | Spark (gratuito) — Zero Cloud Functions (vedi D-15) |
| Regioni Firestore | `europe-west1` (GDPR compliance) |
| Android minSdk | 26 (Android 8.0) — copre > 95% dispositivi attivi |
| Android targetSdk | 35 (Android 15) |
| Android compileSdk | 35 |
