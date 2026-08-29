# TASK — 1.1 Init Android Project

**Delivers:** `./gradlew assembleDebug` → BUILD SUCCESSFUL (APK debug prodotto).

## CDM
🎯 **Drift**: aggiungere Firebase/Hilt (→ task 1.2/1.3) o toccare file oltre la lista sotto
✅ **Validation**: `ls app/build/outputs/apk/debug/*.apk` restituisce il file
🔄 **Pivot**: se gradle wrapper fallisce → copiare JAR da cache `~/.gradle/wrapper/dists/gradle-8.14-all/`

## Checklist

### Gradle scaffolding
- [x] `settings.gradle.kts` — nome progetto + include `:app` + version catalog
- [x] `build.gradle.kts` (root) — plugins AGP + Kotlin + Compose apply false
- [x] `gradle/libs.versions.toml` — versioni base (AGP, Kotlin, Compose, test)
- [x] Eseguito `gradle wrapper --gradle-version 8.14 --distribution-type all`
- [x] `gradlew` + `gradlew.bat` generati da wrapper

### App module
- [x] `app/build.gradle.kts` — applicationId, compileSdk 35, minSdk 26, Compose abilitato
- [x] `app/src/main/AndroidManifest.xml`
- [x] `app/src/main/kotlin/com/carezze/app/MainActivity.kt`
- [x] `app/src/main/res/values/strings.xml`
- [x] `app/src/main/res/values/colors.xml`
- [x] `app/src/main/res/values/themes.xml`
- [x] `app/src/main/res/xml/backup_rules.xml`

### Config
- [x] `local.properties` — sdk.dir per questa macchina (non committato)
- [x] `.gitignore` — esclude `local.properties`, `*.apk`, `build/`, `google-services.json`

### Verifica
- [x] `./gradlew assembleDebug` → BUILD SUCCESSFUL
- [x] APK presente in `app/build/outputs/apk/debug/`
