package com.fpculcasi.carezze.domain.usecase.auth

import app.cash.turbine.test
import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ObserveAuthStateUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private val useCase = ObserveAuthStateUseCase(authRepository)

    @Test
    fun `emits null when no user is signed in`() = runTest {
        every { authRepository.observeAuthState() } returns flowOf(null)

        useCase().test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `emits user when signed in`() = runTest {
        val user = fakeUser()
        every { authRepository.observeAuthState() } returns flowOf(user)

        useCase().test {
            assertEquals(user, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `emits sequence of auth state changes`() = runTest {
        val user = fakeUser()
        every { authRepository.observeAuthState() } returns flowOf(null, user, null)

        useCase().test {
            assertNull(awaitItem())
            assertEquals(user, awaitItem())
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    private fun fakeUser() = User(
        id = "uid-anon",
        email = null,
        displayName = "Utente",
        language = Language.IT,
        temperatureUnit = TemperatureUnit.C,
        quietHoursStart = "22:00",
        quietHoursEnd = "07:00",
        personAccess = emptyList(),
        therapyAccess = emptyList(),
        isAnonymous = true,
    )
}
