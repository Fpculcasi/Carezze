package com.fpculcasi.carezze.domain.usecase.auth

import com.fpculcasi.carezze.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(): Result<Unit> = authRepository.deleteAccount()
    }
