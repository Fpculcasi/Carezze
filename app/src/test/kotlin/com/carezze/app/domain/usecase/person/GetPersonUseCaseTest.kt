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

class GetPersonUseCaseTest {

    private val personRepository = mockk<PersonRepository>()
    private val useCase = GetPersonUseCase(personRepository)

    @Test
    fun `returns person when found`() = runTest {
        val person = fakePerson()
        coEvery { personRepository.getPerson("pid-1") } returns Result.success(person)

        val result = useCase("pid-1")

        assertTrue(result.isSuccess)
        assertEquals(person, result.getOrNull())
    }

    @Test
    fun `returns failure when person not found`() = runTest {
        coEvery { personRepository.getPerson("pid-1") } returns Result.failure(Exception("not found"))

        val result = useCase("pid-1")

        assertTrue(result.isFailure)
    }

    private fun fakePerson() = Person(
        id = "pid-1",
        name = "Vittoria",
        nickname = "Vicky",
        createdBy = "uid-1",
        members = mapOf("uid-1" to MemberRole.OWNER),
    )
}
