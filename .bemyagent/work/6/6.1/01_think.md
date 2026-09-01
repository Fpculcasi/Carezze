# THINK — 6.1: Domain Invitation layer

## Context Saturation Check
- ✅ Package: `com.fpculcasi.carezze.domain.*` (physical dir `com/carezze/app/`, package namespace diverso — pattern verificato su Person.kt)
- ✅ Pattern use case: `@Inject constructor`, `suspend operator fun invoke` → Result<T>; observe usa `operator fun invoke` → Flow<T>
- ✅ Pattern repository: interface pura Kotlin, nessuna dipendenza Firebase/Android
- ✅ Pattern test: mockk, JUnit5, `runTest` (coroutines-test) — verificato su CreatePersonUseCaseTest
- ✅ Modello Invitation completo in code map + schema Firestore
- ✅ `java.time.Instant` confermato via grep su ActivityLogRepositoryImpl e MedicationLogRepositoryImpl
- ✅ Nessuna nuova dipendenza: mockk, JUnit5, coroutines-test già presenti per le milestone precedenti
0 unknown → nessuna domanda bloccante, si procede.

## Assumption esplicita
- `GenerateInvitationUseCase` non genera il codice 8 char direttamente: delega al repository (il codice potrebbe venire dalla Cloud Function o da un generatore sicuro nell'impl). Il domain use case è un thin wrapper sul contratto repository.

## Pre-mortem
1. **Test non compilano per import mancanti** — mitigazione: uso solo `java.time.Instant`, `io.mockk`, `kotlinx.coroutines.test` già nel classpath di test.
2. **ObserveInvitationsUseCase non listato esplicitamente nel task 6.1** — lo aggiungo comunque perché è nel code map e completa il contratto repository; è il pattern di tutte le milestone precedenti.

## Devil's Advocate
Alternativa: generare il codice 8 char nel domain use case con `kotlin.random.Random` puro. Vantaggio: nessuna dipendenza dal repository per la generazione. Svantaggio: perde la possibilità di usare un CSPRNG lato server (Cloud Function) o un generatore iniettabile nel test. → rimango con la generazione nel repository impl.
