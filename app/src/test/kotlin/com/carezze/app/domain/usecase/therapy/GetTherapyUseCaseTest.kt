package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GetTherapyUseCaseTest {
    private val repository = mockk<TherapyRepository>()
    private val useCase = GetTherapyUseCase(repository)

    @Test
    fun `returns therapy on success`() =
        runTest {
            val therapy = fakeTherapy()
            coEvery { repository.getTherapy("pid-1", "tid-1") } returns Result.success(therapy)

            val result = useCase("pid-1", "tid-1")

            assertTrue(result.isSuccess)
            assertEquals(therapy, result.getOrNull())
        }

    @Test
    fun `returns failure when not found`() =
        runTest {
            coEvery { repository.getTherapy(any(), any()) } returns Result.failure(Exception("not found"))

            val result = useCase("pid-1", "tid-1")

            assertTrue(result.isFailure)
        }

    private fun fakeTherapy() =
        Therapy(
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
