package com.fpculcasi.carezze.domain.usecase.invitation

import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.InvitationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RevokeAccessUseCaseTest {

    private val invitationRepository = mockk<InvitationRepository>()
    private val useCase = RevokeAccessUseCase(invitationRepository)

    @Test
    fun `returns success when access revoked`() = runTest {
        coEvery {
            invitationRepository.revokeAccess("pid-1", InvitationType.PERSON, "uid-2")
        } returns Result.success(Unit)

        val result = useCase("pid-1", InvitationType.PERSON, "uid-2")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `returns failure when revocation fails`() = runTest {
        coEvery {
            invitationRepository.revokeAccess("pid-1", InvitationType.PERSON, "uid-2")
        } returns Result.failure(Exception("permission denied"))

        val result = useCase("pid-1", InvitationType.PERSON, "uid-2")

        assertTrue(result.isFailure)
    }
}
