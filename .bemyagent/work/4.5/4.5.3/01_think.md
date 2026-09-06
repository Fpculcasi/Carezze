# THINK — 4.5.3: EditTherapyScreen

## Context Saturation Check
| Item | Status |
|---|---|
| `UpdateTherapyUseCase` signature | verified: `invoke(Therapy): Result<Unit>` |
| `GetTherapyUseCase` signature | verified: `invoke(personId, therapyId): Result<Therapy>` |
| `AddTherapyFormState` / `MedicationFormState` structures | verified in `TherapyViewModel.kt` |
| Type-safe navigation pattern | verified in `AppNavigation.kt` |
| `TherapyViewModel` constructor + Hilt setup | verified |
| Domain model `Therapy` fields | verified in `03-code-map.md` |

**Result: 0 unknowns — proceed.**

## Delivers
User navigates to `EditTherapyScreen(personId, therapyId)`, sees the 2-step wizard pre-popolato con i dati della terapia esistente, modifica qualsiasi campo, e salva tramite `UpdateTherapyUseCase`. Al termine torna alla schermata precedente.

## Approach
Extend `TherapyViewModel` (not a new VM) — it already owns all form state and update helpers. Add:
- `GetTherapyUseCase` + `UpdateTherapyUseCase` to constructor
- `loadTherapyForEdit(personId, therapyId)` → legge la terapia e popola `_form`
- `submitEditTherapy(personId, therapyId, onDone)` → chiama `UpdateTherapyUseCase`

Create `EditTherapyScreen.kt` reusing `StepOneContent`, `StepTwoContent`, `MedicationFormItem` (extracted from `AddTherapyScreen` to a shared file or duplicated with minimal changes). Since the step composables are `private` in `AddTherapyScreen`, the cleanest path is to make them `internal` OR duplicate them (which adds coupling). **Decision: keep AddTherapyScreen untouched; duplicate the step composables in EditTherapyScreen** — this avoids coupling two screens to shared internals and matches "surgical scope". Both screens are small.

Add route `@Serializable data class EditTherapy(val personId: String, val therapyId: String)` to `AppNavigation.kt`.

## Devil's Advocate
Separate `EditTherapyViewModel` avoids coupling to AddTherapy's form state. Rejected: would duplicate all form update methods (8 functions) with no behavioral difference. Extending the existing VM is strictly smaller.

## Pre-mortem
1. **Shared form state**: if `_form` has stale Add-flow data when entering Edit. Mitigation: `loadTherapyForEdit` always resets `_form` before populating.
2. **`scheduledTimes` recalc on edit**: when a medication's `frequencyHours` changes, `scheduledTimes` must be recomputed. Mitigation: `submitEditTherapy` calls `ScheduleCalculator.computeScheduledTimes()` same as `submitTherapy`, preserving existing `startDate` per medication.
3. **Hilt injection**: adding two new use cases to the VM constructor is transparent — Hilt resolves them automatically (both are already bound via `@Inject constructor`).

## Files to touch
| File | Change |
|---|---|
| `TherapyViewModel.kt` | Add `GetTherapyUseCase`, `UpdateTherapyUseCase` to constructor; add `loadTherapyForEdit`, `submitEditTherapy` |
| `AppNavigation.kt` | Add `EditTherapy` route + composable entry |
| `EditTherapyScreen.kt` (NEW) | 2-step wizard pre-popolato, pulsante "Salva modifiche" |
