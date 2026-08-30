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

class SignInAnonymouslyUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val useCase = SignInAnonymouslyUseCase(authRepository)

    @Test
    fun `returns success with anonymous user on successful sign-in`() = runTest {
        val anonymousUser = fakeUser(isAnonymous = true)
        coEvery { authRepository.signInAnonymously() } returns Result.success(anonymousUser)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(anonymousUser, result.getOrNull())
    }

    @Test
    fun `returns failure when repository throws exception`() = runTest {
        coEvery { authRepository.signInAnonymously() } returns Result.failure(RuntimeException("Network error"))

        val result = useCase()

        assertTrue(result.isFailure)
    }

    private fun fakeUser(isAnonymous: Boolean) = User(
        id = "uid-anon",
        email = null,
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
