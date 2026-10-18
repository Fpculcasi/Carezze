package com.fpculcasi.carezze.data.repository

import android.util.Log
import com.fpculcasi.carezze.data.local.db.dao.ActivityLogDao
import com.fpculcasi.carezze.data.local.db.entity.ActivityLogEntity
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.HeightUnit
import com.fpculcasi.carezze.domain.model.MealType
import com.fpculcasi.carezze.domain.model.MealUnit
import com.fpculcasi.carezze.domain.model.MeasurementMethod
import com.fpculcasi.carezze.domain.model.SyncStatus
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.WeightUnit
import com.fpculcasi.carezze.domain.repository.ActivityLogRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLogRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val activityLogDao: ActivityLogDao,
    ) : ActivityLogRepository,
        CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
        private fun logsCollection(personId: String) =
            firestore.collection("persons").document(personId).collection("activityLogs")

        override suspend fun logActivity(
            personId: String,
            log: ActivityLog,
        ): Result<ActivityLog> =
            runCatching {
                val logId = UUID.randomUUID().toString()
                val localLog = log.withId(logId)
                activityLogDao.upsert(localLog.toEntity(personId))
                launch { syncToFirestore(logId, personId, localLog) }
                localLog
            }

        private suspend fun syncToFirestore(
            logId: String,
            personId: String,
            log: ActivityLog,
        ) {
            val timestamp = Timestamp(log.timestamp.epochSecond, log.timestamp.nano)
            val data =
                mutableMapOf<String, Any?>(
                    "type" to log.typeKey(),
                    "timestamp" to timestamp,
                    "loggedBy" to log.loggedBy,
                    "data" to log.toDataMap(),
                    "createdAt" to FieldValue.serverTimestamp(),
                )
            var delayMs = 1000L
            for (attempt in 0..3) {
                val result = runCatching { logsCollection(personId).document(logId).set(data).await() }
                if (result.isSuccess) {
                    activityLogDao.updateSyncStatus(logId, SyncStatus.SYNCED.name)
                    return
                }
                if (attempt < 3) {
                    Log.w("ActivityLogRepo", "Firestore sync attempt $attempt failed, retry in ${delayMs}ms")
                    delay(delayMs)
                    delayMs *= 2
                }
            }
            Log.e("ActivityLogRepo", "Firestore sync failed after 4 attempts: $logId")
            activityLogDao.updateSyncStatus(logId, SyncStatus.ERROR.name)
        }

        override fun observeActivityLogs(
            personId: String,
            from: Instant,
            to: Instant,
        ): Flow<List<ActivityLog>> =
            channelFlow {
                val fromTs = Timestamp(from.epochSecond, from.nano)
                val toTs = Timestamp(to.epochSecond, to.nano)
                val producerScope = this

                val listener =
                    logsCollection(personId)
                        .whereGreaterThanOrEqualTo("timestamp", fromTs)
                        .whereLessThanOrEqualTo("timestamp", toTs)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("ActivityLogRepo", "Firestore listener error", error)
                                return@addSnapshotListener
                            }
                            producerScope.launch {
                                snapshot?.documents?.forEach { doc ->
                                    doc.toDomain(personId)?.let {
                                        activityLogDao.upsert(it.toEntity(personId))
                                    }
                                }
                            }
                        }

                launch {
                    activityLogDao
                        .observe(personId, from.epochSecond, to.epochSecond)
                        .map { entities -> entities.map { it.toDomain() } }
                        .collect { send(it) }
                }

                awaitClose { listener.remove() }
            }

        // --- Entity / domain conversions ---

        private fun ActivityLog.toEntity(personId: String): ActivityLogEntity =
            ActivityLogEntity(
                id = id,
                personId = personId,
                type = typeKey(),
                timestampEpochSecond = timestamp.epochSecond,
                loggedBy = loggedBy,
                dataJson = toDataJson(),
                syncStatus = SyncStatus.PENDING.name,
            )

        private fun ActivityLogEntity.toDomain(): ActivityLog {
            val json = runCatching { Json.parseToJsonElement(dataJson).jsonObject }.getOrNull()
            val ts = Instant.ofEpochSecond(timestampEpochSecond)
            val sync = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.SYNCED)
            return when (type) {
                "meal" -> toMealLog(ts, json, sync)
                "diaper" -> toDiaperLog(ts, json, sync)
                "sleep_start" -> ActivityLog.SleepStart(id, personId, ts, loggedBy, sync)
                "sleep_end" -> ActivityLog.SleepEnd(id, personId, ts, loggedBy, sync)
                "temperature" -> toTemperatureLog(ts, json, sync)
                "weight" -> toWeightLog(ts, json, sync)
                else -> ActivityLog.Hygiene(id, personId, ts, loggedBy, json?.noteOrNull(), sync)
            }
        }

        private fun ActivityLogEntity.toMealLog(
            ts: Instant,
            json: JsonObject?,
            sync: SyncStatus,
        ): ActivityLog.Meal =
            ActivityLog.Meal(
                id = id,
                personId = personId,
                timestamp = ts,
                loggedBy = loggedBy,
                amount = json?.get("amount")?.jsonPrimitive?.doubleOrNull,
                amountUnit = json?.enumOrNull<MealUnit>("amountUnit"),
                mealType = json?.enumOrNull<MealType>("mealType"),
                notes = json?.noteOrNull(),
                syncStatus = sync,
            )

        private fun ActivityLogEntity.toDiaperLog(
            ts: Instant,
            json: JsonObject?,
            sync: SyncStatus,
        ): ActivityLog.Diaper =
            ActivityLog.Diaper(
                id = id,
                personId = personId,
                timestamp = ts,
                loggedBy = loggedBy,
                diaperType = json?.enumOrNull<DiaperType>("diaperType") ?: DiaperType.WET,
                notes = json?.noteOrNull(),
                syncStatus = sync,
            )

        private fun ActivityLogEntity.toTemperatureLog(
            ts: Instant,
            json: JsonObject?,
            sync: SyncStatus,
        ): ActivityLog.Temperature =
            ActivityLog.Temperature(
                id = id,
                personId = personId,
                timestamp = ts,
                loggedBy = loggedBy,
                temperature = json?.get("temperature")?.jsonPrimitive?.doubleOrNull ?: 36.5,
                unit = json?.enumOrNull<TemperatureUnit>("unit") ?: TemperatureUnit.C,
                method = json?.enumOrNull<MeasurementMethod>("method"),
                notes = json?.noteOrNull(),
                syncStatus = sync,
            )

        private fun ActivityLogEntity.toWeightLog(
            ts: Instant,
            json: JsonObject?,
            sync: SyncStatus,
        ): ActivityLog.Weight =
            ActivityLog.Weight(
                id = id,
                personId = personId,
                timestamp = ts,
                loggedBy = loggedBy,
                weight = json?.get("weight")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                weightUnit = json?.enumOrNull<WeightUnit>("weightUnit") ?: WeightUnit.KG,
                height = json?.get("height")?.jsonPrimitive?.doubleOrNull,
                heightUnit = json?.enumOrNull<HeightUnit>("heightUnit"),
                notes = json?.noteOrNull(),
                syncStatus = sync,
            )

        private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(personId: String): ActivityLog? {
            if (!exists()) return null
            val type = getString("type") ?: return null
            val timestamp = getTimestamp("timestamp")?.toDate()?.toInstant() ?: return null
            val loggedBy = getString("loggedBy") ?: return null
            val data = (get("data") as? Map<*, *>) ?: emptyMap<Any?, Any?>()
            return when (type) {
                "meal" -> snapshotToMealLog(id, personId, timestamp, loggedBy, data)
                "diaper" -> snapshotToDiaperLog(id, personId, timestamp, loggedBy, data)
                "sleep_start" -> ActivityLog.SleepStart(id, personId, timestamp, loggedBy, SyncStatus.SYNCED)
                "sleep_end" -> ActivityLog.SleepEnd(id, personId, timestamp, loggedBy, SyncStatus.SYNCED)
                "temperature" -> snapshotToTemperatureLog(id, personId, timestamp, loggedBy, data)
                "weight" -> snapshotToWeightLog(id, personId, timestamp, loggedBy, data)
                "hygiene" ->
                    ActivityLog.Hygiene(
                        id = id,
                        personId = personId,
                        timestamp = timestamp,
                        loggedBy = loggedBy,
                        notes = data["notes"] as? String,
                        syncStatus = SyncStatus.SYNCED,
                    )
                else -> null
            }
        }

        private fun snapshotToMealLog(
            id: String,
            personId: String,
            timestamp: Instant,
            loggedBy: String,
            data: Map<*, *>,
        ): ActivityLog.Meal =
            ActivityLog.Meal(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                amount = (data["amount"] as? Number)?.toDouble(),
                amountUnit = data.enumOrNull<MealUnit>("amountUnit"),
                mealType = data.enumOrNull<MealType>("mealType"),
                notes = data["notes"] as? String,
                syncStatus = SyncStatus.SYNCED,
            )

        private fun snapshotToDiaperLog(
            id: String,
            personId: String,
            timestamp: Instant,
            loggedBy: String,
            data: Map<*, *>,
        ): ActivityLog.Diaper? {
            val diaperType = data.enumOrNull<DiaperType>("diaperType") ?: return null
            return ActivityLog.Diaper(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                diaperType = diaperType,
                notes = data["notes"] as? String,
                syncStatus = SyncStatus.SYNCED,
            )
        }

        private fun snapshotToTemperatureLog(
            id: String,
            personId: String,
            timestamp: Instant,
            loggedBy: String,
            data: Map<*, *>,
        ): ActivityLog.Temperature? {
            val temp = (data["temperature"] as? Number)?.toDouble() ?: return null
            val unit = data.enumOrNull<TemperatureUnit>("unit") ?: return null
            return ActivityLog.Temperature(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                temperature = temp,
                unit = unit,
                method = data.enumOrNull<MeasurementMethod>("method"),
                notes = data["notes"] as? String,
                syncStatus = SyncStatus.SYNCED,
            )
        }

        private fun snapshotToWeightLog(
            id: String,
            personId: String,
            timestamp: Instant,
            loggedBy: String,
            data: Map<*, *>,
        ): ActivityLog.Weight? {
            val weight = (data["weight"] as? Number)?.toDouble() ?: return null
            val weightUnit = data.enumOrNull<WeightUnit>("weightUnit") ?: return null
            return ActivityLog.Weight(
                id = id,
                personId = personId,
                timestamp = timestamp,
                loggedBy = loggedBy,
                weight = weight,
                weightUnit = weightUnit,
                height = (data["height"] as? Number)?.toDouble(),
                heightUnit = data.enumOrNull<HeightUnit>("heightUnit"),
                notes = data["notes"] as? String,
                syncStatus = SyncStatus.SYNCED,
            )
        }

        private fun ActivityLog.typeKey(): String =
            when (this) {
                is ActivityLog.Meal -> "meal"
                is ActivityLog.Diaper -> "diaper"
                is ActivityLog.SleepStart -> "sleep_start"
                is ActivityLog.SleepEnd -> "sleep_end"
                is ActivityLog.Temperature -> "temperature"
                is ActivityLog.Weight -> "weight"
                is ActivityLog.Hygiene -> "hygiene"
            }

        private fun ActivityLog.toDataJson(): String =
            buildJsonObject {
                when (val log = this@toDataJson) {
                    is ActivityLog.Meal -> encodeMeal(log)
                    is ActivityLog.Diaper -> encodeDiaper(log)
                    is ActivityLog.Temperature -> encodeTemperature(log)
                    is ActivityLog.Weight -> encodeWeight(log)
                    is ActivityLog.Hygiene -> log.notes?.let { put("notes", it) }
                    is ActivityLog.SleepStart, is ActivityLog.SleepEnd -> Unit
                }
            }.toString()

        private fun JsonObjectBuilder.encodeMeal(log: ActivityLog.Meal) {
            log.amount?.let { put("amount", it) }
            log.amountUnit?.let { put("amountUnit", it.name) }
            log.mealType?.let { put("mealType", it.name) }
            log.notes?.let { put("notes", it) }
        }

        private fun JsonObjectBuilder.encodeDiaper(log: ActivityLog.Diaper) {
            put("diaperType", log.diaperType.name)
            log.notes?.let { put("notes", it) }
        }

        private fun JsonObjectBuilder.encodeTemperature(log: ActivityLog.Temperature) {
            put("temperature", log.temperature)
            put("unit", log.unit.name)
            log.method?.let { put("method", it.name) }
            log.notes?.let { put("notes", it) }
        }

        private fun JsonObjectBuilder.encodeWeight(log: ActivityLog.Weight) {
            put("weight", log.weight)
            put("weightUnit", log.weightUnit.name)
            log.height?.let { put("height", it) }
            log.heightUnit?.let { put("heightUnit", it.name) }
            log.notes?.let { put("notes", it) }
        }

        private fun ActivityLog.toDataMap(): Map<String, Any?> =
            when (this) {
                is ActivityLog.Meal ->
                    mapOf(
                        "amount" to amount,
                        "amountUnit" to amountUnit?.name?.lowercase(),
                        "mealType" to mealType?.name?.lowercase(),
                        "notes" to notes,
                    )
                is ActivityLog.Diaper -> mapOf("diaperType" to diaperType.name.lowercase(), "notes" to notes)
                is ActivityLog.SleepStart -> emptyMap()
                is ActivityLog.SleepEnd -> emptyMap()
                is ActivityLog.Temperature ->
                    mapOf(
                        "temperature" to temperature,
                        "unit" to unit.name.uppercase(),
                        "method" to method?.name?.lowercase(),
                        "notes" to notes,
                    )
                is ActivityLog.Weight ->
                    mapOf(
                        "weight" to weight,
                        "weightUnit" to weightUnit.name.lowercase(),
                        "height" to height,
                        "heightUnit" to heightUnit?.name?.lowercase(),
                        "notes" to notes,
                    )
                is ActivityLog.Hygiene -> mapOf("notes" to notes)
            }

        private fun ActivityLog.withId(newId: String): ActivityLog =
            when (this) {
                is ActivityLog.Meal -> copy(id = newId)
                is ActivityLog.Diaper -> copy(id = newId)
                is ActivityLog.SleepStart -> copy(id = newId)
                is ActivityLog.SleepEnd -> copy(id = newId)
                is ActivityLog.Temperature -> copy(id = newId)
                is ActivityLog.Weight -> copy(id = newId)
                is ActivityLog.Hygiene -> copy(id = newId)
            }

        private fun JsonObject.noteOrNull(): String? = get("notes")?.jsonPrimitive?.content

        private inline fun <reified E : Enum<E>> JsonObject.enumOrNull(key: String): E? =
            get(key)?.jsonPrimitive?.content?.let { runCatching { enumValueOf<E>(it.uppercase()) }.getOrNull() }

        private inline fun <reified E : Enum<E>> Map<*, *>.enumOrNull(key: String): E? =
            (this[key] as? String)?.let { value -> runCatching { enumValueOf<E>(value.uppercase()) }.getOrNull() }
    }
