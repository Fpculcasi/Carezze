# THINK — 4.1 Domain: Therapy, Medication, TherapyDuration + Use Cases

## Context Saturation Check
| Item | Status |
|---|---|
| Package root (`com.fpculcasi.carezze`) | ✓ confirmed from PersonRepositoryImpl |
| Domain model spec (`Therapy`, `Medication`, `MedicationLog`) | ✓ in 03-code-map.md |
| DI pattern (`@Binds @Singleton` in `FirestoreModule`) | ✓ confirmed from FirestoreModule.kt |
| java.time availability (minSdk 26) | ✓ API 26 = Android O, no desugaring needed |
| MemberRole enum location | ✓ in `domain/model/Person.kt` — can import directly |

**Unknowns: 0** — proceed.

## Pre-mortem (assume task failed — 3 likeliest causes)
1. **Package/import mismatch**: file is in `com/carezze/app/` dir but package is `com.fpculcasi.carezze` — wrong package declaration causes compile error. Mitigation: copy header verbatim from `PersonRepositoryImpl.kt`.
2. **Hilt graph incomplete**: `TherapyRepository` binding missing or uses wrong annotation → DI graph fails at compile time. Mitigation: add `@Binds @Singleton` in `FirestoreModule` before running build.
3. **`MemberRole` double declaration**: `MemberRole` is already in `Person.kt` — re-declaring it in `Therapy.kt` causes duplicate symbol. Mitigation: import `MemberRole` from `domain.model` (it's in the same package, accessible directly).

## Devil's Advocate
**Alternative:** Merge 4.1 + 4.3 into a single task (create both interface and full Firestore impl at once). Rejected: Firestore impl for nested subcollections (`persons/{pid}/therapies/{tid}`) is non-trivial; keeping domain pure in 4.1 means it can be tested without Firestore (TDD-first for 4.7).

## Approach
1. `domain/model/Therapy.kt` — Therapy, TherapyDuration, Medication, MedicationLog, MedicationStatus
2. `domain/repository/TherapyRepository.kt` — interface
3. `domain/repository/MedicationLogRepository.kt` — interface (no use cases injecting it yet, added to close domain ring)
4. `domain/usecase/therapy/` — 5 use cases (Observe, Get, Create, Update, Delete)
5. `data/repository/TherapyRepositoryImpl.kt` — DI stub (TODO bodies, compiles cleanly)
6. `di/FirestoreModule.kt` — add TherapyRepository binding

**Sizing: Heavy** (10 files, new DI dependency)
