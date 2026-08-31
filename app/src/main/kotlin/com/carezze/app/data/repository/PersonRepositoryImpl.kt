package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : PersonRepository {

    private fun personsCollection() = firestore.collection("persons")

    override fun observePersons(userId: String): Flow<List<Person>> = callbackFlow {
        val listener = personsCollection()
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val persons = snapshot?.documents?.mapNotNull { it.toDomain() } ?: emptyList()
                trySend(persons)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getPerson(personId: String): Result<Person> = runCatching {
        val snapshot = personsCollection().document(personId).get().await()
        snapshot.toDomain() ?: error("Person not found: $personId")
    }

    override suspend fun createPerson(name: String, nickname: String?, userId: String): Result<Person> = runCatching {
        val personId = UUID.randomUUID().toString()
        val data = mapOf(
            "name" to name,
            "nickname" to nickname,
            "createdBy" to userId,
            "members" to mapOf(userId to "OWNER"),
            "memberIds" to listOf(userId),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        personsCollection().document(personId).set(data).await()
        Person(
            id = personId,
            name = name,
            nickname = nickname,
            createdBy = userId,
            members = mapOf(userId to MemberRole.OWNER),
        )
    }

    override suspend fun updatePerson(person: Person): Result<Unit> = runCatching {
        val data = mapOf(
            "name" to person.name,
            "nickname" to person.nickname,
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        personsCollection().document(person.id).set(data, SetOptions.merge()).await()
    }

    override suspend fun deletePerson(personId: String): Result<Unit> = runCatching {
        personsCollection().document(personId).delete().await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toDomain(): Person? {
        if (!exists()) return null
        val rawMembers = (get("members") as? Map<*, *>)
            ?.mapNotNull { (k, v) ->
                val key = k as? String ?: return@mapNotNull null
                val role = (v as? String)?.let { runCatching { MemberRole.valueOf(it) }.getOrNull() }
                    ?: return@mapNotNull null
                key to role
            }?.toMap() ?: emptyMap()

        return Person(
            id = id,
            name = getString("name") ?: return null,
            nickname = getString("nickname"),
            createdBy = getString("createdBy") ?: return null,
            members = rawMembers,
        )
    }
}
