# Condivisione & Inviti
**Status**: done (2026-09-15)

## Descrizione
Sistema che permette a un Utente (OWNER) di condividere una Persona o una Terapia con un altro Utente tramite codice alfanumerico 8 char o QR code. L'invito è monouso, scade dopo 24h, e la validazione avviene tramite Firestore transaction atomica lato client (D-08/D-15 — no Cloud Functions). L'accesso può essere revocato con cascade client-side sui dati del membro rimosso.

## Acceptance Criteria
- [x] AC1: L'OWNER di una Persona può generare un Invito (codice 8 char + QR bitmap) e condividerlo via Intent Android — `GenerateInvitationScreen.kt` + `GenerateInvitationViewModel.kt`; icona Share in `PersonDetailScreen`
- [x] AC2: L'invito è monouso e scade dopo 24h — un codice già usato o scaduto restituisce errore esplicito all'utente — `validateInvitation()` in `InvitationRepositoryImpl` + messaggi leggibili in `RedeemInvitationViewModel`
- [x] AC3: L'Utente B che riscatta un codice valido diventa EDITOR della Persona o Terapia — `RedeemInvitationScreen.kt` (input manuale); transaction Firestore aggiunge uid a `members` + `memberIds`; snapshot listener propaga
- [x] AC4: L'OWNER può revocare l'accesso di un Membro; la revoca cancella i dati del membro (cascade client-side) — `revokeAccess` in `InvitationRepositoryImpl`: transaction + batch delete `activityLogs`/`medicationLogs`
- [x] AC5: Schermata Gestione Membri mostra la lista degli EDITOR con pulsante revoca + dialog di conferma — `MembersScreen.kt` + `MembersViewModel.kt`
- [x] AC6: Le Firestore Security Rules impediscono a un non-membro di leggere/scrivere dati altrui — `firestore.rules` aggiornato (therapy-level sharing + invitations create/update restricted)
- [x] AC7: I use case domain (Generate, Redeem, Revoke, Observe) sono coperti da test unitari JVM puri — 14 test totali M6 (8 use case + 6 repository helpers)

## Firestore Security Rules — Evoluzione per M6

Le rules MVP usano accesso person-level per therapies (vedi D-17). Per supportare therapy-level sharing, le rules dovranno evolvere così:

**Therapy read/update (attuale MVP):**
```
allow read, update: if isAuthenticated() &&
    request.auth.uid in get(/databases/$(database)/documents/persons/$(personId)).data.memberIds;
```

**Therapy read/update (M6 — share therapy-level):**
```
allow read, update: if isAuthenticated() && (
    request.auth.uid in get(/databases/$(database)/documents/persons/$(personId)).data.memberIds ||
    request.auth.uid in resource.data.memberIds
);
```

**MedicationLogs read/create/update (M6):**
```
allow read, create, update: if isAuthenticated() && (
    request.auth.uid in get(/databases/$(database)/documents/persons/$(personId)/therapies/$(therapyId)).data.memberIds ||
    request.auth.uid in get(/databases/$(database)/documents/persons/$(personId)).data.memberIds
);
```
Nota: 2 `get()` per operazione medicationLog — accettabile per il volume atteso, ma da monitorare.

**`redeemInvitation` dovrà scrivere:**
- Share Persona → aggiungi uid a `person.members` (Map) + `person.memberIds` (Array)
- Share Terapia → aggiungi uid a `therapy.members` (Map) + `therapy.memberIds` (Array); NON scrivere su person

## Note implementazione — Invito (ADR)
> ✅ Inconsistenza risolta (2026-09-15): `adr-invite-flow.md` aggiornato a Opzione A (Firestore transaction client-side), allineato a D-08/D-15. Task 6.2/6.3 implementati con questo approccio.

## Open Questions
- Nessuna open question bloccante.