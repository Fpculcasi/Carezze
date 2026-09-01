package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class ObserveInvitationsUseCaseTest {

    private val invitationRepository = mockk<InvitationRepository>()
    private val useCase = ObserveInvitationsUseCase(invitationRepository)

    @Test
    fun `emits invitation list for user`() = runTest {
        val invitations = listOf(fakeInvitation())
        every { invitationRepository.observeInvitations("uid-1") } returns flowOf(invitations)

        val result = useCase("uid-1").first()

        assertEquals(invitations, result)
    }

    @Test
    fun `emits empty list when no invitations`() = runTest {
        every { invitationRepository.observeInvitations("uid-1") } returns flowOf(emptyList())

        val result = useCase("uid-1").first()

        assertEquals(emptyList<Invitation>(), result)
    }

    private fun fakeInvitation() = Invitation(
        id = "inv-1",
        type = InvitationType.PERSON,
        targetId = "pid-1",
        personId = null,
        targetName = "Sofia",
        createdBy = "uid-1",
        createdByName = "Mario",
        code = "AB12CD34",
        expiresAt = Instant.now().plusSeconds(86400),
        used = false,
        usedBy = null,
        usedAt = null,
    )
}
