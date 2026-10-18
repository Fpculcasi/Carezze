package com.fpculcasi.carezze.data.repository

import android.util.Log
import com.fpculcasi.carezze.data.local.db.dao.TherapyDao
import com.fpculcasi.carezze.data.local.db.entity.TherapyEntity
import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.SyncStatus
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TherapyRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val therapyDao: TherapyDao,
    ) : TherapyRepository,
        CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
        private fun therapiesCollection(personId: String) =
            firestore.collection("persons").document(personId).collection("therapies")

        override fun observeTherapies(personId: String): Flow<List<Therapy>> =
            channelFlow {
                val producerScope = this
                val listener =
                    therapiesCollection(personId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("TherapyRepo", "Firestore listener error", error)
                                return@addSnapshotListener
                            }
                            producerScope.launch {
                                snapshot?.documents?.mapNotNull { it.toDomain() }?.forEach { therapy ->
                                    therapyDao.upsert(therapy.toEntity())
                                }
                            }
                        }
                launch {
                    therapyDao
                        .observe(personId)
                        .map { entities -> entities.mapNotNull { it.toDomain() } }
                        .collect { send(it) }
                }
                awaitClose { listener.remove() }
            }

        override suspend fun getTherapy(
            personId: String,
            therapyId: String,
        ): Result<Therapy> =
            runCatching {
                val snapshot = therapiesCollection(personId).document(therapyId).get().await()
                snapshot.toDomain() ?: error("Therapy not found: $therapyId")
            }

        override suspend fun createTherapy(
            personId: String,
            name: String,
            startDate: LocalDate,
            duration: TherapyDuration,
            medications: List<Medication>,
            userId: String,
        ): Result<Therapy> =
            runCatching {
                val therapyId = UUID.randomUUID().toString()
                val localTherapy =
                    Therapy(
                        id = therapyId,
                        personId = personId,
                        name = name,
                        createdBy = userId,
                        startDate = startDate,
                        duration = duration,
                        isActive = true,
                        members = mapOf(userId to MemberRole.OWNER),
                        medications = medications,
                        syncStatus = SyncStatus.PENDING,
                    )
                therapyDao.upsert(localTherapy.toEntity())
                launch { syncTherapyToFirestore(therapyId, personId, localTherapy, userId) }
                localTherapy
            }

        private suspend fun syncTherapyToFirestore(
            therapyId: String,
            personId: String,
            therapy: Therapy,
            userId: String,
        ) {
            val data =
                mapOf(
                    "personId" to personId,
                    "name" to therapy.name,
                    "createdBy" to userId,
                    "startDate" to therapy.startDate.toTimestamp(),
                    "duration" to therapy.duration.toMap(),
                    "isActive" to true,
                    "members" to mapOf(userId to "OWNER"),
                    "memberIds" to listOf(userId),
                    "medications" to therapy.medications.map { it.toMap() },
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                )
            var delayMs = 1000L
            for (attempt in 0..3) {
                val result = runCatching { therapiesCollection(personId).document(therapyId).set(data).await() }
                if (result.isSuccess) {
                    therapyDao.updateSyncStatus(therapyId, SyncStatus.SYNCED.name)
                    return
                }
                if (attempt < 3) {
                    Log.w("TherapyRepo", "Firestore sync attempt $attempt failed, retry in ${delayMs}ms")
                    delay(delayMs)
                    delayMs *= 2
                }
            }
            Log.e("TherapyRepo", "Firestore sync failed after 4 attempts: $therapyId")
            therapyDao.updateSyncStatus(therapyId, SyncStatus.ERROR.name)
        }

        override suspend fun updateTherapy(therapy: Therapy): Result<Unit> =
            runCatching {
                val data =
                    mutableMapOf<String, Any?>(
                        "name" to therapy.name,
                        "startDate" to therapy.startDate.toTimestamp(),
                        "duration" to therapy.duration.toMap(),
                        "isActive" to therapy.isActive,
                        "medications" to therapy.medications.map { it.toMap() },
                        "updatedAt" to FieldValue.serverTimestamp(),
                    )
                therapy.endDate?.let { data["endDate"] = it.toTimestamp() }
                therapiesCollection(therapy.personId).document(therapy.id).set(data, SetOptions.merge()).await()
            }

        override suspend fun deleteTherapy(
            personId: String,
            therapyId: String,
        ): Result<Unit> =
            runCatching {
                val therapyRef = therapiesCollection(personId).document(therapyId)
                val logsCollection = therapyRef.collection("medicationLogs")
                var query = logsCollection.limit(500)
                var snapshot = query.get().await()
                while (snapshot.documents.isNotEmpty()) {
                    val batch = firestore.batch()
                    snapshot.documents.forEach { batch.delete(it.reference) }
                    batch.commit().await()
                    snapshot = query.get().await()
                }
                therapyRef.delete().await()
            }

        // --- Entity / domain conversions ---

        private fun Therapy.toEntity(): TherapyEntity =
            TherapyEntity(
                id = id,
                personId = personId,
                name = name,
                createdBy = createdBy,
                startDateEpochDay = startDate.toEpochDay(),
                durationJson = duration.toJsonString(),
                isActive = isActive,
                endDateEpochDay = endDate?.toEpochDay(),
                membersJson = members.toJsonString(),
                medicationsJson = medications.toJsonString(),
                syncStatus = syncStatus.name,
            )

        private fun TherapyEntity.toDomain(): Therapy? {
            val sync = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.SYNCED)
            val duration =
                runCatching {
                    val obj = Json.parseToJsonElement(durationJson).jsonObject
                    when (obj["type"]?.jsonPrimitive?.content) {
                        "fixed" -> TherapyDuration.Fixed(obj["days"]?.jsonPrimitive?.intOrNull ?: 7)
                        else -> TherapyDuration.Indefinite
                    }
                }.getOrDefault(TherapyDuration.Indefinite)
            val members =
                runCatching {
                    Json.parseToJsonElement(membersJson).jsonObject
                        .mapNotNull { (k, v) ->
                            val role =
                                runCatching { MemberRole.valueOf(v.jsonPrimitive.content) }
                                    .getOrNull() ?: return@mapNotNull null
                            k to role
                        }.toMap()
                }.getOrDefault(emptyMap())
            val medications =
                runCatching {
                    Json.parseToJsonElement(medicationsJson).jsonArray.mapNotNull { elem ->
                        val obj = elem.jsonObject
                        val medId = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val medName = obj["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val dosage = obj["dosage"]?.jsonPrimitive?.doubleOrNull ?: return@mapNotNull null
                        val dosageUnit = obj["dosageUnit"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val freqHours = obj["frequencyHours"]?.jsonPrimitive?.intOrNull ?: return@mapNotNull null
                        val startDay = obj["startDate"]?.jsonPrimitive?.longOrNull ?: return@mapNotNull null
                        val times = obj["scheduledTimes"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                        Medication(
                            id = medId,
                            name = medName,
                            dosage = dosage,
                            dosageUnit = dosageUnit,
                            frequencyHours = freqHours,
                            scheduledTimes = times,
                            startDate = LocalDate.ofEpochDay(startDay),
                            notes = obj["notes"]?.jsonPrimitive?.content,
                        )
                    }
                }.getOrDefault(emptyList())
            return Therapy(
                id = id, personId = personId, name = name, createdBy = createdBy,
                startDate = LocalDate.ofEpochDay(startDateEpochDay),
                duration = duration, isActive = isActive,
                endDate = endDateEpochDay?.let { LocalDate.ofEpochDay(it) },
                members = members, medications = medications, syncStatus = sync,
            )
        }

        private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(): Therapy? {
            if (!exists()) return null
            val personId = getString("personId") ?: return null
            val name = getString("name") ?: return null
            val createdBy = getString("createdBy") ?: return null
            val startDate = getTimestamp("startDate")?.toLocalDate() ?: return null
            val duration = (get("duration") as? Map<*, *>)?.toDuration() ?: return null
            return Therapy(
                id = id, personId = personId, name = name, createdBy = createdBy,
                startDate = startDate, duration = duration,
                isActive = getBoolean("isActive") ?: true,
                endDate = getTimestamp("endDate")?.toLocalDate(),
                members = membersFrom(get("members")),
                medications = medicationsFrom(get("medications")),
                syncStatus = SyncStatus.SYNCED,
            )
        }

        private fun TherapyDuration.toJsonString(): String =
            buildJsonObject {
                when (this@toJsonString) {
                    is TherapyDuration.Indefinite -> put("type", "indefinite")
                    is TherapyDuration.Fixed -> {
                        put("type", "fixed")
                        put("days", days)
                    }
                }
            }.toString()

        private fun Map<String, MemberRole>.toJsonString(): String =
            buildJsonObject {
                forEach { (k, v) -> put(k, v.name) }
            }.toString()

        private fun List<Medication>.toJsonString(): String =
            buildJsonArray {
                forEach { med ->
                    add(
                        buildJsonObject {
                            put("id", med.id)
                            put("name", med.name)
                            put("dosage", med.dosage)
                            put("dosageUnit", med.dosageUnit)
                            put("frequencyHours", med.frequencyHours)
                            put("startDate", med.startDate.toEpochDay())
                            put("scheduledTimes", buildJsonArray { med.scheduledTimes.forEach { add(it) } })
                            med.notes?.let { put("notes", it) }
                        },
                    )
                }
            }.toString()

        private fun Map<*, *>.toDuration(): TherapyDuration? =
            when (this["type"] as? String) {
                "fixed" -> (this["days"] as? Long)?.toInt()?.let { TherapyDuration.Fixed(it) }
                else -> TherapyDuration.Indefinite
            }

        private fun membersFrom(raw: Any?): Map<String, MemberRole> =
            (raw as? Map<*, *>)
                ?.mapNotNull { (k, v) ->
                    val key = k as? String ?: return@mapNotNull null
                    val role =
                        (v as? String)?.let { runCatching { MemberRole.valueOf(it) }.getOrNull() }
                            ?: return@mapNotNull null
                    key to role
                }?.toMap() ?: emptyMap()

        private fun medicationsFrom(raw: Any?): List<Medication> =
            ((raw as? List<*>) ?: emptyList<Any?>())
                .mapNotNull { it as? Map<*, *> }
                .mapNotNull { medicationFrom(it) }

        private fun medicationFrom(map: Map<*, *>): Medication? {
            val medId = map["id"] as? String ?: return null
            val medName = map["name"] as? String ?: return null
            val dosage = (map["dosage"] as? Number)?.toDouble() ?: return null
            val dosageUnit = map["dosageUnit"] as? String ?: return null
            val frequencyHours = (map["frequencyHours"] as? Long)?.toInt() ?: return null
            val medStartDate = (map["startDate"] as? Timestamp)?.toLocalDate() ?: return null
            val scheduledTimes = (map["scheduledTimes"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            return Medication(
                id = medId,
                name = medName,
                dosage = dosage,
                dosageUnit = dosageUnit,
                frequencyHours = frequencyHours,
                scheduledTimes = scheduledTimes,
                startDate = medStartDate,
                notes = map["notes"] as? String,
            )
        }

        private fun TherapyDuration.toMap(): Map<String, Any> =
            when (this) {
                is TherapyDuration.Indefinite -> mapOf("type" to "indefinite")
                is TherapyDuration.Fixed -> mapOf("type" to "fixed", "days" to days)
            }

        private fun Medication.toMap(): Map<String, Any?> =
            mapOf(
                "id" to id,
                "name" to name,
                "dosage" to dosage,
                "dosageUnit" to dosageUnit,
                "frequencyHours" to frequencyHours,
                "scheduledTimes" to scheduledTimes,
                "startDate" to startDate.toTimestamp(),
                "notes" to notes,
            )

        private fun LocalDate.toTimestamp(): Timestamp =
            Timestamp(
                atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond,
                0,
            )

        private fun Timestamp.toLocalDate(): LocalDate = toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
    }
