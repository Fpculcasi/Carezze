# TASK — 6.4: InvitationRepositoryImpl (generateInvitation + observeInvitations + QR)

**Delivers:** chiamare `GenerateInvitationUseCase` + `ObserveInvitationsUseCase` da un ViewModel produce rispettivamente un `Invitation` su Firestore e un Flow live; `QrCodeGenerator.generate(code)` restituisce un `Bitmap` valido.

## Checklist

- [ ] `libs.versions.toml` — aggiungi `zxing = "3.5.3"` + `zxing-core` library alias
- [ ] `app/build.gradle.kts` — aggiungi `implementation(libs.zxing.core)`
- [ ] `04-tech-stack.md` — aggiungi ZXing; rimuovi riferimenti Cloud Functions non più accurati
- [ ] `data/util/QrCodeGenerator.kt` — nuovo file: `fun generate(code: String, size: Int = 512): Bitmap`
- [ ] `InvitationRepositoryImpl.kt` — implementa `generateInvitation` (codice random 8 char, write Firestore, return Invitation)
- [ ] `InvitationRepositoryImpl.kt` — implementa `observeInvitations` (channelFlow su query `createdBy == userId`)

## CDM

- ✅ Validation: progetto compila (`./gradlew assembleDebug`); `generateInvitation` scrive documento con tutti i campi schema; `observeInvitations` emette liste; `QrCodeGenerator.generate("AB12CD34")` restituisce Bitmap non null
- 🎯 Drift: logica QR nel domain layer; listener Firestore non chiuso in `awaitClose`
- 🔄 Pivot: se ZXing conflitto con R8 → usare ML Kit Barcode generation
