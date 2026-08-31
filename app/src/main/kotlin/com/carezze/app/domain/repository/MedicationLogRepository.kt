package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import kotlinx.coroutines.flow.Flow

interface MedicationLogRepository {
    fun observeLogs(personId: String, therapyId: String): Flow<List<MedicationLog>>
    suspend fun logMedication(
        personId: String,
        therapyId: String,
        medicationId: String,
        scheduledTime: java.time.Instant,
        status: MedicationStatus,
        userId: String,
    ): Result<MedicationLog>
}
