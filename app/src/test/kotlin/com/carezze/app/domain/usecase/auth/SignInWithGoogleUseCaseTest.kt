package com.fpculcasi.carezze.domain.usecase.auth

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SignInWithGoogleUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val useCase = SignInWithGoogleUseCase(authRepository)

    @Test
    fun `returns success with authenticated user on valid google token`() = runTest {
        val user = fakeUser()
        coEvery { authRepository.signInWithGoogle("valid-id-token") } returns Result.success(user)

        val result = useCase("valid-id-token")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `returns failure on invalid google token`() = runTest {
        coEvery { authRepository.signInWithGoogle(any()) } returns
            Result.failure(RuntimeException("INVALID_ID_TOKEN"))

        val result = useCase("bad-token")

        assertTrue(result.isFailure)
    }

    private fun fakeUser() = User(
        id = "uid-google",
        email = "google@gmail.com",
        displayName = "Google User",
        language = Language.IT,
        temperatureUnit = TemperatureUnit.C,
        quietHoursStart = "22:00",
        quietHoursEnd = "07:00",
        personAccess = emptyList(),
        therapyAccess = emptyList(),
        isAnonymous = false,
    )
}
