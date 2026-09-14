# TASK — 6.3: Client-side cascade `onMemberRevoked`

**Delivers:** L'OWNER può revocare l'accesso di un membro: la transazione rimuove il membro da `memberIds`/`members` sul target (Person o Therapy) e il batch delete elimina i log del membro. Fine: `revokeAccess` restituisce `Result.success(Unit)` con Firestore aggiornato.

## CDM

### 🎯 Drift
Modificare la logica di cascade oltre il solo target-document: non toccare i log di altri membri, non eliminare la Person/Therapy stessa.

### ✅ Validation
- `./gradlew compileDebugKotlin` verde
- `./gradlew :app:testDebugUnitTest --tests "*RevokeAccessUseCaseTest*" --tests "*InvitationRepositoryImplRevokeTest*"` → 5 test pass, 0 fail

### 🔄 Pivot
Se `FieldValue.delete()` su un campo map non esiste lancia eccezione in Firestore, riscrivere come `update(mapOf("members" to FieldValue.delete()))` → STOP e rivalutare.

## Checklist

- [ ] `domain/repository/InvitationRepository.kt` — aggiunge `personId: String? = null` a `revokeAccess`
- [ ] `domain/usecase/invitation/RevokeAccessUseCase.kt` — aggiunge `personId: String? = null`, passa al repository
- [ ] `data/repository/InvitationRepositoryImpl.kt` — implementa `revokeAccess`: transaction remove + batch cascade
- [ ] `test/.../domain/usecase/invitation/RevokeAccessUseCaseTest.kt` — aggiorna 2 test (nessun cambiamento funzionale, solo aggiunta `personId = null`)
- [ ] `test/.../data/repository/InvitationRepositoryImplRevokeTest.kt` — nuovo file: 3 test

## Verify
- [ ] `./gradlew compileDebugKotlin` verde
- [ ] 5 test pass (2 use case + 3 repository)
