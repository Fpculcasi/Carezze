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

class SignInWithEmailUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val useCase = SignInWithEmailUseCase(authRepository)

    @Test
    fun `returns success with authenticated user on valid credentials`() =
        runTest {
            val user = fakeUser(email = "test@email.it")
            coEvery { authRepository.signInWithEmail("test@email.it", "pass123") } returns Result.success(user)

            val result = useCase("test@email.it", "pass123")

            assertTrue(result.isSuccess)
            assertEquals(user, result.getOrNull())
        }

    @Test
    fun `returns failure on wrong credentials`() =
        runTest {
            coEvery { authRepository.signInWithEmail(any(), any()) } returns
                Result.failure(RuntimeException("INVALID_PASSWORD"))

            val result = useCase("test@email.it", "wrong")

            assertTrue(result.isFailure)
        }

    private fun fakeUser(email: String) =
        User(
            id = "uid-email",
            email = email,
            displayName = "Test User",
            language = Language.IT,
            temperatureUnit = TemperatureUnit.C,
            quietHoursStart = "22:00",
            quietHoursEnd = "07:00",
            personAccess = emptyList(),
            therapyAccess = emptyList(),
            isAnonymous = false,
        )
}
