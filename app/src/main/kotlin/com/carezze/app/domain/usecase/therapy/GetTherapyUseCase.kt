package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import javax.inject.Inject

class GetTherapyUseCase @Inject constructor(
    private val therapyRepository: TherapyRepository,
) {
    suspend operator fun invoke(personId: String, therapyId: String): Result<Therapy> =
        therapyRepository.getTherapy(personId, therapyId)
}
