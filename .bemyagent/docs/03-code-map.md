# Code Map — Carezze

## Hot Paths / Performance Critical

- `DashboardViewModel.observePersonsWithRecentActivity()` — snapshot listener aggregato, carico all'avvio
- `ActivityLogRepositoryImpl.streamLogs(personId, from, to)` — query Firestore con range timestamp, indice composito richiesto
- `MedicationLogRepositoryImpl.streamPendingDoses(therapyId)` — polling per countdown widget
- `GlanceWidget.update()` — deve completare in < 500ms (limit Android widget framework)

## Test Coverage Overview

| Milestone | Unit Tests | Instrumented | Coverage |
|---|---|---|---|
| M1 (bootstrap) | 0 | 0 | N/A (nessun domain logic ancora) |
| M2 (auth) — task 2.1 | 5 (SignInAnonymously × 2, ObserveAuthState × 3) | 0 | domain/usecase/auth |
| M2 (auth) — task 2.3 | 6 (SignInWithEmail × 2, CreateUser × 2, LinkWithEmail × 2) | 0 | domain/usecase/auth |
| M2 (auth) — task 2.4 | 4 (SignInWithGoogle × 2, LinkWithGoogle × 2) | 0 | domain/usecase/auth |
| M2 (user) — task 2.5 | 6 (SyncUser × 2, GetUser × 2, ObserveUser × 2) | 0 | domain/usecase/user |
| **M2 total** | **21** | **0** | all auth + user use cases |
| M3 (person) — task 3.5 | 5 (CreatePerson × 1+, GetPerson × 1+, UpdatePerson × 1+, DeletePerson × 1+, ObservePersons × 1+) | 0 | domain/usecase/person |
| **M3 total** | **5** | **0** | all person use cases |
| M4 (therapy) — task 4.7 | 17 (ScheduleCalculator × 6, CreateTherapy × 2, GetTherapy × 2, UpdateTherapy × 2, DeleteTherapy × 2, ObserveTherapies × 1, LogMedication × 2) | 0 | domain/usecase/therapy |
| **M4 total** | **17** | **0** | all therapy use cases + schedule logic |

> Aggiornare dopo ogni milestone. Target: ≥ 80% su domain + data layer.

## Navigazione / Schermate

| Schermata | File (futuro) | Auth richiesta | Descrizione |
|---|---|---|---|
| Splash / Welcome | `ui/auth/WelcomeScreen.kt` ✅ | No | Entry point: locale o registrazione |
| Login | `ui/auth/LoginScreen.kt` ✅ | No | Email/password, Google |
| Register | `ui/auth/RegisterScreen.kt` ✅ | No | Nuovo account + migrazione locale |
| Dashboard | `ui/dashboard/DashboardScreen.kt` ✅ (stub) | No (locale ok) | Card view / Feed view toggle (M5) |
| Quick Log Sheet | `ui/dashboard/QuickLogSheet.kt` | No | Bottom sheet 1-tap event |
| Lista Persone | `ui/person/PersonListScreen.kt` ✅ | No | Tutte le Persone accessibili |
| Dettaglio Persona | `ui/person/PersonDetailScreen.kt` ✅ (stub) | No | Tab: Terapie / Log Attività (M4/M5) |
| Aggiungi/Modifica Persona | `ui/person/EditPersonScreen.kt` ✅ | No | Form Persona |
| Dettaglio Terapia | `ui/therapy/TherapyDetailScreen.kt` ✅ | No | Farmaci, barra progresso, contatore dosi rimanenti |
| Aggiungi Terapia | `ui/therapy/AddTherapyScreen.kt` ✅ | No | Wizard 2-step (info + farmaci) |
| Storico Lista | `ui/history/HistoryListScreen.kt` | No | Feed cronologico filtrato |
| Storico Calendario | `ui/history/HistoryCalendarScreen.kt` | No | Vista mensile + dettaglio giorno |
| Genera Invito | `ui/invitation/GenerateInvitationScreen.kt` | Sì | QR + codice testo |
| Riscatta Invito | `ui/invitation/RedeemInvitationScreen.kt` | No | Scanner QR o input manuale |
| Impostazioni | `ui/settings/SettingsScreen.kt` ✅ | No | Lingua, unità temperatura, quiet hours (Account/notifiche: M7) |

## Use Cases (Domain)

| Use Case | File | Input | Output / Side Effect |
|---|---|---|---|
| `SignInAnonymouslyUseCase` | `domain/usecase/auth/` | — | `Result<User>` anonimo da Firebase Auth |
| `GetCurrentUserUseCase` | `domain/usecase/auth/` | — | `User?` sincrono dal repository |
| `ObserveAuthStateUseCase` | `domain/usecase/auth/` | — | `Flow<User?>` real-time auth state |
| `CreatePersonUseCase` | `domain/usecase/person/` | `name, nickname?` | `Person` creata in Firestore + Room |
| `CreateTherapyUseCase` | `domain/usecase/therapy/` | `personId, name, startDate, duration, medications, userId` | `Result<Therapy>` |
| `GetTherapyUseCase` | `domain/usecase/therapy/` | `personId, therapyId` | `Result<Therapy>` |
| `UpdateTherapyUseCase` | `domain/usecase/therapy/` | `Therapy` | `Result<Unit>` |
| `DeleteTherapyUseCase` | `domain/usecase/therapy/` | `personId, therapyId` | `Result<Unit>` |
| `ObserveTherapiesUseCase` | `domain/usecase/therapy/` | `personId` | `Flow<List<Therapy>>` |
| `ObserveLogsUseCase` | `domain/usecase/therapy/` | `personId, therapyId` | `Flow<List<MedicationLog>>` |
| `LogMedicationUseCase` | `domain/usecase/therapy/` | `personId, therapyId, medicationId, scheduledTime, status, userId` | `Result<MedicationLog>` |
| `LogActivityUseCase` | `personId, ActivityLog` | Log scritto locale + Firestore |
| `GenerateInvitationUseCase` | `type, targetId` | `Invitation` con codice 8 char |
| `RedeemInvitationUseCase` | `code` | Aggiunge utente come Membro |
| `RevokeAccessUseCase` | `targetId, userId` | Rimuove Membro + cancella suoi dati |
| `ObservePersonsUseCase` | `userId` | `Flow<List<Person>>` real-time |
| `ObserveActivityLogsUseCase` | `personId, DateRange` | `Flow<List<ActivityLog>>` |
| `ObservePendingDosesUseCase` | `userId` | `Flow<List<PendingDose>>` per widget |

## Data Schemas — Firestore

> ⚠️ Flag schema mismatches with ⚠️ — the #1 source of integration bugs.

### Schema: users/{userId}

| Campo | Tipo | Esempio | Owner |
|---|---|---|---|
| `email` | `String?` | `"mario@email.it"` | Auth |
| `displayName` | `String` | `"Mario Rossi"` | User |
| `language` | `String` (`"it"` \| `"en"`) | `"it"` | User |
| `temperatureUnit` | `String` (`"C"` \| `"F"`) | `"C"` | User |
| `quietHoursStart` | `String` (`"HH:mm"`) | `"22:00"` | User |
| `quietHoursEnd` | `String` (`"HH:mm"`) | `"07:00"` | User |
| `fcmTokens` | `List<String>` | `["token1"]` | System |
| `personAccess` | `List<String>` | `["pid1","pid2"]` | System |
| `therapyAccess` | `List<String>` | `["pid1_tid1"]` | System |
| `createdAt` | `Timestamp` | — | System |
| `updatedAt` | `Timestamp` | — | System |

### Schema: persons/{personId}

| Campo | Tipo | Esempio | Owner |
|---|---|---|---|
| `name` | `String` | `"Sofia"` | User |
| `nickname` | `String?` | `"Sofi"` | User |
| `createdBy` | `String` (userId) | `"uid123"` | System |
| `members` | `Map<String, String>` | `{"uid1":"owner","uid2":"editor"}` | System |
| `memberIds` | `List<String>` | `["uid1","uid2"]` | System (denorm. per `arrayContains` query) |
| `createdAt` | `Timestamp` | — | System |
| `updatedAt` | `Timestamp` | — | System |

### Schema: persons/{personId}/therapies/{therapyId}

| Campo | Tipo | Esempio | Owner |
|---|---|---|---|
| `name` | `String` | `"Terapia post-intervento"` | User |
| `personId` | `String` | `"pid1"` | System (denorm.) |
| `createdBy` | `String` | `"uid1"` | System |
| `startDate` | `Timestamp` | — | User |
| `duration` | `Map` | `{"type":"fixed","days":7}` | User |
| `isActive` | `Boolean` | `true` | System |
| `members` | `Map<String, String>` | `{"uid1":"owner"}` | System |
| `medications` | `List<Map>` | vedi sotto | User |
| `createdAt` | `Timestamp` | — | System |
| `updatedAt` | `Timestamp` | — | System |

**Struttura elemento `medications[]`:**

| Campo | Tipo | Default | Descrizione |
|---|---|---|---|
| `id` | `String` (UUID) | generato | Identificativo univoco |
| `name` | `String` | — | Nome farmaco (testo libero) |
| `dosage` | `Double` | `1.0` | Quantità per dose |
| `dosageUnit` | `String` | `"pillola"` | Unità (pillola, ml, gocce…) |
| `frequencyHours` | `Int` | `24` | Ogni quante ore |
| `scheduledTimes` | `List<String>` | calcolato | Orari `["HH:mm"]` auto-calcolati, modificabili |
| `startDate` | `Timestamp` | oggi | Prima dose |
| `notes` | `String?` | null | Note opzionali |

### Schema: persons/{personId}/therapies/{therapyId}/medicationLogs/{logId}

| Campo | Tipo | Esempio | Owner |
|---|---|---|---|
| `medicationId` | `String` | `"med-uuid"` | System |
| `scheduledTime` | `Timestamp` | — | System (calcolato) |
| `actualTime` | `Timestamp?` | — | System (al momento della conferma) |
| `status` | `String` (`"taken"` \| `"skipped"` \| `"pending"`) | `"pending"` | System/User |
| `loggedBy` | `String?` (userId) | — | System |
| `createdAt` | `Timestamp` | — | System |

### Schema: persons/{personId}/activityLogs/{logId}

| Campo | Tipo | Valori | Owner |
|---|---|---|---|
| `type` | `String` | `meal` \| `diaper` \| `sleep_start` \| `sleep_end` \| `temperature` \| `weight` \| `hygiene` | User |
| `timestamp` | `Timestamp` | — | User |
| `loggedBy` | `String` (userId) | — | System |
| `data` | `Map` | vedi sotto | User |
| `createdAt` | `Timestamp` | — | System |

**Struttura `data` per tipo:**

| Tipo | Campi | Note |
|---|---|---|
| `meal` | `amount: Double?, amountUnit: String? (ml/min/g), mealType: String? (breast/formula/solid), notes: String?` | amount+unit obbligatori se mealType≠breast |
| `diaper` | `diaperType: String (dry/wet/dirty/both), notes: String?` | — |
| `sleep_start` | *(vuoto)* | Il timestamp del log è l'orario di inizio |
| `sleep_end` | *(vuoto)* | Il timestamp del log è l'orario di fine |
| `temperature` | `temperature: Double, unit: String (C/F), method: String? (axillary/rectal/ear/forehead), notes: String?` | — |
| `weight` | `weight: Double, weightUnit: String (kg/lb), height: Double?, heightUnit: String? (cm/in), notes: String?` | — |
| `hygiene` | `notes: String?` | — |

### Schema: invitations/{inviteId}

| Campo | Tipo | Esempio | Owner |
|---|---|---|---|
| `type` | `String` (`"person"` \| `"therapy"`) | `"person"` | User |
| `targetId` | `String` | `"pid1"` | System |
| `personId` | `String?` | `"pid1"` | System (se type=therapy) |
| `targetName` | `String` | `"Sofia"` | System (denorm.) |
| `createdBy` | `String` | `"uid1"` | System |
| `createdByName` | `String` | `"Mario"` | System (denorm.) |
| `code` | `String` (8 char) | `"AB12CD34"` | System |
| `expiresAt` | `Timestamp` | createdAt + 24h | System |
| `used` | `Boolean` | `false` | System |
| `usedBy` | `String?` | — | System |
| `usedAt` | `Timestamp?` | — | System |
| `createdAt` | `Timestamp` | — | System |

## Domain Models (Kotlin)

> Path: `app/src/main/kotlin/com/carezze/app/domain/model/` | Package: `com.fpculcasi.carezze.domain.model`
> **Note:** Physical dir is `com/carezze/app/` but package is `com.fpculcasi.carezze` (namespace from build.gradle).

```kotlin
// --- User ---
data class User(
    val id: String,
    val email: String?,
    val displayName: String,
    val language: Language,
    val temperatureUnit: TemperatureUnit,
    val quietHoursStart: String,   // "HH:mm"
    val quietHoursEnd: String,     // "HH:mm"
    val personAccess: List<String>,
    val therapyAccess: List<String>
)
enum class Language { IT, EN }
enum class TemperatureUnit { C, F }

// --- Person ---
data class Person(
    val id: String,
    val name: String,
    val nickname: String?,
    val createdBy: String,
    val members: Map<String, MemberRole>
)
enum class MemberRole { OWNER, EDITOR }

// --- Therapy ---
data class Therapy(
    val id: String,
    val personId: String,
    val name: String,
    val createdBy: String,
    val startDate: LocalDate,
    val duration: TherapyDuration,
    val isActive: Boolean,
    val members: Map<String, MemberRole>,
    val medications: List<Medication>
)
sealed class TherapyDuration {
    object Indefinite : TherapyDuration()
    data class Fixed(val days: Int) : TherapyDuration()
}

// --- Medication ---
data class Medication(
    val id: String,
    val name: String,
    val dosage: Double,
    val dosageUnit: String,
    val frequencyHours: Int,
    val scheduledTimes: List<String>, // ["HH:mm"]
    val startDate: LocalDate,
    val notes: String?
)

// --- MedicationLog ---
data class MedicationLog(
    val id: String,
    val therapyId: String,
    val medicationId: String,
    val scheduledTime: Instant,
    val actualTime: Instant?,
    val status: MedicationStatus,
    val loggedBy: String?
)
enum class MedicationStatus { TAKEN, SKIPPED, PENDING }

// --- ActivityLog (sealed) ---
sealed class ActivityLog {
    abstract val id: String
    abstract val personId: String
    abstract val timestamp: Instant
    abstract val loggedBy: String

    data class Meal(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String,
        val amount: Double?, val amountUnit: MealUnit?,
        val mealType: MealType?, val notes: String?
    ) : ActivityLog()

    data class Diaper(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String,
        val diaperType: DiaperType, val notes: String?
    ) : ActivityLog()

    data class SleepStart(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String
    ) : ActivityLog()

    data class SleepEnd(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String
    ) : ActivityLog()

    data class Temperature(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String,
        val temperature: Double, val unit: TemperatureUnit,
        val method: MeasurementMethod?, val notes: String?
    ) : ActivityLog()

    data class Weight(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String,
        val weight: Double, val weightUnit: WeightUnit,
        val height: Double?, val heightUnit: HeightUnit?,
        val notes: String?
    ) : ActivityLog()

    data class Hygiene(
        override val id: String, override val personId: String,
        override val timestamp: Instant, override val loggedBy: String,
        val notes: String?
    ) : ActivityLog()
}
enum class MealUnit { ML, MIN, G }
enum class MealType { BREAST, FORMULA, SOLID }
enum class DiaperType { DRY, WET, DIRTY, BOTH }
enum class MeasurementMethod { AXILLARY, RECTAL, EAR, FOREHEAD }
enum class WeightUnit { KG, LB }
enum class HeightUnit { CM, IN }

// --- Invitation ---
data class Invitation(
    val id: String,
    val type: InvitationType,
    val targetId: String,
    val personId: String?,
    val targetName: String,
    val createdBy: String,
    val createdByName: String,
    val code: String,
    val expiresAt: Instant,
    val used: Boolean,
    val usedBy: String?,
    val usedAt: Instant?
)
enum class InvitationType { PERSON, THERAPY }
```

## Indici Firestore Necessari (firestore.indexes.json)

| Collection | Campi | Query |
|---|---|---|
| `persons/{pid}/activityLogs` | `timestamp DESC` | feed cronologico 30gg |
| `persons/{pid}/therapies/{tid}/medicationLogs` | `scheduledTime DESC` | storico dosi |
| `invitations` | `code ASC, expiresAt ASC` | lookup codice |
