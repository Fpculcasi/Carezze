# THINK — 5.5.6 Distinzione persone: colore locale per-utente

## Delivers
Ogni persona mostra un colore distintivo (scelto dall'utente) in card Dashboard, FilterChip, feed attività e lista Persone. Il colore è salvato localmente via DataStore e non è mai scritto su Firestore.

## Context Saturation Check
- PersonViewModel, DashboardViewModel, FirestoreModule: letti ✅
- DashboardScreen (PersonCard, PersonFilterRow, FeedView): letti ✅
- PersonDetailScreen, PersonListScreen: letti ✅
- DataStore non presente → va aggiunta dipendenza `datastore-preferences:1.1.1` (inferenza da artifact noto)
- Room presente in build.gradle.kts ma nessuna entity — non usato per questo task

**0 incognite bloccanti** → si procede.

## Assunzioni esplicite
- DataStore `1.1.1` compatibile con compileSdk 37 e le coroutines già presenti.
- Il colore è un indice 0-7 in una palette fissa di 8 colori Material3-like.
- `PersonColorStore` è `@Singleton` con `@Inject constructor` — nessun nuovo `@Module` necessario.
- Il `preferencesDataStore` top-level property delegate è thread-safe e non duplica istanze se dichiarato una sola volta nel file.

## Pre-mortem
1. `combine(List<Flow<T>>)` con lista vuota → gestito early-return `flowOf(emptyMap())`
2. Signature aggiornata di `DashboardContent` e `PersonDetailContent` rompe i `@Preview` → li aggiorno nella stessa modifica

## Devil's Advocate
Alternativa: `SharedPreferences` — più semplice ma deprecated e senza Flow nativo. Non adeguato.
Alternativa: Room entity — già in build.gradle.kts ma overkill per key-value; DataStore è semanticamente corretto.

## CDM
- 🎯 **Drift** — toccare file al di fuori dello scope persona/dashboard; aggiungere logica di business lato Firestore.
- ✅ **Validation** — `./gradlew testDebugUnitTest` PASS; colore visibile in card, chip e feed; colore persiste al riavvio app.
- 🔄 **Pivot** — se DataStore causa errori di inizializzazione, fallback su Room entity `PersonColorEntity` (già supportato da build.gradle.kts).

## Approcci considerati
1. **DataStore Preferences** (scelto): dipendenza minima, Flow nativo, semanticamente corretto per preferenze key-value.
2. Room entity: overkill, schema relazionale non necessario.
3. SharedPreferences: deprecated.

## File plan
**Nuovi (4):**
- `data/local/PersonColorStore.kt`
- `domain/usecase/person/ObservePersonColorUseCase.kt`
- `domain/usecase/person/SetPersonColorUseCase.kt`
- `ui/theme/PersonColors.kt`

**Modificati (7):**
- `app/build.gradle.kts` — datastore-preferences
- `ui/person/PersonViewModel.kt` — inject color use cases, expose `personColors`, `setPersonColor`
- `ui/dashboard/DashboardViewModel.kt` — inject `ObservePersonColorUseCase`, `personColors` StateFlow
- `ui/dashboard/DashboardScreen.kt` — propagare `personColors` a PersonCard, FilterChip, FeedItem
- `ui/person/PersonDetailScreen.kt` — color picker row in LazyColumn
- `ui/person/PersonListScreen.kt` — colored dot in PersonItem
- `.bemyagent/docs/03-code-map.md` — aggiornamento
