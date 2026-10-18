---
name: tasks-5.6.2
description: Checklist task 5.6.2 — Repository Room-first
metadata:
  type: project
---

# TASK — 5.6.2

Delivers: logActivity/createTherapy/logMedication ritornano immediatamente; Firestore write in background con retry; observe ritorna Flow da Room.

## Checklist

- [ ] ActivityLogRepositoryImpl: inject ActivityLogDao; logActivity Room-first + bg sync; observe channelFlow
- [ ] TherapyRepositoryImpl: inject TherapyDao; createTherapy Room-first + bg sync; observe channelFlow
- [ ] MedicationLogRepositoryImpl: inject MedicationLogDao; logMedication Room-first + bg sync; observe channelFlow

## CDM
✅ Validation: i tre file compilano; le operazioni di write non contengono .await() diretto su Firestore nel path principale
