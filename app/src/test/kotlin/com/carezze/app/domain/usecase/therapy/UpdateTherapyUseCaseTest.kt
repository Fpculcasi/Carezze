package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UpdateTherapyUseCaseTest {

    private val repository = mockk<TherapyRepository>()
    private val useCase = UpdateTherapyUseCase(repository)

    @Test
    fun `returns success on update`() = runTest {
        val therapy = fakeTherapy()
        coEvery { repository.updateTherapy(therapy) } returns Result.success(Unit)

        val result = useCase(therapy)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `returns failure when repository throws`() = runTest {
        val therapy = fakeTherapy()
        coEvery { repository.updateTherapy(any()) } returns Result.failure(Exception("error"))

        val result = useCase(therapy)

        assertTrue(result.isFailure)
    }

    private fun fakeTherapy() = Therapy(
        id = "tid-1",
        personId = "pid-1",
        name = "Amoxicillina",
        createdBy = "uid-1",
        startDate = LocalDate.now(),
        duration = TherapyDuration.Fixed(7),
        isActive = true,
        members = mapOf("uid-1" to MemberRole.OWNER),
        medications = emptyList(),
    )
}
