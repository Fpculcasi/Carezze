package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?

    fun observeAuthState(): Flow<User?>

    suspend fun signInAnonymously(): Result<User>

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): Result<User>

    suspend fun createUserWithEmail(
        email: String,
        password: String,
    ): Result<User>

    suspend fun linkWithEmail(
        email: String,
        password: String,
    ): Result<User>

    suspend fun signInWithGoogle(idToken: String): Result<User>

    suspend fun linkWithGoogle(idToken: String): Result<User>

    suspend fun signOut()
}
