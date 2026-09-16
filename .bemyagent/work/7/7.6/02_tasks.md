# TASK — 7.6: Cloud Functions — Quiet Hours + FCM Push

Delivers: Cloud Function schedulata che invia push FCM ai familiari per le dosi imminenti, rispettando le quiet hours di ciascun utente.

## Checklist

- [ ] `functions/package.json` — dipendenze: `firebase-functions@^5`, `firebase-admin@^12`, `typescript`, `@types/node`
- [ ] `functions/tsconfig.json` — config TypeScript per Node 20
- [ ] `functions/src/index.ts` — `scheduledMedicationReminder`: onSchedule ogni 15min, query therapies isActive=true, calcolo slot, check quietHours, FCM send
- [ ] `functions/.eslintrc.js` — lint config minimal
- [ ] `firebase.json` — aggiunta sezione `"functions": { "source": "functions" }`
- [ ] **Deploy**: `firebase deploy --only functions` → `requires-human-action` (richiede firebase login + billing)

## CDM

### 🎯 Drift
Drift = aggiungere logica applicativa nel Worker Android che appartiene alla Cloud Function (duplicazione) o inviare FCM da client Android (non sicuro).

### ✅ Validation
- `npm run build` in `functions/` compila senza errori TypeScript
- Grep: `onSchedule` in functions/src/index.ts
- Grep: `quietHours` in functions/src/index.ts
- `firebase.json` ha sezione `functions`

### 🔄 Pivot
Se il deploy fallisce per billing (Spark plan non supporta Cloud Functions con egress) → pivot su Firebase Extensions o mantieni solo Worker on-device (già funzionale per utente singolo).
