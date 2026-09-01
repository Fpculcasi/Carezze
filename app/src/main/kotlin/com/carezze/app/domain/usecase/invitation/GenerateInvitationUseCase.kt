package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import javax.inject.Inject

class GenerateInvitationUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
) {
    suspend operator fun invoke(
        type: InvitationType,
        targetId: String,
        personId: String?,
        userId: String,
        userName: String,
        targetName: String,
    ): Result<Invitation> =
        invitationRepository.generateInvitation(type, targetId, personId, userId, userName, targetName)
}
