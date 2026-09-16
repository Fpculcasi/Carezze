# THINK — Task 7.5: Conferma Familiare

## Context
Azione "Preso" sulla notifica farmaco → chiama `LogMedicationUseCase` (status=TAKEN) → 
aggiorna `MedicationLog` → dismiss della notifica sul dispositivo locale.
Dismiss multi-device (push a tutti i familiari) è gestito lato Cloud Functions (7.6).

### Context Saturation Check
- [x] **File/path target** — nuovo `data/service/NotificationActionReceiver.kt`, `AndroidManifest.xml` (register receiver), `NotificationHelper.kt` (aggiunge action button), `data/worker/MedicationReminderWorker.kt` (passa extras per l'action)
- [x] **Comportamento atteso** — tap "Preso" sulla notifica → BroadcastReceiver → `LogMedicationUseCase(status=TAKEN)` → `NotificationManager.cancel(notifId)`
- [x] **Vincoli** — `BroadcastReceiver` con Hilt → `@AndroidEntryPoint` su receiver; serve `personId`, `therapyId`, `medicationId`, `scheduledTimeEpochSecond` come extras nell'Intent dell'action button
- [x] **Dipendenze** — `LogMedicationUseCase`, `FirebaseAuth` già iniettabili; receiver deve girare in coroutine (usa `GoAsync()`)

## Approaches Considered
- **A (scelto):** `@AndroidEntryPoint BroadcastReceiver` con `goAsync()` per la coroutine; inject `LogMedicationUseCase` + `FirebaseAuth`. Pro: pattern Hilt standard per receiver. Contro: receiver deve essere exported=false nel manifest.
- **B:** `JobIntentService`. Deprecato in API 31+, scartato.

## Selected Approach & Risks
Approccio A — costo **standard** (3 file: receiver, manifest, NotificationHelper update). Rischi:
1. Receiver non registrato nel manifest → crash silenzioso; mitigo con registrazione esplicita
2. `goAsync()` non completa in tempo (10s limit) → raro per una sola Firestore write; accettabile
3. `scheduledTimeEpochSecond` nel PendingIntent potrebbe non essere disponibile dal Worker (il Worker non costruisce la notifica con quel dato) → mitigo: il Worker passa `scheduledTime=0` e la use case usa `Instant.now()` come fallback per `actualTime`

## Pre-mortem
1. Hilt injection fallisce nel receiver → test il receiver con un intent manuale (adb shell am broadcast) per verificare
2. Notifica non rimossa dopo dismiss → mitigo con `NotificationManager.cancel(notifId)` nel receiver

## Devil's Advocate
Alternativa: listener real-time su Firestore (quando il log viene segnato TAKEN da chiunque → dismiss locale). Più corretto per il multi-device case. Ma richiede observer persistente — troppo per M7; rinviato a 7.6.

## Verification Plan
- Build compila
- Grep: `@AndroidEntryPoint` in NotificationActionReceiver.kt
- Grep: `goAsync` in NotificationActionReceiver.kt
- Receiver registrato nel manifest
- Unit test verdi
