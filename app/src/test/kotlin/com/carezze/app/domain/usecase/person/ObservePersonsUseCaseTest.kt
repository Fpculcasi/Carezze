package com.fpculcasi.carezze.domain.usecase.person

import app.cash.turbine.test
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ObservePersonsUseCaseTest {
    private val personRepository = mockk<PersonRepository>()
    private val useCase = ObservePersonsUseCase(personRepository)

    @Test
    fun `emits empty list when user has no persons`() =
        runTest {
            every { personRepository.observePersons("uid-1") } returns flowOf(emptyList())

            useCase("uid-1").test {
                assertTrue(awaitItem().isEmpty())
                awaitComplete()
            }
        }

    @Test
    fun `emits persons when user has access`() =
        runTest {
            val persons = listOf(fakePerson("pid-1"), fakePerson("pid-2"))
            every { personRepository.observePersons("uid-1") } returns flowOf(persons)

            useCase("uid-1").test {
                assertEquals(persons, awaitItem())
                awaitComplete()
            }
        }

    private fun fakePerson(id: String) =
        Person(
            id = id,
            name = "Vittoria",
            nickname = null,
            createdBy = "uid-1",
            members = mapOf("uid-1" to MemberRole.OWNER),
        )
}
