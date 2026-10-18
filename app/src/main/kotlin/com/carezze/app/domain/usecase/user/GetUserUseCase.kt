package com.fpculcasi.carezze.domain.usecase.user

import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(userId: String): Result<User> = userRepository.getUser(userId)
    }
