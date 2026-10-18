---
task: 2.5
title: Gestione users/{userId} documento Firestore
size: Heavy
status: done
---

# THINK — 2.5

## Context Saturation Check

| Item | Status |
|---|---|
| Firestore schema for `users/{userId}` defined in `03-code-map.md` | ✅ verified |
| `firebase-firestore-ktx` in dependencies | ✅ verified |
| `AuthRepositoryImpl.toDomain()` sets default language/temp unit — these should be overridden by Firestore data when available | ✅ design intent from 2.1 pre-mortem |
| `AuthViewModel` already observes auth state — `UserRepository` will be called after sign-in | ✅ plan |
| Firestore `Timestamp` ↔ `java.time.Instant` compatibility issue noted in `04-tech-stack.md` | ✅ aware — will use `toDate().toInstant()` |
| No Room entity for User yet — document sync is Firestore-only for now (Room entity deferred to when offline persona data is needed) | ✅ decision — keep scope tight |

Unknown items: 0 — proceeding.

## Approach

**UserRepository** (domain interface):
- `suspend fun syncUser(user: User)` — creates or updates the Firestore doc; called after every sign-in
- `suspend fun getUser(userId: String): Result<User>` — one-shot fetch
- `fun observeUser(userId: String): Flow<User?>` — snapshot listener

**UserRepositoryImpl** (data layer):
- Uses `FirebaseFirestore` to read/write `users/{userId}`
- On create: sets all fields from `User` domain model
- On update: `merge()` so partial updates don't wipe unset fields
- `toDomain()` maps Firestore `Map<String, Any>` back to `User`; defaults for missing fields

**Use cases**:
- `SyncUserUseCase` — called from `AuthViewModel` after `authState` changes to `Authenticated`/`Anonymous`
- `GetUserUseCase` — one-shot
- `ObserveUserUseCase` — Flow for Settings screen (task 2.6)

**AuthViewModel update**: call `syncUserUseCase(user)` inside `authState` collection when state transitions to `Authenticated`/`Anonymous`.

**DI**: `FirestoreModule` provides `FirebaseFirestore.getInstance()` and binds `UserRepositoryImpl`.

## Pre-mortem

1. **Anonymous user document collision**: anonymous users get a Firestore doc too. If they later link to an email/Google account, the same `uid` persists — Firestore doc is unchanged. No collision possible.
2. **Concurrent write on first sign-in**: two rapid auth events could trigger two `syncUser()` calls. Mitigation: `setOptions(SetOptions.merge())` is idempotent — the last write wins and all fields are preserved.

## Devil's Advocate

**Alternative**: skip `UserRepository` entirely and embed user prefs in `SharedPreferences` locally. Simpler, no Firestore write on sign-in.
**Why not**: the spec requires `quiet hours` and `language` to be per-user in Firestore (so FCM Cloud Functions respect them from the server side in M7). Firestore is the source of truth. Not pivoting.
