package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdatePersonUseCaseTest {

    private val personRepository = mockk<PersonRepository>()
    private val useCase = UpdatePersonUseCase(personRepository)

    @Test
    fun `returns success when update succeeds`() = runTest {
        val person = fakePerson()
        coEvery { personRepository.updatePerson(person) } returns Result.success(Unit)

        val result = useCase(person)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `returns failure when repository throws`() = runTest {
        val person = fakePerson()
        coEvery { personRepository.updatePerson(person) } returns Result.failure(Exception("network error"))

        val result = useCase(person)

        assertTrue(result.isFailure)
    }

    private fun fakePerson() = Person(
        id = "pid-1",
        name = "Sofia aggiornata",
        nickname = "Sofi",
        createdBy = "uid-1",
        members = mapOf("uid-1" to MemberRole.OWNER),
    )
}