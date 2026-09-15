package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvitationRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : InvitationRepository {
        private fun invitationsCollection() = firestore.collection("invitations")

        override fun observeInvitations(userId: String): Flow<List<Invitation>> =
            callbackFlow {
                val registration =
                    invitationsCollection()
                        .whereEqualTo("createdBy", userId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                close(error)
                                return@addSnapshotListener
                            }
                            val invitations =
                                snapshot?.documents?.mapNotNull { it.toInvitation() } ?: emptyList()
                            trySend(invitations)
                        }
                awaitClose { registration.remove() }
            }

        override suspend fun generateInvitation(
            type: InvitationType,
            targetId: String,
            personId: String?,
            userId: String,
            userName: String,
            targetName: String,
        ): Result<Invitation> =
            runCatching {
                val code = generateCode()
                val now = Instant.now()
                val expiresAt = now.plusSeconds(24 * 3600)

                val data =
                    mutableMapOf(
                        "type" to type.name,
                        "targetId" to targetId,
                        "targetName" to targetName,
                        "createdBy" to userId,
                        "createdByName" to userName,
                        "code" to code,
                        "expiresAt" to com.google.firebase.Timestamp(expiresAt.epochSecond, expiresAt.nano),
                        "used" to false,
                        "usedBy" to null,
                        "usedAt" to null,
                        "createdAt" to FieldValue.serverTimestamp(),
                    )
                if (personId != null) data["personId"] = personId

                val docRef = invitationsCollection().add(data).await()
                Invitation(
                    id = docRef.id,
                    type = type,
                    targetId = targetId,
                    personId = personId,
                    targetName = targetName,
                    createdBy = userId,
                    createdByName = userName,
                    code = code,
                    expiresAt = expiresAt,
                    used = false,
                    usedBy = null,
                    usedAt = null,
                )
            }

        override suspend fun redeemInvitation(
            code: String,
            userId: String,
        ): Result<Invitation> =
            runCatching {
                val query =
                    invitationsCollection()
                        .whereEqualTo("code", code)
                        .limit(1)
                        .get()
                        .await()
                val invRef =
                    query.documents.firstOrNull()?.reference
                        ?: throw IllegalArgumentException("No invitation found for code: $code")

                firestore
                    .runTransaction { transaction ->
                        val snapshot = transaction.get(invRef)

                        val used = snapshot.getBoolean("used") ?: false
                        val expiresAt =
                            snapshot.getTimestamp("expiresAt")?.let {
                                Instant.ofEpochSecond(it.seconds, it.nanoseconds.toLong())
                            } ?: error("Missing expiresAt on invitation")

                        validateInvitation(used, expiresAt).getOrThrow()

                        val type =
                            InvitationType.valueOf(
                                snapshot.getString("type") ?: error("Missing type"),
                            )
                        val targetId =
                            snapshot.getString("targetId") ?: error("Missing targetId")
                        val personId = snapshot.getString("personId")

                        transaction.update(
                            invRef,
                            mapOf(
                                "used" to true,
                                "usedBy" to userId,
                                "usedAt" to FieldValue.serverTimestamp(),
                            ),
                        )

                        addMemberToTarget(transaction, type, targetId, personId, userId)

                        snapshot.toInvitation() ?: error("Failed to parse invitation")
                    }.await()
            }

        override suspend fun revokeAccess(
            targetId: String,
            type: InvitationType,
            memberUserId: String,
            personId: String?,
        ): Result<Unit> =
            validateRevokeArgs(type, personId).fold(
                onSuccess = {
                    runCatching {
                        revokeInternal(targetId, type, memberUserId, personId)
                    }
                },
                onFailure = { Result.failure(it) },
            )

        private fun addMemberToTarget(
            transaction: Transaction,
            type: InvitationType,
            targetId: String,
            personId: String?,
            userId: String,
        ) {
            when (type) {
                InvitationType.PERSON -> {
                    val personRef = firestore.collection("persons").document(targetId)
                    transaction.update(
                        personRef,
                        mapOf(
                            "memberIds" to FieldValue.arrayUnion(userId),
                            "members.$userId" to "EDITOR",
                        ),
                    )
                }
                InvitationType.THERAPY -> {
                    requireNotNull(personId) { "personId required for THERAPY invitation" }
                    val therapyRef =
                        firestore.collection("persons").document(personId)
                            .collection("therapies").document(targetId)
                    transaction.update(
                        therapyRef,
                        mapOf(
                            "memberIds" to FieldValue.arrayUnion(userId),
                            "members.$userId" to "EDITOR",
                        ),
                    )
                }
            }
        }

        private suspend fun revokeInternal(
            targetId: String,
            type: InvitationType,
            memberUserId: String,
            personId: String?,
        ) {
            when (type) {
                InvitationType.PERSON -> {
                    val personRef = firestore.collection("persons").document(targetId)
                    firestore.runTransaction { tx ->
                        tx.update(
                            personRef,
                            mapOf(
                                "memberIds" to FieldValue.arrayRemove(memberUserId),
                                "members.$memberUserId" to FieldValue.delete(),
                            ),
                        )
                    }.await()
                    val logs =
                        firestore.collection("persons").document(targetId)
                            .collection("activityLogs")
                            .whereEqualTo("loggedBy", memberUserId)
                            .get()
                            .await()
                    if (logs.documents.isNotEmpty()) {
                        val batch = firestore.batch()
                        logs.documents.forEach { batch.delete(it.reference) }
                        batch.commit().await()
                    }
                }
                InvitationType.THERAPY -> {
                    val therapyRef =
                        firestore.collection("persons").document(personId!!)
                            .collection("therapies").document(targetId)
                    firestore.runTransaction { tx ->
                        tx.update(
                            therapyRef,
                            mapOf(
                                "memberIds" to FieldValue.arrayRemove(memberUserId),
                                "members.$memberUserId" to FieldValue.delete(),
                            ),
                        )
                    }.await()
                    val logs =
                        firestore.collection("persons").document(personId)
                            .collection("therapies").document(targetId)
                            .collection("medicationLogs")
                            .whereEqualTo("loggedBy", memberUserId)
                            .get()
                            .await()
                    if (logs.documents.isNotEmpty()) {
                        val batch = firestore.batch()
                        logs.documents.forEach { batch.delete(it.reference) }
                        batch.commit().await()
                    }
                }
            }
        }

        private fun DocumentSnapshot.toInvitation(): Invitation? {
            if (!exists()) return null
            val typeStr = getString("type") ?: return null
            val type = runCatching { InvitationType.valueOf(typeStr) }.getOrNull() ?: return null
            val targetId = getString("targetId") ?: return null
            val createdBy = getString("createdBy") ?: return null
            val code = getString("code") ?: return null
            val expiresAtTs = getTimestamp("expiresAt") ?: return null
            return Invitation(
                id = id,
                type = type,
                targetId = targetId,
                personId = getString("personId"),
                targetName = getString("targetName") ?: "",
                createdBy = createdBy,
                createdByName = getString("createdByName") ?: "",
                code = code,
                expiresAt = Instant.ofEpochSecond(expiresAtTs.seconds, expiresAtTs.nanoseconds.toLong()),
                used = getBoolean("used") ?: false,
                usedBy = getString("usedBy"),
                usedAt =
                    getTimestamp("usedAt")?.let {
                        Instant.ofEpochSecond(it.seconds, it.nanoseconds.toLong())
                    },
            )
        }

        companion object {
            private val CODE_CHARS = (('A'..'Z') + ('0'..'9')).toList()

            internal fun generateCode(): String =
                (1..8).map { CODE_CHARS.random() }.joinToString("")

            internal fun validateRevokeArgs(
                type: InvitationType,
                personId: String?,
            ): Result<Unit> {
                if (type == InvitationType.THERAPY && personId == null) {
                    return Result.failure(IllegalArgumentException("personId required for THERAPY revocation"))
                }
                return Result.success(Unit)
            }

            internal fun validateInvitation(
                used: Boolean,
                expiresAt: Instant,
            ): Result<Unit> {
                if (used) return Result.failure(IllegalStateException("Invitation already used"))
                if (Instant.now().isAfter(expiresAt)) return Result.failure(IllegalStateException("Invitation expired"))
                return Result.success(Unit)
            }
        }
    }
