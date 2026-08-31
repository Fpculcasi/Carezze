package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.repository.MedicationLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLogsUseCase @Inject constructor(
    private val medicationLogRepository: MedicationLogRepository,
) {
    operator fun invoke(personId: String, therapyId: String): Flow<List<MedicationLog>> =
        medicationLogRepository.observeLogs(personId, therapyId)
}
