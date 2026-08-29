# THINK — 1.1 Init Android Project

## Context Saturation Check
Fatti noti:
- Kotlin 2.1.x, Compose BOM 2025.x, minSdk 26, targetSdk 35, compileSdk 35
- Package: `com.fpculcasi.carezze` (da 01-overview.md struttura `java/com/carezze/`)
- Gradle 8.14 disponibile localmente: `~/.gradle/wrapper/dists/gradle-8.14-all/.../gradle-8.14/bin/gradle`
- Android SDK: `C:\Users\f.culcasi\AppData\Local\Android\Sdk` (platform android-35)
- Java 21 via Android Studio JDK: `C:\Program Files\Android\Android Studio\jbr`
- No cmdline-tools disponibili; `gradle wrapper` da eseguire con il binario locale

Assunzione esplicita: AGP 8.9.2 + Gradle 8.14 è una coppia compatibile (AGP 8.9.x richiede Gradle 8.11.1+). ✓

Incognite: 0 → si procede.

## Delivers
`./gradlew assembleDebug` → BUILD SUCCESSFUL con APK installabile su Android 8+.
Il progetto non include ancora Firebase, Hilt o Room (quelli sono task 1.2 e 1.3).

## Pre-mortem
1. `gradle wrapper` fallisce per mancanza di `build.gradle.kts` → creare file minimale prima del comando
2. `./gradlew assembleDebug` fallisce per sdk.dir errato in `local.properties` → verificare il path prima di eseguire
3. `compileSdk 35` non trovato → Android SDK ha `android-35` ✓ (già verificato)

## Devil's Advocate
Alternativa: usare Android Studio "New Project" wizard → non scriptabile in questo ambiente, non deterministico, non versionabile come file. La creazione manuale è superiore per portfolio AI.

## CDM
🎯 **Drift**: aggiungere Firebase/Hilt in questo task (appartengono a 1.2/1.3)
✅ **Validation**: `./gradlew assembleDebug` → BUILD SUCCESSFUL; APK presente in `app/build/outputs/apk/debug/`
🔄 **Pivot**: se `gradle wrapper` non genera il JAR correttamente → copiare il JAR da `~/.gradle/wrapper/dists/gradle-8.14-all/.../lib/`
