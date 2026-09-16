# TASK — 7.4: FCM Service Routing

Delivers: quando arriva un FCM data message, l'app mostra una notifica locale; al tap, apre MainActivity con gli extra per navigare alla schermata corretta.

## Checklist

- [ ] `data/service/CarezzeMessagingService.kt` — implementare `onMessageReceived`: parse data map → distingui `medication_reminder` / `inactivity_alert` → chiama `NotificationHelper`
- [ ] `data/worker/NotificationHelper.kt` — aggiungere `postFcmNotification(ctx, id, title, body, contentIntent: PendingIntent?)`
- [ ] `MainActivity.kt` — leggere extra `EXTRA_ROUTE_TYPE` da intent e loggarlo (navigazione profonda rinviata a refactor NavigationGraph)

## CDM

### ✅ Validation
- Build debug compila
- Grep: `onMessageReceived` contiene logica (non solo super call)
- Grep: `PendingIntent` in CarezzeMessagingService.kt
- Unit test verdi
