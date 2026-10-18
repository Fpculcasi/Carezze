package com.fpculcasi.carezze.domain.usecase.user

import app.cash.turbine.test
import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ObserveUserUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val useCase = ObserveUserUseCase(userRepository)

    @Test
    fun `emits null when document does not exist`() =
        runTest {
            every { userRepository.observeUser("uid-1") } returns flowOf(null)

            useCase("uid-1").test {
                assertNull(awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `emits user when document exists`() =
        runTest {
            val user = fakeUser()
            every { userRepository.observeUser("uid-1") } returns flowOf(user)

            useCase("uid-1").test {
                assertEquals(user, awaitItem())
                awaitComplete()
            }
        }

    private fun fakeUser() =
        User(
            id = "uid-1",
            email = "test@email.it",
            displayName = "Test",
            language = Language.IT,
            temperatureUnit = TemperatureUnit.C,
            quietHoursStart = "22:00",
            quietHoursEnd = "07:00",
            personAccess = emptyList(),
            therapyAccess = emptyList(),
            isAnonymous = false,
        )
}
