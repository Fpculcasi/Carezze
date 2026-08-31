package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TherapyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : TherapyRepository {

    private fun therapiesCollection(personId: String) =
        firestore.collection("persons").document(personId).collection("therapies")

    override fun observeTherapies(personId: String): Flow<List<Therapy>> = callbackFlow {
        val listener = therapiesCollection(personId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val therapies = snapshot?.documents?.mapNotNull { it.toDomain() } ?: emptyList()
                trySend(therapies)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getTherapy(personId: String, therapyId: String): Result<Therapy> = runCatching {
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
    ): Result<Therapy> = runCatching {
        val therapyId = UUID.randomUUID().toString()
        val data = mapOf(
            "personId" to personId,
            "name" to name,
            "createdBy" to userId,
            "startDate" to startDate.toTimestamp(),
            "duration" to duration.toMap(),
            "isActive" to true,
            "members" to mapOf(userId to "OWNER"),
            "memberIds" to listOf(userId),
            "medications" to medications.map { it.toMap() },
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        therapiesCollection(personId).document(therapyId).set(data).await()
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
        )
    }

    override suspend fun updateTherapy(therapy: Therapy): Result<Unit> = runCatching {
        val data = mapOf(
            "name" to therapy.name,
            "duration" to therapy.duration.toMap(),
            "isActive" to therapy.isActive,
            "medications" to therapy.medications.map { it.toMap() },
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        therapiesCollection(therapy.personId).document(therapy.id).set(data, SetOptions.merge()).await()
    }

    override suspend fun deleteTherapy(personId: String, therapyId: String): Result<Unit> = runCatching {
        therapiesCollection(personId).document(therapyId).delete().await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(): Therapy? {
        if (!exists()) return null
        val personId = getString("personId") ?: return null
        val name = getString("name") ?: return null
        val createdBy = getString("createdBy") ?: return null
        val startDate = getTimestamp("startDate")?.toLocalDate() ?: return null
        val durationMap = get("duration") as? Map<*, *> ?: return null
        val duration: TherapyDuration = when (durationMap["type"] as? String) {
            "fixed" -> TherapyDuration.Fixed((durationMap["days"] as? Long)?.toInt() ?: return null)
            else -> TherapyDuration.Indefinite
        }
        val isActive = getBoolean("isActive") ?: true
        val rawMembers = (get("members") as? Map<*, *>)
            ?.mapNotNull { (k, v) ->
                val key = k as? String ?: return@mapNotNull null
                val role = (v as? String)?.let { runCatching { MemberRole.valueOf(it) }.getOrNull() }
                    ?: return@mapNotNull null
                key to role
            }?.toMap() ?: emptyMap()
        val medications = ((get("medications") as? List<*>) ?: emptyList<Any?>())
            .mapNotNull { it as? Map<*, *> }
            .mapNotNull { map ->
                val medId = map["id"] as? String ?: return@mapNotNull null
                val medName = map["name"] as? String ?: return@mapNotNull null
                val dosage = (map["dosage"] as? Number)?.toDouble() ?: return@mapNotNull null
                val dosageUnit = map["dosageUnit"] as? String ?: return@mapNotNull null
                val frequencyHours = (map["frequencyHours"] as? Long)?.toInt() ?: return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val scheduledTimes = (map["scheduledTimes"] as? List<String>) ?: emptyList()
                val medStartDate = (map["startDate"] as? Timestamp)?.toLocalDate() ?: return@mapNotNull null
                Medication(
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
        return Therapy(
            id = id,
            personId = personId,
            name = name,
            createdBy = createdBy,
            startDate = startDate,
            duration = duration,
            isActive = isActive,
            members = rawMembers,
            medications = medications,
        )
    }

    private fun TherapyDuration.toMap(): Map<String, Any> = when (this) {
        is TherapyDuration.Indefinite -> mapOf("type" to "indefinite")
        is TherapyDuration.Fixed -> mapOf("type" to "fixed", "days" to days)
    }

    private fun Medication.toMap(): Map<String, Any?> = mapOf(
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
        Timestamp(atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond, 0)

    private fun Timestamp.toLocalDate(): LocalDate =
        toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
}
