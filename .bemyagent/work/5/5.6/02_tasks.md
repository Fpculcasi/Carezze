# TASKS — 5.6 UI: Storico Calendario

**Delivers:** HistoryCalendarScreen con calendario mensile custom (nessuna dipendenza esterna), dot colorato per i giorni con log, dettaglio giorno al tap.

## CDM Criteria (Heavy)
- 🎯 **Drift**: calendario mostra giorni sfasati rispetto al mese reale
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` exits 0; preview compilabile
- 🔄 **Pivot**: se LazyVerticalGrid causa problemi, usare Grid con Row/Column manual

## Checklist
- [x] `ui/history/HistoryCalendarScreen.kt` — calendario custom 7 colonne + dettaglio giorno
- [x] `./gradlew :app:compileDebugKotlin` → 0 errori — PASS 2026-09-01
