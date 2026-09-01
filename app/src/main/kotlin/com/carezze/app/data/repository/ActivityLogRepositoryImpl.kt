package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.HeightUnit
import com.fpculcasi.carezze.domain.model.MealType
import com.fpculcasi.carezze.domain.model.MealUnit
import com.fpculcasi.carezze.domain.model.MeasurementMethod
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.WeightUnit
import com.fpculcasi.carezze.domain.repository.ActivityLogRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ActivityLogRepository {

    private fun logsCollection(personId: String) =
        firestore.collection("persons").document(personId).collection("activityLogs")

    override suspend fun logActivity(personId: String, log: ActivityLog): Result<ActivityLog> = runCatching {
        val logId = UUID.randomUUID().toString()
        val timestamp = Timestamp(log.timestamp.epochSecond, log.timestamp.nano)
        val data = mutableMapOf<String, Any?>(
            "type" to log.typeKey(),
            "timestamp" to timestamp,
            "loggedBy" to log.loggedBy,
            "data" to log.toDataMap(),
            "createdAt" to FieldValue.serverTimestamp(),
        )
        logsCollection(personId).document(logId).set(data).await()
        log.withId(logId)
    }

    override fun observeActivityLogs(personId: String, from: Instant, to: Instant): Flow<List<ActivityLog>> =
        callbackFlow {
            val fromTs = Timestamp(from.epochSecond, from.nano)
            val toTs = Timestamp(to.epochSecond, to.nano)
            val listener = logsCollection(personId)
                .whereGreaterThanOrEqualTo("timestamp", fromTs)
                .whereLessThanOrEqualTo("timestamp", toTs)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) { close(error); return@addSnapshotListener }
                    val logs = snapshot?.documents?.mapNotNull { doc ->
                        doc.toDomain(personId)
                    } ?: emptyList()
                    trySend(logs)
                }
            awaitClose { listener.remove() }
        }

    private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(personId: String): ActivityLog? {
        if (!exists()) return null
        val type = getString("type") ?: return null
        val timestamp = getTimestamp("timestamp")?.toDate()?.toInstant() ?: return null
        val loggedBy = getString("loggedBy") ?: return null
        @Suppress("UNCHECKED_CAST")
        val data = (get("data") as? Map<String, Any?>) ?: emptyMap()
        return when (type) {
            "meal" -> ActivityLog.Meal(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                amount = (data["amount"] as? Number)?.toDouble(),
                amountUnit = (data["amountUnit"] as? String)?.let { runCatching { MealUnit.valueOf(it.uppercase()) }.getOrNull() },
                mealType = (data["mealType"] as? String)?.let { runCatching { MealType.valueOf(it.uppercase()) }.getOrNull() },
                notes = data["notes"] as? String,
            )
            "diaper" -> ActivityLog.Diaper(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                diaperType = (data["diaperType"] as? String)?.let { runCatching { DiaperType.valueOf(it.uppercase()) }.getOrNull() }
                    ?: return null,
                notes = data["notes"] as? String,
            )
            "sleep_start" -> ActivityLog.SleepStart(id = id, personId = personId, timestamp = timestamp, loggedBy = loggedBy)
            "sleep_end" -> ActivityLog.SleepEnd(id = id, personId = personId, timestamp = timestamp, loggedBy = loggedBy)
            "temperature" -> ActivityLog.Temperature(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                temperature = (data["temperature"] as? Number)?.toDouble() ?: return null,
                unit = (data["unit"] as? String)?.let { runCatching { TemperatureUnit.valueOf(it.uppercase()) }.getOrNull() }
                    ?: return null,
                method = (data["method"] as? String)?.let { runCatching { MeasurementMethod.valueOf(it.uppercase()) }.getOrNull() },
                notes = data["notes"] as? String,
            )
            "weight" -> ActivityLog.Weight(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                weight = (data["weight"] as? Number)?.toDouble() ?: return null,
                weightUnit = (data["weightUnit"] as? String)?.let { runCatching { WeightUnit.valueOf(it.uppercase()) }.getOrNull() }
                    ?: return null,
                height = (data["height"] as? Number)?.toDouble(),
                heightUnit = (data["heightUnit"] as? String)?.let { runCatching { HeightUnit.valueOf(it.uppercase()) }.getOrNull() },
                notes = data["notes"] as? String,
            )
            "hygiene" -> ActivityLog.Hygiene(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                notes = data["notes"] as? String,
            )
            else -> null
        }
    }

    private fun ActivityLog.typeKey(): String = when (this) {
        is ActivityLog.Meal -> "meal"
        is ActivityLog.Diaper -> "diaper"
        is ActivityLog.SleepStart -> "sleep_start"
        is ActivityLog.SleepEnd -> "sleep_end"
        is ActivityLog.Temperature -> "temperature"
        is ActivityLog.Weight -> "weight"
        is ActivityLog.Hygiene -> "hygiene"
    }

    private fun ActivityLog.toDataMap(): Map<String, Any?> = when (this) {
        is ActivityLog.Meal -> mapOf(
            "amount" to amount,
            "amountUnit" to amountUnit?.name?.lowercase(),
            "mealType" to mealType?.name?.lowercase(),
            "notes" to notes,
        )
        is ActivityLog.Diaper -> mapOf(
            "diaperType" to diaperType.name.lowercase(),
            "notes" to notes,
        )
        is ActivityLog.SleepStart -> emptyMap()
        is ActivityLog.SleepEnd -> emptyMap()
        is ActivityLog.Temperature -> mapOf(
            "temperature" to temperature,
            "unit" to unit.name.uppercase(),
            "method" to method?.name?.lowercase(),
            "notes" to notes,
        )
        is ActivityLog.Weight -> mapOf(
            "weight" to weight,
            "weightUnit" to weightUnit.name.lowercase(),
            "height" to height,
            "heightUnit" to heightUnit?.name?.lowercase(),
            "notes" to notes,
        )
        is ActivityLog.Hygiene -> mapOf("notes" to notes)
    }

    private fun ActivityLog.withId(newId: String): ActivityLog = when (this) {
        is ActivityLog.Meal -> copy(id = newId)
        is ActivityLog.Diaper -> copy(id = newId)
        is ActivityLog.SleepStart -> copy(id = newId)
        is ActivityLog.SleepEnd -> copy(id = newId)
        is ActivityLog.Temperature -> copy(id = newId)
        is ActivityLog.Weight -> copy(id = newId)
        is ActivityLog.Hygiene -> copy(id = newId)
    }
}
