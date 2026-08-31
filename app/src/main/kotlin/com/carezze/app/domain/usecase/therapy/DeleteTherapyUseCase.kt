package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.repository.TherapyRepository
import javax.inject.Inject

class DeleteTherapyUseCase @Inject constructor(
    private val therapyRepository: TherapyRepository,
) {
    suspend operator fun invoke(personId: String, therapyId: String): Result<Unit> =
        therapyRepository.deleteTherapy(personId, therapyId)
}
