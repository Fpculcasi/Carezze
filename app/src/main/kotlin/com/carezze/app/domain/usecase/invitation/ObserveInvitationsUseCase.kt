package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveInvitationsUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
) {
    operator fun invoke(userId: String): Flow<List<Invitation>> =
        invitationRepository.observeInvitations(userId)
}
