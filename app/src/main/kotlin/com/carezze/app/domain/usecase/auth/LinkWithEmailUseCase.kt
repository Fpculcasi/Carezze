package com.fpculcasi.carezze.domain.usecase.auth

import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import javax.inject.Inject

class LinkWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.linkWithEmail(email, password)
}
