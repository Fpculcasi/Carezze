package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.repository.TherapyRepository
import java.time.LocalDate
import javax.inject.Inject

class TerminateTherapyUseCase
    @Inject
    constructor(
        private val therapyRepository: TherapyRepository,
        private val getTherapyUseCase: GetTherapyUseCase,
    ) {
        suspend operator fun invoke(
            personId: String,
            therapyId: String,
        ): Result<Unit> =
            getTherapyUseCase(personId, therapyId).mapCatching { therapy ->
                therapyRepository
                    .updateTherapy(
                        therapy.copy(isActive = false, endDate = LocalDate.now()),
                    ).getOrThrow()
            }
    }
