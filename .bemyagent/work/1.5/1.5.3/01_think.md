# THINK — Task 1.5.3: Keystore signing config in build.gradle.kts

## Context
Configurare la firma del release APK in `app/build.gradle.kts` leggendo il keystore da variabili d'ambiente (injected da GitHub Actions). La firma deve essere no-op in build locali senza le env var, senza rompere `assembleDebug`.

### Context Saturation Check
- [x] **File/path target** — `app/build.gradle.kts` (letto, struttura nota)
- [x] **Comportamento atteso** — `signingConfig` applicato solo se `KEYSTORE_FILE` env var è presente; release build usa R8 + firma
- [x] **Vincoli** — nessuna dipendenza nuova; Gradle KTS; pattern esistente già usa `System.getenv()`
- [x] **Dipendenze** — nessuna libreria aggiuntiva; keystore sarà decodificato nel workflow (1.5.4)

## Approaches Considered
- **A (scelto):** `signingConfigs.create("release")` che legge `KEYSTORE_FILE`, `KEYSTORE_STORE_PASSWORD`, `KEYSTORE_ALIAS`, `KEYSTORE_KEY_PASSWORD` via `System.getenv()`. Se `KEYSTORE_FILE` è null → `storeFile` non settato → Gradle non firma (debug-like behavior).
- **B:** Local.properties per firmare in locale. Pro: workflow locale più ricco. Contro: richiede file aggiuntivo non tracciato, out-of-scope.

## Selected Approach & Risks
Approccio A · costo [low] · Rischio: Gradle KTS richiede che `storeFile` sia non-null o non settato — se settato a null esplode. Mitigazione: guard `if (keystoreFilePath != null)`.

## Pre-mortem
1. `storeFile = file(null)` → crash a configure-time → guard esplicito.
2. La release build locale senza env var non sarà firmata → non installabile su device fisico senza debug key. Accettabile (il workflow firma).

## Devil's Advocate
Alternativa: usare Gradle properties (`-Pkeystore.file=...`) invece di env vars. Meno standard per CI, richiederebbe passaggio esplicito `-P` in ogni step Gradle. L'env var approach è idiomatico per GitHub Actions.

## Verification Plan
- `./gradlew assembleDebug` passa senza env var → build non firmato OK
- Diff `build.gradle.kts` mostra `signingConfigs` block + `signingConfig = signingConfigs.getByName("release")` in release buildType
