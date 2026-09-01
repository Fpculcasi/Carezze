# THINK — 5.6 UI: Storico Calendario

## Context Saturation Check
| Item | Status |
|---|---|
| `HistoryViewModel` già creato in 5.5 con `logs` StateFlow | ✓ |
| LazyVerticalGrid disponibile in Compose (non experimental in Compose 1.5+) | ✓ |
| `LocalDate` API disponibile (minSdk 26) | ✓ |

**Unknowns: 0** — proceed.

## Pre-mortem
1. **Offset del primo giorno del mese** → `YearMonth.atDay(1).dayOfWeek` per calcolare le celle vuote iniziali (lunedì = 0).
2. **BottomSheet per dettaglio giorno** → usare `AnimatedVisibility` o un semplice `if (selectedDay != null)` che mostra una Card espandibile sotto il calendario.

## Devil's Advocate
**Alternativa:** libreria `Compose Kalendar` o simili. Rifiutato: aggiunge dipendenza esterna, il calendario 7×N con LazyVerticalGrid è sufficiente per i requisiti.

## Approach
- Grid 7 colonne: celle vuote iniziali + giorni del mese
- Tap su giorno → `selectedDay` state → mostra lista log di quel giorno sotto
