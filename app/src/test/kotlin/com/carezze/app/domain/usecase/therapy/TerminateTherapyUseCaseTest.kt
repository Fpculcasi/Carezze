package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.TherapyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TerminateTherapyUseCaseTest {
    private val repository = mockk<TherapyRepository>()
    private val getTherapy = GetTherapyUseCase(repository)
    private val useCase = TerminateTherapyUseCase(repository, getTherapy)

    @Test
    fun `returns success and marks therapy inactive with endDate`() =
        runTest {
            val slot = slot<Therapy>()
            coEvery { repository.getTherapy("pid-1", "tid-1") } returns Result.success(fakeTherapy())
            coEvery { repository.updateTherapy(capture(slot)) } returns Result.success(Unit)

            val result = useCase("pid-1", "tid-1")

            assertTrue(result.isSuccess)
            assertFalse(slot.captured.isActive)
            assertNotNull(slot.captured.endDate)
        }

    @Test
    fun `returns failure when getTherapy fails`() =
        runTest {
            coEvery { repository.getTherapy(any(), any()) } returns Result.failure(Exception("not found"))

            val result = useCase("pid-1", "tid-1")

            assertTrue(result.isFailure)
            coVerify(exactly = 0) { repository.updateTherapy(any()) }
        }

    @Test
    fun `returns failure when updateTherapy fails`() =
        runTest {
            coEvery { repository.getTherapy("pid-1", "tid-1") } returns Result.success(fakeTherapy())
            coEvery { repository.updateTherapy(any()) } returns Result.failure(Exception("write error"))

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
            duration = TherapyDuration.Fixed(7),
            isActive = true,
            members = mapOf("uid-1" to MemberRole.OWNER),
            medications = emptyList(),
        )
}
