# TASK — 3.2: Data PersonRepositoryImpl

**Delivers:** `PersonRepositoryImpl` compilabile e bindato via Hilt — il layer data è pronto per essere iniettato nei ViewModel di M3.3.

## CDM
- ✅ Validation: `./gradlew :app:compileDebugKotlin` → 0 errori

## Checklist
- [ ] `data/repository/PersonRepositoryImpl.kt` — Firestore-only, 5 metodi
- [ ] `di/FirestoreModule.kt` — aggiunge `@Binds PersonRepository → PersonRepositoryImpl`
