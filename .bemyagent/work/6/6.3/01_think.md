# THINK — 6.3: Client-side cascade `onMemberRevoked`

## Obiettivo
Implementare `revokeAccess` in `InvitationRepositoryImpl`: transazione Firestore atomica per rimuovere il membro dal target (Person/Therapy) + batch delete dei dati del membro (activityLogs o medicationLogs). Aggiornare l'interfaccia con `personId: String?` (necessario per path THERAPY nested).

## Context Saturation Check

**Noti:**
- `InvitationRepositoryImpl.revokeAccess` attualmente è `TODO("Implemented in 6.3")`
- Struttura PERSON: `persons/{personId}` con `memberIds: Array`, `members: Map<uid, role>`
- Struttura THERAPY: `persons/{personId}/therapies/{therapyId}` — path nested richiede `personId`
- ActivityLogs: `persons/{personId}/activityLogs` — campo `loggedBy` identifica l'autore
- MedicationLogs: `persons/{personId}/therapies/{therapyId}/medicationLogs` — campo `loggedBy`
- Pattern transazione 6.2: `firestore.runTransaction { }.await()` con `FieldValue.arrayUnion/arrayRemove`
- Pattern batch delete: `firestore.batch()` + `forEach { batch.delete(it.reference) }` + `batch.commit().await()`
- `revokeAccess` interface attuale: `targetId, type, memberUserId` — manca `personId` per THERAPY

**Assunzione (1 — dichiarata):**
- Il numero di log da cancellare per un membro è << 500 (limite batch Firestore) in casi normali. Se superasse 500, il batch fallirebbe. Accettabile per questa fase: non gestiamo paginazione del batch.

0 unknown bloccanti → si procede.

## Approcci Considerati

**A. Aggiungere `personId: String?` all'interfaccia**
- Pro: path corretto per THERAPY, type-safe, nessuna query aggiuntiva
- Con: breaking change minimo su interface + use case + test esistenti (meccanico)

**B. Collection group query su `therapies` per trovare il personId**
- Pro: interfaccia invariata
- Con: impossibile filtrare per document ID in un collection group; richiede un campo `therapyId` ridondante nel documento

→ Scelto A: più pulito e corretto.

## Pre-mortem
1. **Batch > 500 documenti** — per il volume atteso (utenti beta, famiglia) improbabile; documentato come assunzione, da gestere in futuro con batch chunking
2. **Transaction update fallisce se il documento non ha il campo `members.{uid}`** — mitigato: `FieldValue.delete()` su un campo inesistente è un no-op in Firestore (non lancia eccezione)

## Devil's Advocate
Alternativa: eliminare i log con una Firestore query + delete dentro la transaction. Impossibile: le transaction Firestore richiedono che tutte le reference siano note a priori, non supportano query interne. → batch separato post-transaction è l'unico approccio corretto.

## File da toccare (5 → Heavy)
1. `domain/repository/InvitationRepository.kt` — aggiunge `personId: String?` a `revokeAccess`
2. `domain/usecase/invitation/RevokeAccessUseCase.kt` — thread `personId` through
3. `data/repository/InvitationRepositoryImpl.kt` — implementa `revokeAccess` con transaction + batch cascade
4. `test/.../domain/usecase/invitation/RevokeAccessUseCaseTest.kt` — aggiorna 2 chiamate (aggiunge `personId`)
5. `test/.../data/repository/InvitationRepositoryImplRevokeTest.kt` — nuovo: 3 test `validateRevoke` companion
