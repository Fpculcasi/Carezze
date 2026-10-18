# THINK — Task 3.1: Domain Person

## Context Saturation Check
- Person schema: verificato in `03-code-map.md` — campi `name`, `nickname?`, `createdBy`, `members: Map<String,MemberRole>`, `createdAt`, `updatedAt` ✅
- Package convention: `com.fpculcasi.carezze.domain.model` (da User.kt) ✅
- Pattern use case: classe con `@Inject constructor` + `operator fun invoke` (da ObserveUserUseCase) ✅
- Pattern repository interface: suspend + Flow (da UserRepository.kt) ✅
- Nessun unknown: 0 item — continuo

## Scope
Task 3.1 crea il layer domain puro (no Android, no DI):
- `Person.kt` (model + MemberRole enum)
- `PersonRepository.kt` (interface)
- 5 use cases: ObservePersons, GetPerson, CreatePerson, UpdatePerson, DeletePerson

## Pre-mortem
1. **Enum MemberRole duplicato** — `MemberRole` potrebbe essere definito altrove. Mitigazione: grepping su tutto il progetto prima di scrivere → nessun risultato, creo in Person.kt.
2. **Use case granularità** — UpdatePerson potrebbe dover aggiornare anche `personAccess` su `users/`. Mitigazione: UpdatePerson aggiorna solo `persons/{id}`, il sync di `personAccess` è responsabilità del data layer.

## Devil's Advocate
Alternativa: unico `PersonRepository` con metodi inline invece di use cases separati. Scartata: il pattern stabilito nel progetto usa use cases granulari (vedi auth/user).

## Files da toccare
- NEW: `domain/model/Person.kt`
- NEW: `domain/repository/PersonRepository.kt`
- NEW: `domain/usecase/person/ObservePersonsUseCase.kt`
- NEW: `domain/usecase/person/GetPersonUseCase.kt`
- NEW: `domain/usecase/person/CreatePersonUseCase.kt`
- NEW: `domain/usecase/person/UpdatePersonUseCase.kt`
- NEW: `domain/usecase/person/DeletePersonUseCase.kt`

**Size: Heavy** (7 file nuovi, logica ripetitiva)
