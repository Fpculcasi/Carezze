package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ObserveTherapiesUseCaseTest {

    private val repository = mockk<TherapyRepository>()
    private val useCase = ObserveTherapiesUseCase(repository)

    @Test
    fun `emits therapy list from repository`() = runTest {
        val therapies = listOf(fakeTherapy())
        every { repository.observeTherapies("pid-1") } returns flowOf(therapies)

        val result = useCase("pid-1").first()

        assertEquals(therapies, result)
    }

    private fun fakeTherapy() = Therapy(
        id = "tid-1",
        personId = "pid-1",
        name = "Amoxicillina",
        createdBy = "uid-1",
        startDate = LocalDate.now(),
        duration = TherapyDuration.Indefinite,
        isActive = true,
        members = mapOf("uid-1" to MemberRole.OWNER),
        medications = emptyList(),
    )
}
