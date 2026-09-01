# THINK — 5.1 Domain: ActivityLog

## Context Saturation Check
- TemperatureUnit already in `domain/model/User.kt` — VERIFIED (read file)
- Package root: `com.fpculcasi.carezze` — VERIFIED
- DI pattern: abstract class @Binds — VERIFIED (read FirestoreModule.kt)
- TherapyRepositoryImpl pattern: callbackFlow + tasks.await() — VERIFIED
- No new dependency needed

## Goal
Create ActivityLog sealed class + repository interface + 2 use cases + stub impl + DI binding.

## Pre-mortem
1. TemperatureUnit re-declaration → anti-drift rule: just use it from same package, no redeclaration
2. Stub impl must compile (no TODO() in abstract methods) → provide TODO() bodies
3. DI: abstract class cannot have @Provides outside companion → already has companion object

## Devil's Advocate
Alternative: put all enums in a separate Enums.kt. Rejected — existing pattern has each model in its own file.

## Assumptions
- `Instant` from `java.time.Instant`
- `Flow` from `kotlinx.coroutines.flow`
