package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CreatePersonUseCaseTest {

    private val personRepository = mockk<PersonRepository>()
    private val useCase = CreatePersonUseCase(personRepository)

    @Test
    fun `returns created person on success`() = runTest {
        val person = fakePerson()
        coEvery { personRepository.createPerson("Sofia", null, "uid-1") } returns Result.success(person)

        val result = useCase("Sofia", null, "uid-1")

        assertTrue(result.isSuccess)
        assertEquals(person, result.getOrNull())
    }

    @Test
    fun `returns failure when repository throws`() = runTest {
        coEvery { personRepository.createPerson("Sofia", null, "uid-1") } returns Result.failure(Exception("network error"))

        val result = useCase("Sofia", null, "uid-1")

        assertTrue(result.isFailure)
    }

    private fun fakePerson() = Person(
        id = "pid-1",
        name = "Sofia",
        nickname = null,
        createdBy = "uid-1",
        members = mapOf("uid-1" to MemberRole.OWNER),
    )
}