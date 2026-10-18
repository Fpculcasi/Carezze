package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import javax.inject.Inject

class RedeemInvitationUseCase
    @Inject
    constructor(
        private val invitationRepository: InvitationRepository,
    ) {
        suspend operator fun invoke(
            code: String,
            userId: String,
        ): Result<Invitation> = invitationRepository.redeemInvitation(code, userId)
    }
