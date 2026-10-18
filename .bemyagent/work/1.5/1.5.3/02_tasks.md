# TASKS — Task 1.5.3: Keystore signing config in build.gradle.kts

**Delivers:** `./gradlew assembleRelease` (con env var `KEYSTORE_FILE` settata) produce un APK firmato; senza env var la build non esplode.

## Checklist

- [x] Aggiungere `signingConfigs { create("release") { … } }` block in `android {}` che legge env var
- [x] Applicare `signingConfig = signingConfigs.getByName("release")` al buildType `release`
- [x] Guard: `if (keystoreFilePath != null)` per evitare crash in build locale

## CDM

### ✅ Validation
- `./gradlew assembleDebug` passa senza env var (nessun errore configure-time)
- Diff `build.gradle.kts`: `signingConfigs` block presente + `signingConfig` in release
