# Verify — 2.5.3

## Verdict: PASS

| Criterio | Evidenza |
|---|---|
| `consentGivenAt` scritto su Firestore | `UserRepositoryImpl.saveConsent()` chiama `FieldValue.serverTimestamp()` via `SetOptions.merge()` — `grep "consentGivenAt" app/src/main/kotlin/.../UserRepositoryImpl.kt` → riga presente ✅ |
| Bottone "Registrati" disabled senza checkbox | `enabled = ... && termsAccepted` in `RegisterScreen.kt` ✅ |
| Link policy apre URL esterno | `LinkAnnotation.Url(PRIVACY_POLICY_URL)` in AnnotatedString ✅ |
| `saveConsent` chiamato dopo registrazione | `result.onSuccess { user -> saveConsent(user.id) }` in `AuthViewModel.registerOrLink` ✅ |
| Build OK | `./gradlew compileDebugKotlin` → BUILD SUCCESSFUL ✅ |
| Test OK | `./gradlew testDebugUnitTest` → BUILD SUCCESSFUL ✅ |
