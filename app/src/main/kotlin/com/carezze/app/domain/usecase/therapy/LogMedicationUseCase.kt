package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.repository.MedicationLogRepository
import java.time.Instant
import javax.inject.Inject

class LogMedicationUseCase @Inject constructor(
    private val medicationLogRepository: MedicationLogRepository,
) {
    suspend operator fun invoke(
        personId: String,
        therapyId: String,
        medicationId: String,
        scheduledTime: Instant,
        status: MedicationStatus,
        userId: String,
    ): Result<MedicationLog> = medicationLogRepository.logMedication(
        personId = personId,
        therapyId = therapyId,
        medicationId = medicationId,
        scheduledTime = scheduledTime,
        status = status,
        userId = userId,
    )
}
