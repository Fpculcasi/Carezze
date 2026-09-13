---
name: think-5.6.1
description: THINK per task 5.6.1 — SyncStatus + Room infrastruttura
metadata:
  type: project
---

# THINK — 5.6.1 Infrastruttura Room

## Context Saturation Check
- Room dependency già presente in build.gradle.kts ✓
- kotlinx.serialization.json disponibile ✓
- Package: com.fpculcasi.carezze, dir: com/carezze/app ✓
- No Room DB esistente → versione 1
- ActivityLog è sealed class con 7 subtype → serializzare campo dataJson come String JSON
- Therapy e MedicationLog sono data class → colonna syncStatus aggiunta con default SYNCED
- Nessuna incognita critica: 0 domande aperte

## Delivers
SyncStatus enum nel domain + Room entities/DAO/DB/DI compilabili, con colonna syncStatus su ogni entity.

## Pre-mortem
1. Mismatch tipo Instant ↔ Long in Room → uso epochSecond (Long) come colonna
2. Room non supporta sealed class → uso flat entity con typeKey + dataJson (String)
3. Hilt non vede il RoomModule → InstallIn(SingletonComponent) + @Provides

## Devil's Advocate
Alternativa in-memory set invece di Room: più semplice ma non persiste i pending tra riavvii
→ La spec chiede Room esplicitamente → si procede con Room
