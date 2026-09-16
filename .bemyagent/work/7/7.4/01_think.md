# THINK — Task 7.4: FCM Service Routing

## Context
Implementare `onMessageReceived` in `CarezzeMessagingService`: parse del payload FCM →
notifica locale con `PendingIntent` che apre `MainActivity`. La navigazione profonda (deep-link
verso la schermata corretta) richiede modifiche ad `AppNavigation.kt` ma viene fatto in modo
minimale: l'intent porta gli extra, l'Activity li legge e li passa come `startRoute`.

### Context Saturation Check
- [x] **File/path target** — `CarezzeMessagingService.kt`, `NotificationHelper.kt`, `MainActivity.kt`
- [x] **Comportamento atteso** — FCM data map con `type`, `personId`, `therapyId`, `personName`, `medicationName` → notifica locale con tap che apre l'Activity; route target passata come Intent extra per futura integrazione navigation
- [x] **Vincoli** — `PendingIntent.FLAG_IMMUTABLE` richiesto da Android 12+; nessuna modifica a `AppNavigation.kt` per ora (troppo invasivo, rinviato)
- [x] **Dipendenze** — `NotificationHelper` già esistente, `MainActivity` accessibile via Intent

## Approaches Considered
- **A (scelto):** payload FCM data map → parse in service → `PendingIntent` a `MainActivity` con extras → `NotificationHelper.postFcmNotification()`. Routing completo (navigate al TherapyLog) → Intent extra `EXTRA_ROUTE_TYPE` + `EXTRA_PERSON_ID` + `EXTRA_THERAPY_ID`.
- **B:** deep link URI (`carezze://therapy/{personId}/{therapyId}`) nel `PendingIntent`. Pro: più idiomatico Compose Navigation. Contro: richiede `<intent-filter>` nel manifest + deep links in NavHost — scope più largo.

## Selected Approach & Risks
Approccio A — costo **low** (3 file, ~40 righe). Rischi:
1. `PendingIntent.FLAG_MUTABLE` vs `FLAG_IMMUTABLE` → uso `FLAG_IMMUTABLE` come da best practice Android 12+
2. Notifica FCM in foreground non visualizzata automaticamente — mitigato: `onMessageReceived` è invocato solo quando l'app è in foreground → qui postiamo noi la notifica manualmente

## Pre-mortem
1. `RemoteMessage.data` vuota (notifica pura vs data message) → gestione graceful con valori di default
2. Test FCM con emulatore → ok, basta mandare una data message dalla Firebase Console

## Devil's Advocate
Alternativa deep link: richiederebbe `NavDeepLinkBuilder` o URI scheme — più completo ma fuori scope M7. Lo trackio in backlog.

## Verification Plan
- Build compila
- Grep: `onMessageReceived` implementato (non solo super call)
- Grep: `PendingIntent` in CarezzeMessagingService.kt
- Unit test verdi
