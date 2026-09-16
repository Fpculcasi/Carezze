# TASK — 7.2: MedicationReminderWorker

Delivers: l'app schedula un worker che ogni 15 minuti verifica le dosi in scadenza e posta una notifica locale Android per ciascuna dose non ancora confermata.

## Checklist

- [ ] `gradle/libs.versions.toml` — aggiungere alias `androidx-hilt-work` e `androidx-hilt-compiler` (ver 1.2.0)
- [ ] `app/build.gradle.kts` — aggiungere `implementation(libs.androidx.hilt.work)` + `ksp(libs.androidx.hilt.compiler)`
- [ ] `data/local/db/dao/TherapyDao.kt` — aggiungere `suspend fun getActiveTherapies(): List<TherapyEntity>`
- [ ] `data/local/db/dao/MedicationLogDao.kt` — aggiungere `suspend fun getLogsInWindow(from: Long, to: Long): List<MedicationLogEntity>`
- [ ] `CarezzeApplication.kt` — implementare `Configuration.Provider` + inject `HiltWorkerFactory`
- [ ] `data/worker/NotificationHelper.kt` — helper che posta notifiche locali con `NotificationCompat` (canale MEDICATION_REMINDER)
- [ ] `data/worker/MedicationReminderWorker.kt` — `@HiltWorker` CoroutineWorker: carica terapie attive → calcola dosi window → chiama NotificationHelper

## CDM

### 🎯 Drift
Drift = aggiungere Firestore queries nel Worker (rompe offline-safety) o schedulare con AlarmManager anziché WorkManager.

### ✅ Validation
- Build debug compila senza errori KSP/Hilt
- Grep: `@HiltWorker` in MedicationReminderWorker.kt
- Grep: `HiltWorkerFactory` in CarezzeApplication.kt
- Grep: `getActiveTherapies` in TherapyDao.kt
- Unit test verdi

### 🔄 Pivot
Se la build KSP fallisce con hilt-work 1.2.0 → downgrade a 1.1.0 o switch ad Approach B (factory manuale).
