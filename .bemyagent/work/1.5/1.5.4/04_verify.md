# VERIFY — Task 1.5.4: GitHub Actions workflow firebase-distribute.yml

## Verdict: PASS

## Validation Criteria

### ✅ File creato con trigger corretto
**Evidenza:** `grep -n "tags:" .github/workflows/firebase-distribute.yml` → riga 5-6: `tags: ['v*.*.*-beta']`

### ✅ `fetch-depth: 0` presente (per git tag history)
**Evidenza:** riga 15 — `fetch-depth: 0`

### ✅ Tutti i secrets referenziati
**Evidenza:**
- `KEYSTORE_BASE64` (r.37), `KEYSTORE_STORE_PASSWORD` (r.53), `KEYSTORE_ALIAS` (r.54), `KEYSTORE_KEY_PASSWORD` (r.55) — signing
- `FIREBASE_SERVICE_ACCOUNT` (r.61), `FIREBASE_APP_ID` (r.62) — Firebase CLI auth + app ID
- `GOOGLE_SERVICES_JSON` (r.31) — google-services.json inject

### ✅ Nessuna community action di terze parti per l'upload
**Evidenza:** upload via `firebase appdistribution:distribute` (Firebase CLI ufficiale), auth via `GOOGLE_APPLICATION_CREDENTIALS` + service account JSON.

### ✅ Release notes da git log rispetto al tag precedente
**Evidenza:** step "Generate release notes" — `git tag --sort=-version:refname | grep -E '^v[0-9]+...-beta$' | grep -v "^${CURRENT_TAG}$" | head -1` → fallback a `-20 commits` se primo tag.
