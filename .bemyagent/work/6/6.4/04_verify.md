# VERIFY — 6.4

## Criteri CDM

### ✅ Validation: progetto compila
- **Comando**: `./gradlew assembleDebug --quiet`
- **Risultato**: exit code 0 — BUILD SUCCESSFUL
- **Verdict**: PASS

### ✅ Validation: generateInvitation scrive tutti i campi dello schema
- **Evidence**: `grep -n "expiresAt\|targetName\|createdBy\|createdByName\|code\|used\|usedBy\|usedAt\|createdAt\|personId" InvitationRepositoryImpl.kt`
- Tutti i campi del schema Firestore presenti nella `mutableMapOf`: type, targetId, targetName, createdBy, createdByName, code, expiresAt, used, usedBy, usedAt, createdAt; personId aggiunto condizionalmente se non null
- **Verdict**: PASS

### ✅ Validation: observeInvitations usa callbackFlow con awaitClose
- **Evidence**: `grep -n "callbackFlow\|awaitClose\|registration.remove" InvitationRepositoryImpl.kt` → linee 27-41 confermano pattern corretto
- Il listener viene chiuso in `awaitClose { registration.remove() }` — no leak
- **Verdict**: PASS

### ✅ Validation: QrCodeGenerator genera Bitmap
- **Evidence**: file `data/util/QrCodeGenerator.kt` creato; usa `QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)` + loop pixel; restituisce `Bitmap.createBitmap(size, size, RGB_565)` non null per input valido
- **Verdict**: PASS

## Verdict finale: PASS
