# TASK — 6.1: Domain Invitation layer

**Delivers:** Use cases invitation puri e testabili (`GenerateInvitationUseCase`, `RedeemInvitationUseCase`, `RevokeAccessUseCase`, `ObserveInvitationsUseCase`) invocabili in isolamento da test JVM senza Firebase, con 8 test verdi.

## CDM

### 🎯 Drift
Aggiungere dipendenze Firebase, Room o Android SDK nel package `domain/` — il domain layer deve rimanere puro Kotlin.

### ✅ Validation
- `./gradlew compileDebugKotlin` verde senza errori
- `./gradlew test --tests "*.invitation.*"` → 8 test pass, 0 fail

### 🔄 Pivot
Se il modello `Invitation` richiede campi aggiuntivi non presenti nel code map (scoperti durante la scrittura dell'impl), fermarsi e aggiornare `03-code-map.md` prima di continuare.

## Checklist

### Domain model
- [x] `domain/model/Invitation.kt` — data class + `InvitationType` enum
- [x] `domain/repository/InvitationRepository.kt` — interface pura

### Use Cases
- [x] `domain/usecase/invitation/GenerateInvitationUseCase.kt`
- [x] `domain/usecase/invitation/RedeemInvitationUseCase.kt`
- [x] `domain/usecase/invitation/RevokeAccessUseCase.kt`
- [x] `domain/usecase/invitation/ObserveInvitationsUseCase.kt`

### Test
- [x] `GenerateInvitationUseCaseTest.kt` (2 test)
- [x] `RedeemInvitationUseCaseTest.kt` (2 test)
- [x] `RevokeAccessUseCaseTest.kt` (2 test)
- [x] `ObserveInvitationsUseCaseTest.kt` (2 test)

### Verify
- [ ] `./gradlew compileDebugKotlin` verde
- [ ] `./gradlew test --tests "*.invitation.*"` → 8 pass
