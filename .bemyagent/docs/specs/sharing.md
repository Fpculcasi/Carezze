# Condivisione & Inviti
**Status**: in-progress

## Descrizione
Sistema che permette a un Utente (OWNER) di condividere una Persona o una Terapia con un altro Utente tramite codice alfanumerico 8 char o QR code. L'invito è monouso, scade dopo 24h, e la validazione avviene tramite Firebase Cloud Function callable (`redeemInvitation`) per garantire atomicità server-side. L'accesso può essere revocato con cascade sui dati del membro rimosso tramite Firestore trigger (`onMemberRevoked`).

## Acceptance Criteria
- [ ] AC1: L'OWNER di una Persona può generare un Invito (codice 8 char + QR bitmap) e condividerlo via Intent Android
- [ ] AC2: L'invito è monouso e scade dopo 24h — un codice già usato o scaduto restituisce errore esplicito all'utente
- [ ] AC3: L'Utente B che riscatta un codice valido diventa EDITOR della Persona o Terapia in < 3s (snapshot listener propaga)
- [ ] AC4: L'OWNER può revocare l'accesso di un Membro; la revoca cancella i dati del membro rimosso (cascade via Cloud Function trigger)
- [ ] AC5: Schermata Gestione Membri mostra la lista degli EDITOR con pulsante revoca + dialog di conferma
- [ ] AC6: Le Firestore Security Rules impediscono a un non-membro di leggere/scrivere dati altrui (invitations, persons, therapies)
- [ ] AC7: I use case domain (Generate, Redeem, Revoke, Observe) sono coperti da test unitari JVM puri (mockk, zero dipendenze Firebase)

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
> ⚠️ Inconsistenza da risolvere prima di M6: `adr-invite-flow.md` ha status "accepted" per Cloud Function callable, ma D-15 in `05-decisions-and-issues.md` decide "No Cloud Functions (Spark plan)". Allineare le due decisioni prima di iniziare la milestone.

## Open Questions
- Nessuna open question bloccante.