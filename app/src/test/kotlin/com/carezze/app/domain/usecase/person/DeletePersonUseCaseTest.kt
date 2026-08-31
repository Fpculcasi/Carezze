package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.repository.PersonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeletePersonUseCaseTest {

    private val personRepository = mockk<PersonRepository>()
    private val useCase = DeletePersonUseCase(personRepository)

    @Test
    fun `returns success when delete succeeds`() = runTest {
        coEvery { personRepository.deletePerson("pid-1") } returns Result.success(Unit)

        val result = useCase("pid-1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `returns failure when repository throws`() = runTest {
        coEvery { personRepository.deletePerson("pid-1") } returns Result.failure(Exception("permission denied"))

        val result = useCase("pid-1")

        assertTrue(result.isFailure)
    }
}