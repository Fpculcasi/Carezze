package com.fpculcasi.carezze.data.repository

import com.fpculcasi.carezze.domain.model.InvitationType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvitationRepositoryImplRevokeTest {
    @Test
    fun `PERSON type with null personId passes validation`() {
        val result = InvitationRepositoryImpl.validateRevokeArgs(InvitationType.PERSON, null)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `THERAPY type with personId passes validation`() {
        val result = InvitationRepositoryImpl.validateRevokeArgs(InvitationType.THERAPY, "person-1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `THERAPY type with null personId returns failure`() {
        val result = InvitationRepositoryImpl.validateRevokeArgs(InvitationType.THERAPY, null)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("personId required"))
    }
}
