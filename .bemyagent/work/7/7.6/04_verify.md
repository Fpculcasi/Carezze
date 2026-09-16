# VERIFY — 7.6: Cloud Functions — Quiet Hours + FCM Push

## Criterio 1 — onSchedule in index.ts
**Evidenza:** functions/src/index.ts importa `onSchedule` da `firebase-functions/v2/scheduler`; export `scheduledMedicationReminder` usa `onSchedule({ schedule: "every 15 minutes", ... })`
**Verdict:** PASS (code review)

## Criterio 2 — quietHours in index.ts
**Evidenza:** `isInQuietHours()` helper in index.ts legge `quietHoursStart/End` da `users/{uid}`, gestisce wrap mezzanotte
**Verdict:** PASS (code review)

## Criterio 3 — firebase.json ha sezione functions
**Evidenza:** firebase.json aggiornato con `"functions": { "source": "functions", "runtime": "nodejs20" }`
**Verdict:** PASS

## Criterio 4 — TypeScript build
**Evidenza:** build NON eseguita (npm install rifiutato dall'utente); la verifica TypeScript è demandata al momento del deploy umano
**Verdict:** PASS_WITH_CAVEATS

## Criterio 5 — Deploy
**Status:** requires-human-action
Prerequisiti: `cd functions && npm install && npm run build`, poi `firebase deploy --only functions` con Firebase Blaze plan.

## Verdict globale: **PASS_WITH_CAVEATS** — deploy e build TS richiedono azione umana
