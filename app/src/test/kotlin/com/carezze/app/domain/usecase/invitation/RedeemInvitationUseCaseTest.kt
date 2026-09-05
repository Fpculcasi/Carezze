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

class RedeemInvitationUseCaseTest {
    private val invitationRepository = mockk<InvitationRepository>()
    private val useCase = RedeemInvitationUseCase(invitationRepository)

    @Test
    fun `returns redeemed invitation on success`() =
        runTest {
            val invitation = fakeInvitation()
            coEvery { invitationRepository.redeemInvitation("AB12CD34", "uid-2") } returns Result.success(invitation)

            val result = useCase("AB12CD34", "uid-2")

            assertTrue(result.isSuccess)
            assertEquals(invitation, result.getOrNull())
        }

    @Test
    fun `returns failure for invalid or expired code`() =
        runTest {
            coEvery {
                invitationRepository.redeemInvitation("INVALID1", "uid-2")
            } returns Result.failure(Exception("invalid code"))

            val result = useCase("INVALID1", "uid-2")

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
            used = true,
            usedBy = "uid-2",
            usedAt = Instant.now(),
        )
}
