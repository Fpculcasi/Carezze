# THINK — 6.8: Firestore Security Rules — sharing granulare

**Delivers:** AC6 — un non-membro non può leggere/scrivere dati altrui; therapy-level sharing funziona senza rompere person-level access.

## Modifiche
1. **therapies/read/update**: aggiunto `|| request.auth.uid in resource.data.memberIds` (D-17 evoluzione M6)
2. **medicationLogs/read,create,update**: aggiunto `isTherapyMember(personId, therapyId)` via helper function (2 `get()` — accettabile per volume)
3. **invitations**: creazione ristretta a OWNER del target; update ristretta a `used=false → true, usedBy=uid`; delete disabilitato
4. Helper functions: `isPersonMember`, `isTherapyMember` per ridurre duplicazione

## Build
- firestore.rules è solo deployment — non richiede build Android; verifica manuale della sintassi
