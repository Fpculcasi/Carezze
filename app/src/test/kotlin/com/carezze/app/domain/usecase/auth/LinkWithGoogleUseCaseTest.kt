package com.fpculcasi.carezze.domain.usecase.auth

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LinkWithGoogleUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val useCase = LinkWithGoogleUseCase(authRepository)

    @Test
    fun `returns non-anonymous user after linking anonymous session to google`() = runTest {
        val linkedUser = fakeUser(isAnonymous = false)
        coEvery { authRepository.linkWithGoogle("valid-id-token") } returns Result.success(linkedUser)

        val result = useCase("valid-id-token")

        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.isAnonymous)
    }

    @Test
    fun `returns failure when google account already linked to another user`() = runTest {
        coEvery { authRepository.linkWithGoogle(any()) } returns
            Result.failure(RuntimeException("CREDENTIAL_ALREADY_IN_USE"))

        val result = useCase("valid-id-token")

        assertTrue(result.isFailure)
    }

    private fun fakeUser(isAnonymous: Boolean) = User(
        id = "uid-google",
        email = "google@gmail.com",
        displayName = "Google User",
        language = Language.IT,
        temperatureUnit = TemperatureUnit.C,
        quietHoursStart = "22:00",
        quietHoursEnd = "07:00",
        personAccess = emptyList(),
        therapyAccess = emptyList(),
        isAnonymous = isAnonymous,
    )
}
