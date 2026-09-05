package com.fpculcasi.carezze.domain.usecase.therapy

import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.repository.MedicationLogRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class LogMedicationUseCaseTest {
    private val repository = mockk<MedicationLogRepository>()
    private val useCase = LogMedicationUseCase(repository)

    @Test
    fun `returns log on success`() =
        runTest {
            val log = fakeLog()
            coEvery {
                repository.logMedication("pid-1", "tid-1", "med-1", any(), MedicationStatus.TAKEN, "uid-1")
            } returns Result.success(log)

            val result = useCase("pid-1", "tid-1", "med-1", Instant.now(), MedicationStatus.TAKEN, "uid-1")

            assertTrue(result.isSuccess)
        }

    @Test
    fun `returns failure when repository throws`() =
        runTest {
            coEvery {
                repository.logMedication(any(), any(), any(), any(), any(), any())
            } returns Result.failure(Exception("error"))

            val result = useCase("pid-1", "tid-1", "med-1", Instant.now(), MedicationStatus.TAKEN, "uid-1")

            assertTrue(result.isFailure)
        }

    private fun fakeLog() =
        MedicationLog(
            id = "log-1",
            therapyId = "tid-1",
            medicationId = "med-1",
            scheduledTime = Instant.now(),
            actualTime = Instant.now(),
            status = MedicationStatus.TAKEN,
            loggedBy = "uid-1",
        )
}
