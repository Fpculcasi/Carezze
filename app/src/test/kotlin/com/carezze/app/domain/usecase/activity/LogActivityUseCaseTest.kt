package com.fpculcasi.carezze.domain.usecase.activity

import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.repository.ActivityLogRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class LogActivityUseCaseTest {

    private val repository = mockk<ActivityLogRepository>()
    private val useCase = LogActivityUseCase(repository)

    @Test
    fun `returns log on success`() = runTest {
        val log = fakeLog()
        coEvery { repository.logActivity("pid-1", log) } returns Result.success(log)

        val result = useCase("pid-1", log)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `returns failure when repository throws`() = runTest {
        val log = fakeLog()
        coEvery { repository.logActivity(any(), any()) } returns Result.failure(Exception("error"))

        val result = useCase("pid-1", log)

        assertTrue(result.isFailure)
    }

    private fun fakeLog() = ActivityLog.Diaper(
        id = "log-1",
        personId = "pid-1",
        timestamp = Instant.now(),
        loggedBy = "uid-1",
        diaperType = DiaperType.WET,
        notes = null,
    )
}
