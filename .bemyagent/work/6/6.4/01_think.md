# THINK — 6.4: InvitationRepositoryImpl (generateInvitation + observeInvitations + QR)

**Delivers:** `generateInvitation` scrive un invito su Firestore e restituisce `Invitation`; `observeInvitations` emette la lista in real-time; `QrCodeGenerator` converte il codice in `Bitmap`. Fine 6.4 = lo ViewModel di 6.5 può chiamare questi metodi e funzionare.

## Context Saturation Check
- `InvitationRepositoryImpl` esiste con stubs chiari: `TODO("Implemented in 6.4")` ✅
- Schema Firestore `invitations/` documentato in `03-code-map.md` ✅
- ZXing non presente in `libs.versions.toml` ✅ (da aggiungere)
- `InvitationRepository` interface firma verificata ✅
- 0 unknown → procedo

## Files da toccare
1. `InvitationRepositoryImpl.kt` — implementa `generateInvitation` + `observeInvitations`
2. `data/util/QrCodeGenerator.kt` — nuovo file, genera `Bitmap` da code string
3. `gradle/libs.versions.toml` — aggiunge zxing-core version + library alias
4. `app/build.gradle.kts` — aggiunge `implementation(libs.zxing.core)`
5. `.bemyagent/docs/04-tech-stack.md` — aggiorna (new dep + rimuove riferimenti Cloud Functions non più usati)

Size: Heavy (5 file, nuova dipendenza)

## Scelte
- **QR library**: ZXing core (`com.google.zxing:core:3.5.3`) — pura Java, ~300KB, zero Android deps; genera `BitMatrix` → convertiamo in `android.graphics.Bitmap`
- **QrCodeGenerator**: classe separata in `data/util/` — non nel repository (che già dipende da Firestore); non nell'UI (logica non è presentazione); nel data layer come utility Android
- **`generateInvitation` codice**: `('A'..'Z') + ('0'..'9')` random, 8 char, uppercased
- **`observeInvitations`**: `channelFlow { addSnapshotListener(...) }` su query `createdBy == userId`; usa `toInvitation()` extension già presente

## Pre-mortem
1. ZXing `QRCodeWriter` lancia `WriterException` → wrappare in `Result.failure`
2. `channelFlow` con `addSnapshotListener` può perdere eventi se il canale si chiude prima — usare `awaitClose { registration.remove() }` correttamente

## Devil's Advocate
- Alternativa: generare QR lato ViewModel con `remember` — non porta benefici rispetto al utility nella data layer, e accoppia logica bitmap alla composable. Scartata.

## CDM
- 🎯 Drift: spostare la logica QR nel domain layer (violazione clean arch) o dimenticare di chiudere il listener Firestore in `observeInvitations`
- ✅ Validation: progetto compila; `generateInvitation` scrive un documento Firestore con tutti i campi dello schema; `observeInvitations` emette `List<Invitation>` non vuota; `QrCodeGenerator.generate("AB12CD34")` restituisce un `Bitmap` non null con width > 0
- 🔄 Pivot: se ZXing ha conflitti con R8/ProGuard o con le versioni Kotlin → usare ML Kit Barcode per la generazione (già in Firebase BOM)
