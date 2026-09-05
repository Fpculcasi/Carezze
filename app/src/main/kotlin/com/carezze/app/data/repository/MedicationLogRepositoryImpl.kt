package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.repository.MedicationLogRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationLogRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : MedicationLogRepository {
        private fun logsCollection(
            personId: String,
            therapyId: String,
        ) = firestore.collection("persons").document(personId)
            .collection("therapies").document(therapyId)
            .collection("medicationLogs")

        override fun observeLogs(
            personId: String,
            therapyId: String,
        ): Flow<List<MedicationLog>> =
            callbackFlow {
                val listener =
                    logsCollection(personId, therapyId)
                        .orderBy("scheduledTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val logs = snapshot?.documents?.mapNotNull { it.toDomain(therapyId) } ?: emptyList()
                            trySend(logs)
                        }
                awaitClose { listener.remove() }
            }

        override suspend fun logMedication(
            personId: String,
            therapyId: String,
            medicationId: String,
            scheduledTime: Instant,
            status: MedicationStatus,
            userId: String,
        ): Result<MedicationLog> =
            runCatching {
                val logId = UUID.randomUUID().toString()
                val data =
                    mapOf(
                        "medicationId" to medicationId,
                        "scheduledTime" to Timestamp(scheduledTime.epochSecond, scheduledTime.nano),
                        "actualTime" to Timestamp.now(),
                        "status" to status.name,
                        "loggedBy" to userId,
                        "createdAt" to FieldValue.serverTimestamp(),
                    )
                logsCollection(personId, therapyId).document(logId).set(data).await()
                MedicationLog(
                    id = logId,
                    therapyId = therapyId,
                    medicationId = medicationId,
                    scheduledTime = scheduledTime,
                    actualTime = Instant.now(),
                    status = status,
                    loggedBy = userId,
                )
            }

        private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(therapyId: String): MedicationLog? {
            if (!exists()) return null
            val medicationId = getString("medicationId") ?: return null
            val scheduledTime = getTimestamp("scheduledTime")?.toDate()?.toInstant() ?: return null
            val actualTime = getTimestamp("actualTime")?.toDate()?.toInstant()
            val status =
                getString("status")?.let { runCatching { MedicationStatus.valueOf(it) }.getOrNull() }
                    ?: return null
            return MedicationLog(
                id = id,
                therapyId = therapyId,
                medicationId = medicationId,
                scheduledTime = scheduledTime,
                actualTime = actualTime,
                status = status,
                loggedBy = getString("loggedBy"),
            )
        }
    }
