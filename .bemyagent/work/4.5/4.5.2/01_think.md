# THINK — 4.5.2
## Context Saturation Check (0 unknown → proceed)
- `TherapyRepositoryImpl` letto ✅ — `updateTherapy` manca `startDate`/`endDate`; `deleteTherapy` non fa cascade; `toDomain` non legge `endDate`
- `MedicationLogRepositoryImpl` letto ✅ — `toDomain` non legge `isManual`
- Interfaccia `TherapyRepository` letta ✅ — nessuna modifica richiesta
- Path Firestore: `persons/{p}/therapies/{t}/medicationLogs/{l}` — confermato in entrambe le impl

## Scope
**Standard** (2 file, no new dep) → TTEV + CDM `✅ Validation`.

## Piano
1. `TherapyRepositoryImpl.updateTherapy` — aggiungere `startDate` e `endDate` al payload Firestore (stessa logica `toTimestamp()` già presente; `endDate` scritto solo se non null)
2. `TherapyRepositoryImpl.deleteTherapy` — cascade: get tutti i docs in `medicationLogs`, delete in batch (<= 500 per batch), poi delete therapy doc
3. `TherapyRepositoryImpl.toDomain` — leggere `endDate` (Timestamp → LocalDate, nullable)
4. `MedicationLogRepositoryImpl.toDomain` — leggere `isManual` (Boolean, default false)

## Pre-mortem
- Batch Firestore ≤ 500 op: loop su ogni 500 docs → commit → prossimo batch. Mitigation: implementare con `chunked(500)`.
- `endDate` null in Firestore: `getTimestamp("endDate")` ritorna null → `endDate = null` nel domain. Compatibile col costruttore.

## Devil's Advocate
Alternativa: Cloud Function su trigger `onDelete` per cascade. Scartata — richiede Blaze + deploy esterno; client-side batch è consistente col pattern attuale e sufficiente per volumi terapia.
