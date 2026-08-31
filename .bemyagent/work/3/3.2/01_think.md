# THINK — Task 3.2: Data PersonRepositoryImpl

## Context Saturation Check
- Schema Firestore `persons/{personId}`: `name`, `nickname?`, `createdBy`, `members: Map<String,String>`, `createdAt`, `updatedAt` ✅
- Pattern repo impl: `UserRepositoryImpl` — `callbackFlow` + `addSnapshotListener` + `awaitClose` ✅
- `observePersons(userId)` filtra per `personAccess` list — la query Firestore corretta è `whereArrayContains("members.$userId", ...)` oppure recuperare gli ID da `users/{userId}.personAccess` e fare get singoli. Scelta: query `whereArrayContains` non funziona su Map keys; l'approccio corretto per Firestore è filtrare via `members.{userId}` usando `whereNotEqualTo` o, meglio, query `where("members.{userId}", isNotEqualTo, null)`. In Firestore questo è supportato come field path. ✅ (verificato documentazione pattern)
- Room: non esiste nel progetto → Firestore-only per 3.2. Deviazione documentata.
- UUID generazione: `UUID.randomUUID().toString()` standard Kotlin/Java ✅
- DI: `FirestoreModule.kt` usa `@Binds` abstract — aggiungo PersonRepository binding ✅

## Note deviazione da piano
Il piano dice "Firestore + Room". Room non esiste nel progetto. Aggiungere Room ora sarebbe una nuova dipendenza (new dependency rule §5) con scope fuori M3. Implementazione Firestore-only con offline persistence di Firestore abilitata (già default con Firebase SDK). Nota aggiunta a `05-decisions-and-issues.md` nella stessa risposta.

## Pre-mortem
1. **Query `members.{userId}` su Firestore**: path con campo dinamico. Mitigazione: uso `FieldPath.of("members", userId)` per costruire il field path in modo safe.
2. **Timestamp Firestore → null su create**: `FieldValue.serverTimestamp()` non è noto al momento del return. Mitigazione: `createPerson` ritorna il `Person` domain object costruito lato client con i dati inviati, senza timestamp (timestamp non sono nel domain model).

## Files da toccare
- NEW: `data/repository/PersonRepositoryImpl.kt`
- EDIT: `di/FirestoreModule.kt` — aggiunge binding PersonRepository

**Size: Standard** (2 file)
