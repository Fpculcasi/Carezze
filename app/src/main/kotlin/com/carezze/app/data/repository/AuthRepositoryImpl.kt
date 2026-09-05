package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
    ) : AuthRepository {
        override val currentUser: User?
            get() = firebaseAuth.currentUser?.toDomain()

        override fun observeAuthState(): Flow<User?> =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        trySend(auth.currentUser?.toDomain())
                    }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

        override suspend fun signInAnonymously(): Result<User> =
            runCatching {
                firebaseAuth.signInAnonymously().await().user?.toDomain()
                    ?: error("Anonymous sign-in returned null user")
            }

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ): Result<User> =
            runCatching {
                firebaseAuth.signInWithEmailAndPassword(email, password).await().user?.toDomain()
                    ?: error("Email sign-in returned null user")
            }

        override suspend fun createUserWithEmail(
            email: String,
            password: String,
        ): Result<User> =
            runCatching {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await().user?.toDomain()
                    ?: error("Create user returned null user")
            }

        override suspend fun linkWithEmail(
            email: String,
            password: String,
        ): Result<User> =
            runCatching {
                val credential = EmailAuthProvider.getCredential(email, password)
                firebaseAuth.currentUser?.linkWithCredential(credential)?.await()?.user?.toDomain()
                    ?: error("Link with email returned null user")
            }

        override suspend fun signInWithGoogle(idToken: String): Result<User> =
            runCatching {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).await().user?.toDomain()
                    ?: error("Google sign-in returned null user")
            }

        override suspend fun linkWithGoogle(idToken: String): Result<User> =
            runCatching {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.currentUser?.linkWithCredential(credential)?.await()?.user?.toDomain()
                    ?: error("Link with Google returned null user")
            }

        override suspend fun signOut() {
            firebaseAuth.signOut()
        }

        private fun FirebaseUser.toDomain() =
            User(
                id = uid,
                email = email,
                displayName = displayName ?: "Utente",
                language = Language.IT,
                temperatureUnit = TemperatureUnit.C,
                quietHoursStart = "22:00",
                quietHoursEnd = "07:00",
                personAccess = emptyList(),
                therapyAccess = emptyList(),
                isAnonymous = isAnonymous,
            )
    }
