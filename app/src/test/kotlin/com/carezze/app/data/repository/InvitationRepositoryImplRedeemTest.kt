package com.fpculcasi.carezze.data.repository

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class InvitationRepositoryImplRedeemTest {
    @Test
    fun `valid invitation — not used and not expired — passes validation`() {
        val result =
            InvitationRepositoryImpl.validateInvitation(
                used = false,
                expiresAt = Instant.now().plusSeconds(3600),
            )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `already used invitation returns failure with message`() {
        val result =
            InvitationRepositoryImpl.validateInvitation(
                used = true,
                expiresAt = Instant.now().plusSeconds(3600),
            )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("already used"))
    }

    @Test
    fun `expired invitation returns failure with message`() {
        val result =
            InvitationRepositoryImpl.validateInvitation(
                used = false,
                expiresAt = Instant.now().minusSeconds(3600),
            )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("expired"))
    }
}
