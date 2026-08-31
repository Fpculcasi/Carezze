package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import javax.inject.Inject

class UpdateTherapyUseCase @Inject constructor(
    private val therapyRepository: TherapyRepository,
) {
    suspend operator fun invoke(therapy: Therapy): Result<Unit> =
        therapyRepository.updateTherapy(therapy)
}
