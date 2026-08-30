# TASKS — Task 1.5.4: GitHub Actions workflow firebase-distribute.yml

**Delivers:** push di un tag `v1.0.0-beta` triggera il workflow che builda e distribuisce l'APK su Firebase App Distribution ai tester del gruppo `beta-testers`.

## Checklist

- [x] Creare `.github/workflows/firebase-distribute.yml`
- [x] Trigger `on.push.tags: ['v*.*.*-beta']`
- [x] Step: checkout con `fetch-depth: 0` (necessario per `git tag` history)
- [x] Step: JDK 21 + Android SDK (stesso setup di `ci.yml`)
- [x] Step: inject `google-services.json` da secret `GOOGLE_SERVICES_JSON`
- [x] Step: decode keystore Base64 → `/tmp/release.keystore`
- [x] Step: genera release notes da `git log` rispetto al tag beta precedente
- [x] Step: `./gradlew assembleRelease` con env var signing config
- [x] Step: upload Firebase App Distribution via Firebase CLI (service account auth)

## CDM

### ✅ Validation
- File `.github/workflows/firebase-distribute.yml` esiste con trigger corretto
- Tutti i secrets (`KEYSTORE_BASE64`, `KEYSTORE_ALIAS`, `KEYSTORE_STORE_PASSWORD`, `KEYSTORE_KEY_PASSWORD`, `FIREBASE_SERVICE_ACCOUNT`, `FIREBASE_APP_ID`, `GOOGLE_SERVICES_JSON`) referenziati
- Nessuna community action di terze parti usata per upload
