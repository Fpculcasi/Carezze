package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateTherapyUseCase @Inject constructor(
    private val therapyRepository: TherapyRepository,
) {
    suspend operator fun invoke(
        personId: String,
        name: String,
        startDate: LocalDate,
        duration: TherapyDuration,
        medications: List<Medication>,
        userId: String,
    ): Result<Therapy> = therapyRepository.createTherapy(
        personId = personId,
        name = name,
        startDate = startDate,
        duration = duration,
        medications = medications,
        userId = userId,
    )
}
