# THINK — 4.5 Progress: barra avanzamento + calendario dosi + contatore rimanenti

## Context Saturation Check
| Item | Status |
|---|---|
| `MedicationLog` has `scheduledTime`, `status` (TAKEN/SKIPPED/PENDING) | ✓ from 4.1 |
| `Therapy.duration` tells us how many days total | ✓ from model |
| `Therapy.startDate` is the start | ✓ from model |
| MedicationLog observations already available via `MedicationLogRepository.observeLogs` | ✓ from 4.3 |

**Unknowns: 0** — proceed.

## Approach
Progress is computed from `MedicationLog` records. Three components:
1. **Progress bar** — `takenCount / totalExpectedDoses` for fixed therapies; for indefinite, show taken count only
2. **Remaining counter** — `totalExpectedDoses - takenCount - skippedCount`
3. **Dose calendar** — a simple 7-day grid showing each day with colored indicators (taken=green, skipped=grey, pending=outline)

All three live in `TherapyDetailScreen` via `TherapyViewModel` which observes `medicationLogs`.

**Files:**
- `TherapyViewModel.kt` — add `logsFor(personId, therapyId)` StateFlow
- `TherapyDetailScreen.kt` — add progress composables

**Sizing: Standard** (2 files)
