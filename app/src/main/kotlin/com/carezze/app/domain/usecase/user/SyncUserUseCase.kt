package com.fpculcasi.carezze.domain.usecase.user

import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import javax.inject.Inject

class SyncUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(user: User): Result<Unit> = userRepository.syncUser(user)
    }
