# TASKS — 5.4 UI: Quick Log Bottom Sheet

**Delivers:** Bottom sheet che permette di registrare qualsiasi tipo di evento con 1 tap sulla griglia, espandendo il form per i dettagli opzionali.

## CDM Criteria (Heavy)
- 🎯 **Drift**: campi del form non corrispondono al Firestore schema (tipi, nomi)
- ✅ **Validation**: `./gradlew :app:compileDebugKotlin` exits 0; QuickLogSheet ha @Preview funzionante
- 🔄 **Pivot**: se il ModalBottomSheet causa problemi, usare Dialog come fallback

## Checklist
- [x] `ui/dashboard/QuickLogViewModel.kt` — @HiltViewModel: selectedType, isLoading, isSaved + fun per ogni tipo
- [x] `ui/dashboard/QuickLogSheet.kt` — ModalBottomSheet con griglia tipo + form contestuale
- [x] `./gradlew :app:compileDebugKotlin` → 0 errori — PASS 2026-09-01
