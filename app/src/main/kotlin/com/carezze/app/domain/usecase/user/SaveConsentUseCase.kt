package com.fpculcasi.carezze.domain.usecase.user

import com.fpculcasi.carezze.domain.repository.UserRepository
import javax.inject.Inject

class SaveConsentUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(userId: String): Result<Unit> = userRepository.saveConsent(userId)
    }
