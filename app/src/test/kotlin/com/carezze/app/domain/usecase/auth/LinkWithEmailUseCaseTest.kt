package com.fpculcasi.carezze.domain.usecase.auth

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LinkWithEmailUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val useCase = LinkWithEmailUseCase(authRepository)

    @Test
    fun `returns success with non-anonymous user after linking`() = runTest {
        val linkedUser = fakeUser(isAnonymous = false)
        coEvery { authRepository.linkWithEmail("user@email.it", "pass123") } returns Result.success(linkedUser)

        val result = useCase("user@email.it", "pass123")

        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.isAnonymous)
        assertEquals(linkedUser, result.getOrNull())
    }

    @Test
    fun `returns failure when email already linked to another account`() = runTest {
        coEvery { authRepository.linkWithEmail(any(), any()) } returns
            Result.failure(RuntimeException("CREDENTIAL_ALREADY_IN_USE"))

        val result = useCase("taken@email.it", "pass123")

        assertTrue(result.isFailure)
    }

    private fun fakeUser(isAnonymous: Boolean) = User(
        id = "uid-linked",
        email = "user@email.it",
        displayName = "Utente",
        language = Language.IT,
        temperatureUnit = TemperatureUnit.C,
        quietHoursStart = "22:00",
        quietHoursEnd = "07:00",
        personAccess = emptyList(),
        therapyAccess = emptyList(),
        isAnonymous = isAnonymous,
    )
}
