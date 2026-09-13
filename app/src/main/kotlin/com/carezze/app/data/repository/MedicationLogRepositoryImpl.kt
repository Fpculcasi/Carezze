package com.fpculcasi.carezze.data.repository

import android.util.Log
import com.fpculcasi.carezze.data.local.db.dao.MedicationLogDao
import com.fpculcasi.carezze.data.local.db.entity.MedicationLogEntity
import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.model.SyncStatus
import com.fpculcasi.carezze.domain.repository.MedicationLogRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationLogRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val medicationLogDao: MedicationLogDao,
    ) : MedicationLogRepository,
        CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
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
            channelFlow {
                val producerScope = this
                val listener =
                    logsCollection(personId, therapyId)
                        .orderBy("scheduledTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e("MedicationLogRepo", "Firestore listener error", error)
                                return@addSnapshotListener
                            }
                            producerScope.launch {
                                snapshot?.documents?.forEach { doc ->
                                    doc.toDomain(therapyId)?.let {
                                        medicationLogDao.upsert(it.toEntity(personId))
                                    }
                                }
                            }
                        }
                launch {
                    medicationLogDao
                        .observe(personId, therapyId)
                        .map { entities -> entities.map { it.toDomain() } }
                        .collect { send(it) }
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
            isManual: Boolean,
        ): Result<MedicationLog> =
            runCatching {
                val logId = UUID.randomUUID().toString()
                val localLog =
                    MedicationLog(
                        id = logId,
                        therapyId = therapyId,
                        medicationId = medicationId,
                        scheduledTime = scheduledTime,
                        actualTime = Instant.now(),
                        status = status,
                        loggedBy = userId,
                        isManual = isManual,
                        syncStatus = SyncStatus.PENDING,
                    )
                medicationLogDao.upsert(localLog.toEntity(personId))
                launch { syncToFirestore(logId, personId, therapyId, localLog) }
                localLog
            }

        private suspend fun syncToFirestore(
            logId: String,
            personId: String,
            therapyId: String,
            log: MedicationLog,
        ) {
            val data =
                mapOf(
                    "medicationId" to log.medicationId,
                    "scheduledTime" to Timestamp(log.scheduledTime.epochSecond, log.scheduledTime.nano),
                    "actualTime" to Timestamp.now(),
                    "status" to log.status.name,
                    "loggedBy" to log.loggedBy,
                    "isManual" to log.isManual,
                    "createdAt" to FieldValue.serverTimestamp(),
                )
            var delayMs = 1000L
            for (attempt in 0..3) {
                val result = runCatching { logsCollection(personId, therapyId).document(logId).set(data).await() }
                if (result.isSuccess) {
                    medicationLogDao.updateSyncStatus(logId, SyncStatus.SYNCED.name)
                    return
                }
                if (attempt < 3) {
                    Log.w("MedicationLogRepo", "Firestore sync attempt $attempt failed, retry in ${delayMs}ms")
                    delay(delayMs)
                    delayMs *= 2
                }
            }
            Log.e("MedicationLogRepo", "Firestore sync failed after 4 attempts: $logId")
            medicationLogDao.updateSyncStatus(logId, SyncStatus.ERROR.name)
        }

        // --- Entity / domain conversions ---

        private fun MedicationLog.toEntity(personId: String): MedicationLogEntity =
            MedicationLogEntity(
                id = id,
                therapyId = therapyId,
                personId = personId,
                medicationId = medicationId,
                scheduledTimeEpochSecond = scheduledTime.epochSecond,
                actualTimeEpochSecond = actualTime?.epochSecond,
                status = status.name,
                loggedBy = loggedBy,
                isManual = isManual,
                syncStatus = syncStatus.name,
            )

        private fun MedicationLogEntity.toDomain(): MedicationLog =
            MedicationLog(
                id = id,
                therapyId = therapyId,
                medicationId = medicationId,
                scheduledTime = Instant.ofEpochSecond(scheduledTimeEpochSecond),
                actualTime = actualTimeEpochSecond?.let { Instant.ofEpochSecond(it) },
                status = runCatching { MedicationStatus.valueOf(status) }.getOrDefault(MedicationStatus.PENDING),
                loggedBy = loggedBy,
                isManual = isManual,
                syncStatus = runCatching { SyncStatus.valueOf(syncStatus) }.getOrDefault(SyncStatus.SYNCED),
            )

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
                isManual = getBoolean("isManual") ?: false,
                syncStatus = SyncStatus.SYNCED,
            )
        }
    }
