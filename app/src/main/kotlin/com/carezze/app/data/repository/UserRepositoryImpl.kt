package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : UserRepository {

    private fun usersCollection() = firestore.collection("users")

    override suspend fun syncUser(user: User): Result<Unit> = runCatching {
        val data = mapOf(
            "email" to user.email,
            "displayName" to user.displayName,
            "language" to user.language.name.lowercase(),
            "temperatureUnit" to user.temperatureUnit.name,
            "quietHoursStart" to user.quietHoursStart,
            "quietHoursEnd" to user.quietHoursEnd,
            "personAccess" to user.personAccess,
            "therapyAccess" to user.therapyAccess,
        )
        usersCollection().document(user.id).set(data, SetOptions.merge()).await()
    }

    override suspend fun getUser(userId: String): Result<User> = runCatching {
        val snapshot = usersCollection().document(userId).get().await()
        snapshot.toDomain(userId) ?: error("User document not found for $userId")
    }

    override fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val listener = usersCollection().document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toDomain(userId))
        }
        awaitClose { listener.remove() }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(userId: String): User? {
        if (!exists()) return null
        return User(
            id = userId,
            email = getString("email"),
            displayName = getString("displayName") ?: "Utente",
            language = getString("language")?.uppercase()
                ?.let { runCatching { Language.valueOf(it) }.getOrNull() } ?: Language.IT,
            temperatureUnit = getString("temperatureUnit")
                ?.let { runCatching { TemperatureUnit.valueOf(it) }.getOrNull() } ?: TemperatureUnit.C,
            quietHoursStart = getString("quietHoursStart") ?: "22:00",
            quietHoursEnd = getString("quietHoursEnd") ?: "07:00",
            personAccess = (get("personAccess") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            therapyAccess = (get("therapyAccess") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            isAnonymous = getString("email") == null,
        )
    }
}
