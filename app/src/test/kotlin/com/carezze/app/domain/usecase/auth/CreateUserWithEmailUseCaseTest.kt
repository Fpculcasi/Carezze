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

class CreateUserWithEmailUseCaseTest {
    private val authRepository = mockk<AuthRepository>()
    private val useCase = CreateUserWithEmailUseCase(authRepository)

    @Test
    fun `returns success with new user on valid registration`() =
        runTest {
            val user = fakeUser()
            coEvery { authRepository.createUserWithEmail("new@email.it", "pass123") } returns Result.success(user)

            val result = useCase("new@email.it", "pass123")

            assertTrue(result.isSuccess)
            assertEquals(user, result.getOrNull())
        }

    @Test
    fun `returns failure when email already exists`() =
        runTest {
            coEvery { authRepository.createUserWithEmail(any(), any()) } returns
                Result.failure(RuntimeException("EMAIL_ALREADY_IN_USE"))

            val result = useCase("existing@email.it", "pass123")

            assertTrue(result.isFailure)
        }

    private fun fakeUser() =
        User(
            id = "uid-new",
            email = "new@email.it",
            displayName = "Utente",
            language = Language.IT,
            temperatureUnit = TemperatureUnit.C,
            quietHoursStart = "22:00",
            quietHoursEnd = "07:00",
            personAccess = emptyList(),
            therapyAccess = emptyList(),
            isAnonymous = false,
        )
}
