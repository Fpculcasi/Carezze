package com.fpculcasi.carezze.data.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class InvitationRepositoryImplGenerateTest {
    @Test
    fun `generateCode returns 8-char string`() {
        val code = InvitationRepositoryImpl.generateCode()
        assertEquals(8, code.length)
    }

    @Test
    fun `generateCode uses only uppercase letters and digits`() {
        val code = InvitationRepositoryImpl.generateCode()
        assertTrue(code.all { it.isUpperCase() || it.isDigit() })
    }

    @RepeatedTest(20)
    fun `generateCode produces unique codes across repeated calls`() {
        val codes = (1..10).map { InvitationRepositoryImpl.generateCode() }.toSet()
        // With 36^8 ~2.8T combinations, 10 draws should almost never collide
        assertTrue(codes.size > 1, "Expected at least 2 unique codes in 10 draws")
    }

    @Test
    fun `validateRevokeArgs accepts PERSON type without personId`() {
        val result =
            InvitationRepositoryImpl.validateRevokeArgs(
                type = com.fpculcasi.carezze.domain.model.InvitationType.PERSON,
                personId = null,
            )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `validateRevokeArgs rejects THERAPY type without personId`() {
        val result =
            InvitationRepositoryImpl.validateRevokeArgs(
                type = com.fpculcasi.carezze.domain.model.InvitationType.THERAPY,
                personId = null,
            )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("personId required"))
    }

    @Test
    fun `validateRevokeArgs accepts THERAPY type with personId`() {
        val result =
            InvitationRepositoryImpl.validateRevokeArgs(
                type = com.fpculcasi.carezze.domain.model.InvitationType.THERAPY,
                personId = "pid1",
            )
        assertTrue(result.isSuccess)
    }
}
