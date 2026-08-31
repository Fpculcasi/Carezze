# TASK — 3.1: Domain Person

**Delivers:** `ObservePersonsUseCase` + `CreatePersonUseCase` + interfaccia `PersonRepository` compilabili — il layer domain M3 è completo e testabile in isolamento.

## CDM
- ✅ Validation: `./gradlew :app:compileDebugKotlin` → 0 errori, 0 warning nuovi

## Checklist
- [ ] `domain/model/Person.kt` — data class Person + enum MemberRole
- [ ] `domain/repository/PersonRepository.kt` — interface con 5 metodi
- [ ] `domain/usecase/person/ObservePersonsUseCase.kt`
- [ ] `domain/usecase/person/GetPersonUseCase.kt`
- [ ] `domain/usecase/person/CreatePersonUseCase.kt`
- [ ] `domain/usecase/person/UpdatePersonUseCase.kt`
- [ ] `domain/usecase/person/DeletePersonUseCase.kt`
