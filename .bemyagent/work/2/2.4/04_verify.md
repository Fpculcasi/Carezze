# VERIFY — 2.4

**Verdict: PASS**

## CDM Evidence

| Criterion | Command | Result |
|---|---|---|
| Build succeeds with new Credential Manager deps | `./gradlew assembleDebug test --no-daemon` | `BUILD SUCCESSFUL in 3m 15s` |
| `SignInWithGoogleUseCaseTest` | test-results XML | `tests=2 failures=0 errors=0` |
| `LinkWithGoogleUseCaseTest` | test-results XML | `tests=2 failures=0 errors=0` |
| All 11 prior tests still pass | test-results XML | `tests=11 failures=0 errors=0` |
| Drift — no WorkManager/FCM touched | grep in 2.4 new files | 0 hits |

### Caveats

- `BuildConfig.GOOGLE_WEB_CLIENT_ID` is an empty string placeholder — runtime Google Sign-In will fail until a valid OAuth 2.0 Web Client ID is set. Developer must: (1) register SHA-1 of debug keystore in Firebase Console, (2) copy the Web Client ID to `local.properties` and pipe it to `buildConfigField`.
- `GetCredentialException` is caught and passed to `onError`; the error message is not currently surfaced to the user (tracked as M9 polish).
