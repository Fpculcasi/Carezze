package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun syncUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User>
    fun observeUser(userId: String): Flow<User?>
}
