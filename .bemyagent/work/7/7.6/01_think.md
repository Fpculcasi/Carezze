# THINK — Task 7.6: Cloud Functions — Quiet Hours + FCM Push

## Context
Cloud Function schedulata ogni 15 minuti che:
1. Legge le terapie attive da Firestore
2. Calcola le dosi in scadenza nella prossima finestra (30 min)
3. Per ogni membro della terapia, controlla le `quietHoursStart/End` nel documento `users/{uid}`
4. Invia FCM via Admin SDK solo agli utenti NON in quiet hours

### Context Saturation Check
- [x] **File/path target** — nuova dir `functions/` con TypeScript; `firebase.json` aggiornato; `functions/src/index.ts` con `scheduledMedicationReminder` function
- [x] **Comportamento atteso** — Cloud Scheduler ogni 15 min → query Firestore therapies isActive=true → calcola slot prossimi 30min → per ogni membro: leggi quietHours → if not quiet → `admin.messaging().send({token, notification})`
- [x] **Vincoli** — richiede `firebase-functions`, `firebase-admin` npm packages; la function è in TypeScript; deploy richiede `firebase login` + `firebase deploy --only functions` (azione umana)
- [x] **Dipendenze** — Firebase Admin SDK, `firebase-functions` v5+; Node.js 20

## Approaches Considered
- **A (scelto):** Cloud Scheduler (`onSchedule`) ogni 15 min — stesso intervallo del Worker on-device. Pro: multi-device (raggiungi tutti i membri), quiet hours centralizzate. Contro: latenza max 15 min; costo Firebase (1M invocazioni/mese free).
- **B:** Firestore trigger `onDocumentCreated` su `medicationLogs` quando status=PENDING → improbabile per "dose in arrivo" (il log viene creato quando la dose è confermata, non schedulata). Non applicabile.

## Selected Approach & Risks
Approccio A — costo **heavy** (5 file nuovi, setup infra). Rischi:
1. Timezone: `quietHoursStart/End` sono "HH:mm" senza timezone — mitigo usando il timestamp del documento utente; per ora assumiamo timezone Europe/Rome (hardcoded, da migliorare con campo user)
2. Rate limit FCM → 1 msg/user/intervallo, deduplicato via Cloud Scheduler
3. Deploy richiede azione umana (firebase login) → creo i file, il deploy è `requires-human-action`

## Pre-mortem
1. `firebase-functions` v5 API diversa da v4 → uso l'API v5 (`onSchedule` da `firebase-functions/v2/scheduler`)
2. `fcmTokens` array vuoto → skip utente silenzioso
3. Quiet hours span mezzanotte (es. 22:00-07:00) → gestisco con confronto orario circolare

## Devil's Advocate
Alternative: fare tutto on-device con WorkManager (già fatto in 7.2). La differenza: il Worker 7.2 è per il dispositivo locale, la Cloud Function è per tutti i familiari connessi. Complementari, non alternativi.

## Verification Plan
- `functions/` compilato con TypeScript (`npm run build` in functions/)
- Grep: `onSchedule` in index.ts
- Grep: `quietHours` in index.ts
- Deploy: requires-human-action (firebase deploy --only functions)
