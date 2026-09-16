# TASK — 7.5: Conferma Familiare

Delivers: tap "Preso" sulla notifica farmaco segna la dose come TAKEN in Firestore/Room e rimuove la notifica dal dispositivo.

## Checklist

- [ ] `data/service/NotificationActionReceiver.kt` — `@AndroidEntryPoint BroadcastReceiver`: legge extras (therapyId, medicationId, personId, scheduledTime, notifId), chiama `LogMedicationUseCase` via `goAsync()`, cancel notifica
- [ ] `AndroidManifest.xml` — registra `NotificationActionReceiver` (exported=false)
- [ ] `data/worker/NotificationHelper.kt` — `addAction` "Preso" alla notifica `postMedicationReminder`; costruisce `PendingIntent` con action + extras
- [ ] `data/worker/MedicationReminderWorker.kt` — passa `personId`, `therapyId`, `medicationId` alla notifica via NotificationHelper

## CDM

### ✅ Validation
- Build debug compila
- Grep: `@AndroidEntryPoint` in NotificationActionReceiver.kt
- Grep: `goAsync` in NotificationActionReceiver.kt
- Receiver nell'AndroidManifest (grep)
- Unit test verdi
