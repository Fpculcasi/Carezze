package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class GenerateInvitationUseCaseTest {
    private val invitationRepository = mockk<InvitationRepository>()
    private val useCase = GenerateInvitationUseCase(invitationRepository)

    @Test
    fun `returns generated invitation on success`() =
        runTest {
            val invitation = fakeInvitation()
            coEvery {
                invitationRepository
                    .generateInvitation(InvitationType.PERSON, "pid-1", null, "uid-1", "Francesco", "Vittoria")
            } returns Result.success(invitation)

            val result = useCase(InvitationType.PERSON, "pid-1", null, "uid-1", "Francesco", "Vittoria")

            assertTrue(result.isSuccess)
            assertEquals(invitation, result.getOrNull())
        }

    @Test
    fun `returns failure when repository throws`() =
        runTest {
            coEvery {
                invitationRepository
                    .generateInvitation(InvitationType.PERSON, "pid-1", null, "uid-1", "Francesco", "Vittoria")
            } returns Result.failure(Exception("network error"))

            val result = useCase(InvitationType.PERSON, "pid-1", null, "uid-1", "Francesco", "Vittoria")

            assertTrue(result.isFailure)
        }

    private fun fakeInvitation() =
        Invitation(
            id = "inv-1",
            type = InvitationType.PERSON,
            targetId = "pid-1",
            personId = null,
            targetName = "Vittoria",
            createdBy = "uid-1",
            createdByName = "Francesco",
            code = "AB12CD34",
            expiresAt = Instant.now().plusSeconds(86400),
            used = false,
            usedBy = null,
            usedAt = null,
        )
}
