package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTherapiesUseCase
    @Inject
    constructor(
        private val therapyRepository: TherapyRepository,
    ) {
        operator fun invoke(personId: String): Flow<List<Therapy>> = therapyRepository.observeTherapies(personId)
    }
