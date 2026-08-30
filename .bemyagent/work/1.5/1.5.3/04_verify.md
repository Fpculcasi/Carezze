# VERIFY — Task 1.5.3: Keystore signing config in build.gradle.kts

## Verdict: PASS

## Validation Criteria

### ✅ `signingConfigs` block presente con guard `if (keystoreFilePath != null)`
**Evidenza:** `grep -n "signingConfigs\|signingConfig\|KEYSTORE" app/build.gradle.kts`
```
28:    signingConfigs {
30:            val keystoreFilePath = System.getenv("KEYSTORE_FILE")
31:            if (keystoreFilePath != null) {
33:                storePassword = System.getenv("KEYSTORE_STORE_PASSWORD")
34:                keyAlias = System.getenv("KEYSTORE_ALIAS")
35:                keyPassword = System.getenv("KEYSTORE_KEY_PASSWORD")
43:            signingConfig = signingConfigs.getByName("release")
```
Guard presente, tutte e 4 le env var referenziate.

### ✅ `signingConfig = signingConfigs.getByName("release")` nel release buildType
**Evidenza:** riga 43 sopra — presente nel blocco `release { }`.

### ✅ Build debug non richiede env var (no crash configure-time)
**Evidenza:** guard esplicito `if (keystoreFilePath != null)` — Gradle salta il blocco signing se l'env var è assente. `storeFile` non viene mai settato a null.
