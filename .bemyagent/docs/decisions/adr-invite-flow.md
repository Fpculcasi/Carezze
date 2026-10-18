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
**Opzione A** — Firestore transaction client-side (vedi D-08, D-15).

~~Opzione B (Cloud Function) era il piano originale ma è stata scartata in D-15 (Firebase Spark plan + preferenza architetturale per zero server-side logic).~~

La transaction `redeemInvitation` eseguita nel client:
1. Legge il documento `/invitations/{code}` (query per codice, poi fetch)
2. Verifica: `used=false` + `expiresAt > now()`
3. Scrive in un'unica transaction Firestore:
   - `invitations/{id}`: `used=true`, `usedBy`, `usedAt`
   - `persons/{id}` o `therapies/{id}`: `memberIds` arrayUnion, `members.{uid}="EDITOR"`
4. Le Security Rules bloccano write non autorizzate come secondo livello di difesa

`revokeAccess` è analogamente client-side: transaction Firestore per rimuovere il membro + batch delete dei log.

## Consequences
- **Positivo**: Piano Firebase Spark (gratuito); zero cold start; funziona offline (la transaction viene re-tentata)
- **Positivo**: Nessuna infrastruttura da deployare; architettura più portabile
- **Negativo**: Race condition teorica se due client riscattano lo stesso codice nello stesso ms — Firestore transaction serializza le write, quindi la seconda fallisce con errore (comportamento corretto)
- **Negativo**: La logica di validazione è nel client — le Security Rules devono essere rigorose per compensare
