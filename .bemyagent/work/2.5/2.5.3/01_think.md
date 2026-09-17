---
task: 2.5.3
title: Consenso T&C / Privacy Policy alla registrazione
size: Heavy
---

## Context Saturation Check
- RegisterScreen: letta ✅
- AuthViewModel: letto ✅
- UserRepository/Impl: letti ✅
- URL policy: GitHub Pages — placeholder `https://fpculcasi.github.io/carezze/privacy` (user confermato)
- Nessun item sconosciuto → 0 unknowns, procedo.

## Delivers
Bottone "Registrati" disabilitato finché l'utente non accetta T&C tramite checkbox; al termine della registrazione email viene scritto `consentGivenAt` su Firestore `users/{uid}`.

## Approccio
1. `UserRepository` + `UserRepositoryImpl`: aggiungere `saveConsent(userId: String): Result<Unit>` che scrive `consentGivenAt = FieldValue.serverTimestamp()`.
2. `SaveConsentUseCase`: use case wrapper (pattern esistente nel codebase).
3. `AuthViewModel.registerOrLink`: iniettare `SaveConsentUseCase`; dopo successo chiamare `saveConsent(currentUser.id)`.
4. `RegisterScreen`: aggiungere checkbox + testo cliccabile con link esterno; disabilitare "Registrati" se non accettato.

## Pre-mortem
- `SetOptions.merge()` in `syncUser` NON sovrascriverà `consentGivenAt` (non è nel map) → campo sicuro.
- Se `saveConsent` fallisce: log errore silenzioso, non blocca UX (la registrazione è già completata).
- Il link esterno apre il browser tramite `Intent(ACTION_VIEW)` — nessuna dipendenza aggiuntiva.

## Devil's Advocate
Alt: salvare `consentGivenAt` direttamente in `syncUser` tramite il modello `User`. Meno buono: `User` diventerebbe dipendente da un timestamp di consenso GDPR che non ha senso per gli anonimi, e `syncUser` gira ad ogni cambio auth state sovrascrivendo il valore.

## CDM
- **Drift**: toccare file fuori dal dominio auth/user
- **Validation**: il campo `consentGivenAt` esiste su Firestore dopo una registrazione test; il bottone "Registrati" è disabled senza checkbox
- **Pivot**: se `FieldValue.serverTimestamp()` causa problemi di serializzazione → usare `Date().time` come fallback
