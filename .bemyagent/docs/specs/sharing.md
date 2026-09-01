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

## Open Questions
- Nessuna open question bloccante.