package com.fpculcasi.carezze.domain.usecase.user

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SyncUserUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val useCase = SyncUserUseCase(userRepository)

    @Test
    fun `delegates to repository and returns success`() =
        runTest {
            val user = fakeUser()
            coEvery { userRepository.syncUser(user) } returns Result.success(Unit)

            val result = useCase(user)

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { userRepository.syncUser(user) }
        }

    @Test
    fun `returns failure when repository throws`() =
        runTest {
            val user = fakeUser()
            coEvery { userRepository.syncUser(user) } returns Result.failure(RuntimeException("Network error"))

            val result = useCase(user)

            assertTrue(result.isFailure)
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
