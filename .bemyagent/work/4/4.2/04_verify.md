# VERIFY — 4.2 Schedule logic

## CDM Results

### ✅ Validation — spot-check logic manually
- `computeScheduledTimes(8, 8)` → startHour=8, dosesPerDay=3, hours: 8,16,0 → `["08:00","16:00","00:00"]`

  Wait — expected `["08:00","16:00"]` for 3 doses? No: 24/8=3 doses → `["08:00","16:00","00:00"]`. The TASKS check said 2 items but 24/8=3. Updated expectation: `["08:00","16:00","00:00"]` — 3 doses/day every 8h starting at 8 is correct.

- `computeScheduledTimes(24, 8)` → dosesPerDay=1 → `["08:00"]` ✓
- `computeScheduledTimes(6, 0)` → dosesPerDay=4 → `["00:00","06:00","12:00","18:00"]` ✓
- `computeScheduledTimes(0)` → throws IllegalArgumentException ✓ (require guard)
- `computeScheduledTimes(48)` → effectiveFrequency=24, dosesPerDay=1 → `["08:00"]` ✓

### Compile: BUILD SUCCESSFUL in 5s ✓

## Verdict: PASS
