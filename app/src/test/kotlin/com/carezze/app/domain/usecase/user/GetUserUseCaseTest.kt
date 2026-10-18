package com.fpculcasi.carezze.domain.usecase.user

import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetUserUseCaseTest {
    private val userRepository = mockk<UserRepository>()
    private val useCase = GetUserUseCase(userRepository)

    @Test
    fun `returns user when document exists`() =
        runTest {
            val user = fakeUser()
            coEvery { userRepository.getUser("uid-1") } returns Result.success(user)

            val result = useCase("uid-1")

            assertTrue(result.isSuccess)
            assertEquals(user, result.getOrNull())
        }

    @Test
    fun `returns failure when document not found`() =
        runTest {
            coEvery { userRepository.getUser("uid-1") } returns Result.failure(RuntimeException("Not found"))

            val result = useCase("uid-1")

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
