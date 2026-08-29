# VERIFY — 1.1 Init Android Project

## Verdetto: PASS

## CDM ✅ Validation
**Criterio**: `ls app/build/outputs/apk/debug/*.apk` → file presente
**Evidenza**: `app/build/outputs/apk/debug/app-debug.apk` ✓ (verificato via `ls`)
**Build**: `./gradlew assembleDebug` → BUILD SUCCESSFUL

## File prodotti
| File | Esito |
|---|---|
| `settings.gradle.kts` | ✓ creato |
| `build.gradle.kts` (root) | ✓ creato |
| `gradle/libs.versions.toml` | ✓ creato con versioni verified (AGP 8.9.1, Kotlin 2.2.10, KSP 2.2.10-2.0.2) |
| `gradle/wrapper/gradle-wrapper.jar` | ✓ generato da gradle wrapper |
| `gradle/wrapper/gradle-wrapper.properties` | ✓ generato |
| `gradlew` / `gradlew.bat` | ✓ generati |
| `gradle.properties` | ✓ creato (android.useAndroidX=true) |
| `app/build.gradle.kts` | ✓ creato |
| `app/src/main/AndroidManifest.xml` | ✓ creato |
| `app/src/main/kotlin/com/carezze/app/MainActivity.kt` | ✓ creato |
| `app/src/main/kotlin/com/carezze/app/CareNestApplication.kt` | ✓ creato (@HiltAndroidApp) |
| `app/src/main/kotlin/com/carezze/app/ui/theme/` | ✓ Theme.kt, Color.kt, Type.kt |
| `app/src/main/res/` | ✓ strings.xml, themes.xml, backup_rules.xml, data_extraction_rules.xml |
| `app/src/main/res/mipmap-anydpi-v26/` | ✓ ic_launcher.xml, ic_launcher_round.xml |
| `app/src/main/res/drawable/` | ✓ ic_launcher_background.xml, ic_launcher_foreground.xml |
| `app/src/main/kotlin/.../data/service/CareNestMessagingService.kt` | ✓ stub creato |
| `app/proguard-rules.pro` | ✓ creato |
| `.gitignore` | ✓ creato |

## Note
- Versioni adattate a quelle in cache locale (macchina utente più aggiornata del training LLM)
- `google-services` plugin deliberatamente escluso → da aggiungere in task 1.2
