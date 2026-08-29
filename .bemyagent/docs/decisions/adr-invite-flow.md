# ADR: Flusso di Invito e Validazione Lato Server
**Data**: 2026-08 | **Status**: accepted

## Context
L'app permette di condividere Persone e Terapie con altri utenti tramite un codice/QR monouso. La validazione del codice deve essere sicura: un utente non autorizzato non deve poter accedere ai dati altrui indovinando o riutilizzando un codice.

## Options Considered

| Opzione | Pro | Contro |
|---|---|---|
| **A) Validazione client-side** (app legge `invitations/` e applica i permessi) | Semplice, nessuna Cloud Function | Bypassabile: il client può scrivere direttamente su `members` se le security rules non sono perfette; race condition su `used=true` |
| **B) Cloud Function HTTPS callable** (client invia codice, la Function valida e applica) | Atomica (transaction Firestore), non bypassabile, centralizzata | Richiede Firebase Blaze plan; latenza aggiuntiva (~500ms) |
| **C) Firestore Security Rules pure** (rules controllano l'invito al momento della write) | No Cloud Functions | Rules molto complesse, difficili da testare, rischio regressioni |

## Decision
**Opzione B** — Cloud Function HTTPS callable `redeemInvitation`.

La Function esegue una transaction Firestore che:
1. Legge il documento `/invitations/{code}` 
2. Verifica: non scaduto, non usato, codice corretto
3. Aggiunge `userId` come `editor` nel documento `persons/{id}` o `therapies/{id}`
4. Aggiorna `users/{userId}.personAccess` o `therapyAccess` (indice denormalizzato)
5. Marca `invitations/{id}.used = true`

Tutto atomicamente — se uno step fallisce, nessuna modifica viene applicata.

## Consequences
- **Positivo**: Nessuna race condition; sicurezza garantita server-side; facile da testare con Firebase Emulator
- **Positivo**: La Function può aggiungere in futuro logica (es. notifica al creatore che qualcuno ha accettato)
- **Negativo**: Richiede Firebase Blaze (pay-as-you-go); costo trascurabile per il volume atteso
- **Negativo**: La Function deve essere deployata e monitorata; aggiunge dipendenza infrastrutturale
