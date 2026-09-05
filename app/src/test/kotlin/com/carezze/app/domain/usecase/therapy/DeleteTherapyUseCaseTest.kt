package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.repository.TherapyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeleteTherapyUseCaseTest {
    private val repository = mockk<TherapyRepository>()
    private val useCase = DeleteTherapyUseCase(repository)

    @Test
    fun `returns success on delete`() =
        runTest {
            coEvery { repository.deleteTherapy("pid-1", "tid-1") } returns Result.success(Unit)

            val result = useCase("pid-1", "tid-1")

            assertTrue(result.isSuccess)
        }

    @Test
    fun `returns failure when repository throws`() =
        runTest {
            coEvery { repository.deleteTherapy(any(), any()) } returns Result.failure(Exception("error"))

            val result = useCase("pid-1", "tid-1")

            assertTrue(result.isFailure)
        }
}
