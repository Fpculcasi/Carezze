# TASKS — 4.5.3: EditTherapyScreen

**Delivers:** L'utente può aprire `EditTherapyScreen` da una terapia esistente, trovare il wizard pre-popolato con tutti i campi (nome, data inizio, durata, farmaci), modificarli, e salvare — la terapia aggiornata è persistita via `UpdateTherapyUseCase`.

## Checklist

- [ ] `TherapyViewModel.kt` — aggiunge `GetTherapyUseCase` e `UpdateTherapyUseCase` al costruttore
- [ ] `TherapyViewModel.kt` — aggiunge `loadTherapyForEdit(personId, therapyId)`: resetta `_form`, legge la terapia, popola `AddTherapyFormState` con i dati esistenti
- [ ] `TherapyViewModel.kt` — aggiunge `submitEditTherapy(personId, therapyId, onDone)`: costruisce `Therapy` aggiornata e chiama `UpdateTherapyUseCase`
- [ ] `AppNavigation.kt` — aggiunge `@Serializable data class EditTherapy(val personId: String, val therapyId: String)`
- [ ] `AppNavigation.kt` — aggiunge `composable<EditTherapy> { ... }` che istanzia `EditTherapyScreen`
- [ ] `EditTherapyScreen.kt` (NEW) — wizard 2-step con pre-popolamento, step 1: nome/data/durata, step 2: farmaci, pulsante "Salva modifiche"
- [ ] Build verde (`:app:compileDebugKotlin` senza errori)

## CDM

### ✅ Validation
- `compileDebugKotlin` termina senza errori (evidence: output Gradle)
- `EditTherapyScreen` è raggiungibile via route `EditTherapy(personId, therapyId)` in `AppNavigation`
- `loadTherapyForEdit` popola correttamente i campi (verificabile in `03_execute.log` con grep dei campi)
- `submitEditTherapy` chiama `UpdateTherapyUseCase` e non `CreateTherapyUseCase`
