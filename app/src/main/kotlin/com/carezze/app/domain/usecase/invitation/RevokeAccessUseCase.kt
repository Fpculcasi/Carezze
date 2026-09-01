package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import javax.inject.Inject

class RevokeAccessUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
) {
    suspend operator fun invoke(
        targetId: String,
        type: InvitationType,
        memberUserId: String,
    ): Result<Unit> =
        invitationRepository.revokeAccess(targetId, type, memberUserId)
}
