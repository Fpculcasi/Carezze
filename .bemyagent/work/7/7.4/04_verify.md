# VERIFY — 7.4: FCM Service Routing

## Criterio 1 — Build compila
**Evidenza:** `./gradlew assembleDebug` → `BUILD SUCCESSFUL in 1m 2s`
**Verdict:** PASS

## Criterio 2 — onMessageReceived contiene logica
**Evidenza:** CarezzeMessagingService.kt — parse data map, switch su type, chiama NotificationHelper (non solo super call)
**Verdict:** PASS

## Criterio 3 — PendingIntent in CarezzeMessagingService
**Evidenza:** `PendingIntent.getActivity(...)` con FLAG_IMMUTABLE definito nel service
**Verdict:** PASS

## Caveat
Navigazione profonda (open TherapyLog/HistoryList on tap) è un TODO — solo il log in MainActivity per ora. Navigazione completa richiede refactor di `AppNavigation.kt` con deep link, rinviata al polish finale.

## Verdict globale: **PASS_WITH_CAVEATS** — navigazione profonda stub
