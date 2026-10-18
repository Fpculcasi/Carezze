# TASKS — 4.2 Schedule logic: scheduledTimes[] calculation

**Delivers:** `ScheduleCalculator.computeScheduledTimes(frequencyHours, startHour)` returns a correct `List<String>` of "HH:mm" strings — callable from `CreateTherapyUseCase` and unit-testable standalone.

## CDM Criteria (Standard)
- ✅ **Validation**: `ScheduleCalculator.computeScheduledTimes(8, 8)` returns `["08:00", "16:00"]`; `computeScheduledTimes(24, 8)` returns `["08:00"]`; `computeScheduledTimes(6, 0)` returns `["00:00","06:00","12:00","18:00"]` — verified by unit test in 4.7; for now, verify with a quick Kotlin assertions check.

## Checklist
- [ ] `domain/usecase/therapy/ScheduleCalculator.kt` — `object` with `computeScheduledTimes(frequencyHours: Int, startHour: Int = 8): List<String>`
- [ ] Guard: `frequencyHours <= 0` throws `IllegalArgumentException`
- [ ] Guard: `frequencyHours > 24` → treated as 24 (once per day)
- [ ] Compile check (`compileDebugKotlin` still passes)
