---
name: think-5.6.2
description: THINK per task 5.6.2 — Repository pattern Room-first + retry Firestore
metadata:
  type: project
---

# THINK — 5.6.2 Repository pattern

## Context Saturation Check
- ActivityLogRepositoryImpl: Firestore-only, logActivity blocca su .await() ✓
- TherapyRepositoryImpl: createTherapy blocca su .await() ✓
- MedicationLogRepositoryImpl: logMedication blocca su .await() ✓
- DAOs disponibili dopo task 5.6.1 ✓
- channelFlow pattern per merge Room + Firestore listener ✓
- kotlinx.serialization.json per dataJson, medicationsJson, etc. ✓

## Delivers
Le tre operazioni di write (logActivity, createTherapy, logMedication) scrivono Room-first e lanciano Firestore in background con 3 retry esponenziali. observeX ritorna un Flow Room alimentato anche da snapshot Firestore.

## Pre-mortem
1. Listener Firestore leaked → usare channelFlow + awaitClose { listener.remove() }
2. Race condition PENDING→SYNCED → updateSyncStatus è idempotente, OK
3. Sealed class ActivityLog non serializzabile da Room → flat entity con typeKey + dataJson String (kotlinx.serialization)

## Devil's Advocate
Alternativa: in-memory Set<pendingId> invece di Room. Più semplice ma non sopravvive al processo kill. Room è la scelta spec.
