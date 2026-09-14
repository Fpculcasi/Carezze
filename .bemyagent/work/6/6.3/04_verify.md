# VERIFY — 6.3: Client-side cascade `onMemberRevoked`

## CDM Results

### ✅ Validation

**Criterion 1: `./gradlew compileDebugKotlin` verde**
- PASS — `BUILD SUCCESSFUL in 13s`, 0 errors
- Evidence: `> Task :app:compileDebugKotlin` (executed, not up-to-date)

**Criterion 2: 5 test pass, 0 fail**
- PASS
- Evidence XML report:
  - `InvitationRepositoryImplRevokeTest` → tests=3, failures=0, errors=0
  - `RevokeAccessUseCaseTest` → tests=2, failures=0, errors=0

## Verdict: PASS

## Caveats (non-bloccanti)
- Batch delete non paginato: assume < 500 log per membro (documentato in 01_think.md)
- `personId!!` in `revokeInternal` THERAPY branch è safe perché `validateRevokeArgs` blocca il caso null prima della chiamata
