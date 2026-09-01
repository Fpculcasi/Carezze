package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    fun observeInvitations(userId: String): Flow<List<Invitation>>
    suspend fun generateInvitation(
        type: InvitationType,
        targetId: String,
        personId: String?,
        userId: String,
        userName: String,
        targetName: String,
    ): Result<Invitation>
    suspend fun redeemInvitation(code: String, userId: String): Result<Invitation>
    suspend fun revokeAccess(targetId: String, type: InvitationType, memberUserId: String): Result<Unit>
}
