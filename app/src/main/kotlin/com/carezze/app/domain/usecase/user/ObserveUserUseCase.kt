package com.fpculcasi.carezze.domain.usecase.user

import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        operator fun invoke(userId: String): Flow<User?> = userRepository.observeUser(userId)
    }
