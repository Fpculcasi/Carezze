# THINK — Task 1.5.4: GitHub Actions workflow firebase-distribute.yml

## Context
Creare il workflow CI/CD `.github/workflows/firebase-distribute.yml` che su push di tag `v*.*.*-beta`: decodifica il keystore, builda il release APK firmato, genera release notes da `git log` dall'ultimo tag beta, e fa upload su Firebase App Distribution → gruppo `beta-testers`.

### Context Saturation Check
- [x] **File/path target** — nuovo file `.github/workflows/firebase-distribute.yml`; riusa `ci.yml` come template strutturale
- [x] **Comportamento atteso** — trigger su tag `v*.*.*-beta`; APK firmato; release notes automatiche; upload Firebase App Distribution
- [x] **Vincoli** — secrets necessari: `KEYSTORE_BASE64`, `KEYSTORE_ALIAS`, `KEYSTORE_STORE_PASSWORD`, `KEYSTORE_KEY_PASSWORD`, `FIREBASE_SERVICE_ACCOUNT`, `FIREBASE_APP_ID`, `GOOGLE_SERVICES_JSON`; Firebase CLI via npm; nessuna community action esterna (sicurezza supply chain)
- [x] **Dipendenze** — `firebase-tools` npm; `android-actions/setup-android@v3`; `actions/setup-java@v4`; stesso JDK 21 del CI

## Approaches Considered
- **A (scelto):** Firebase CLI (`firebase appdistribution:distribute`) con autenticazione via `GOOGLE_APPLICATION_CREDENTIALS` (service account JSON scritto su file temp). No dipendenza da community action.
- **B:** `wzieba/Firebase-Distribution-Github-Action` (community action). Pro: meno boilerplate. Contro: dipendenza da terza parte, rischio supply chain, pinning SHA richiesto.

## Selected Approach & Risks
Approccio A · costo [low] · Rischi:
1. `npm install -g firebase-tools` aggiunge ~1min al workflow → accettabile.
2. Service account JSON scritto su `/tmp/sa.json` → rimosso al termine del job (runner effimero).
3. Release notes vuote se nessun commit tra i tag → `git log` produce stringa vuota, Firebase accetta.

## Pre-mortem
1. `FIREBASE_APP_ID` mancante → `firebase` CLI errore non ovvio → documentato nei secrets richiesti.
2. `fetch-depth: 0` mancante → `git tag` non vede tag precedenti → release notes dal solo HEAD → aggiunto `fetch-depth: 0` nel checkout.
3. APK non trovato nel path atteso → verificare output path con `ls app/build/outputs/apk/release/`.

## Devil's Advocate
Alternativa: GitHub Release + attach APK + link manuale. Pro: nessuna dipendenza Firebase. Contro: nessuna distribuzione automatica ai tester, nessun install link OTA. Inferiore.

## Verification Plan
- File `.github/workflows/firebase-distribute.yml` esiste
- `on.push.tags` = `['v*.*.*-beta']`
- Steps presenti: checkout (fetch-depth:0), JDK 21, Android SDK, google-services.json, keystore decode, release notes, assembleRelease, firebase distribute
- Tutti i secrets referenziati come `${{ secrets.X }}`
