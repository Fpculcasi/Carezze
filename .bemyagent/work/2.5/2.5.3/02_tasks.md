---
task: 2.5.3
Delivers: Consenso T&C obbligatorio alla registrazione; `consentGivenAt` scritto su Firestore.
---

## Checklist

- [ ] `UserRepository.kt` — aggiungere `saveConsent(userId: String): Result<Unit>`
- [ ] `UserRepositoryImpl.kt` — implementare `saveConsent` con `FieldValue.serverTimestamp()`
- [ ] `SaveConsentUseCase.kt` — nuovo use case in `domain/usecase/user/`
- [ ] `AuthViewModel.kt` — iniettare `SaveConsentUseCase`; chiamarlo dopo `registerOrLink` success
- [ ] `RegisterScreen.kt` — checkbox T&C con link cliccabile; bottone "Registrati" disabled se non accettato

## CDM Validation
- `consentGivenAt` presente su Firestore dopo registrazione (verificabile in console Firebase)
- Bottone "Registrati" disabilitato senza checkbox → verificabile in preview/code
- Link policy apre URL esterno → Intent verifica
