# TASK — 6.2: `redeemInvitation` Firestore transaction

**Delivers:** Un utente può riscattare un codice invito valido: la transazione Firestore marca l'invito come usato e aggiunge l'utente come EDITOR del target (Persona o Terapia), atomicamente. Inviti scaduti o già usati restituiscono `Result.failure()` con messaggio esplicito.

## CDM

### ✅ Validation
- `./gradlew compileDebugKotlin` verde
- `./gradlew :app:testDebugUnitTest --tests "*InvitationRepositoryImplRedeemTest*"` → 3 test pass, 0 fail

## Checklist

- [ ] `data/repository/InvitationRepositoryImpl.kt` — scheletro con `redeemInvitation` implementato + `validateInvitation` companion function; altri metodi `TODO()`
- [ ] `di/FirestoreModule.kt` — aggiunto binding `InvitationRepository → InvitationRepositoryImpl`
- [ ] `test/.../data/repository/InvitationRepositoryImplRedeemTest.kt` — 3 test sulla `validateInvitation` companion (codice valido, già usato, scaduto)

## Verify
- [ ] `./gradlew compileDebugKotlin` verde
- [ ] 3 test pass
