package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
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
            TODO("Implemented in 6.4")

        override suspend fun generateInvitation(
            type: InvitationType,
            targetId: String,
            personId: String?,
            userId: String,
            userName: String,
            targetName: String,
        ): Result<Invitation> = TODO("Implemented in 6.4")

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
                            } ?: throw IllegalStateException("Missing expiresAt on invitation")

                        validateInvitation(used, expiresAt).getOrThrow()

                        val type =
                            InvitationType.valueOf(
                                snapshot.getString("type") ?: throw IllegalStateException("Missing type"),
                            )
                        val targetId =
                            snapshot.getString("targetId")
                                ?: throw IllegalStateException("Missing targetId")
                        val personId = snapshot.getString("personId")

                        transaction.update(
                            invRef,
                            mapOf(
                                "used" to true,
                                "usedBy" to userId,
                                "usedAt" to FieldValue.serverTimestamp(),
                            ),
                        )

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
                                    firestore
                                        .collection("persons")
                                        .document(personId)
                                        .collection("therapies")
                                        .document(targetId)
                                transaction.update(
                                    therapyRef,
                                    mapOf(
                                        "memberIds" to FieldValue.arrayUnion(userId),
                                        "members.$userId" to "EDITOR",
                                    ),
                                )
                            }
                        }

                        snapshot.toInvitation() ?: throw IllegalStateException("Failed to parse invitation")
                    }.await()
            }

        override suspend fun revokeAccess(
            targetId: String,
            type: InvitationType,
            memberUserId: String,
        ): Result<Unit> = TODO("Implemented in 6.3")

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
