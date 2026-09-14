# THINK — 6.2: `redeemInvitation` Firestore transaction

## Obiettivo
Implementare `redeemInvitation` come transazione Firestore atomica lato client: verifica codice → validazione (monouso + scadenza) → marca usato + aggiunge userId ai membri del target.

## Context Saturation Check

**Noti:**
- Struttura Firestore persons: `persons/{id}` con `memberIds: Array`, `members: Map<uid, role>` (PersonRepositoryImpl.kt:61-64)
- Struttura Invitation domain model: `Invitation.kt` — code, expiresAt, used, usedBy, usedAt, type (PERSON/THERAPY), targetId, personId
- Pattern Firestore repository: `runCatching { ... .await() }`, `FieldValue.serverTimestamp()`, `runTransaction`
- Decision D-08: validazione lato client, Firestore transaction atomica (no Cloud Function)
- Decision D-05: condivisione a due livelli — PERSON o THERAPY
- Per THERAPY: `persons/{personId}/therapies/{therapyId}` con `memberIds` + `members`

**Assunzioni (0 unknown — nessun blocco):**
- La collection invitations è `invitations/{id}` (flat, non nested sotto persons) — inferring from domain model che non ha personId come campo mandatory
- Per trovare invito by code: query `invitations.whereEqualTo("code", code).limit(1)`
- Per THERAPY sharing: la struttura `memberIds`/`members` esiste anche su therapy doc (D-17 + sharing.md)

## Approcci Considerati

**A. Firestore transaction `runTransaction { }` con read-then-write atomico**
- Pro: atomicità garantita (nessuna race condition su invito monouso)
- Con: Firestore transaction richiede che i documenti siano tutti nello stesso progetto (ok, lo sono)

**B. Batch write con check precondition**
- Pro: più semplice
- Con: non atomico — due utenti potrebbero riscattare lo stesso codice contemporaneamente

→ Scelto A (è la decisione D-08).

## Pre-mortem
1. **Query by code non trova il documento** → `redeemInvitation` deve fare una query (non get by ID), il risultato potrebbe essere vuoto → gestire con `Result.failure()`
2. **Transaction failure su conflitto** → Firestore riprova la transaction automaticamente fino a 5 volte; se fallisce ancora → `Result.failure()` propagato al caller

## Devil's Advocate
Alternativa: Cloud Function callable (già rigettata da D-15 per Spark plan) — chiaramente inferiore, confermato.

## File da toccare
- `data/repository/InvitationRepositoryImpl.kt` (nuovo — solo `redeemInvitation` implementato, altri metodi `TODO()`)
- `data/di/InvitationModule.kt` (nuovo — binding Hilt)
- `test/…/InvitationRepositoryImplRedeemTest.kt` (nuovo — 3 test)
