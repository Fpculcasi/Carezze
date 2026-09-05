package com.fpculcasi.carezze.domain.usecase.activity

import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.repository.ActivityLogRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class ObserveActivityLogsUseCaseTest {
    private val repository = mockk<ActivityLogRepository>()
    private val useCase = ObserveActivityLogsUseCase(repository)

    private val from = Instant.now().minus(7, ChronoUnit.DAYS)
    private val to = Instant.now()

    @Test
    fun `emits list from repository`() =
        runTest {
            val logs = listOf(fakeLog())
            every { repository.observeActivityLogs("pid-1", from, to) } returns flowOf(logs)

            val result = useCase("pid-1", from, to).first()

            assertEquals(1, result.size)
        }

    @Test
    fun `emits empty list when no logs`() =
        runTest {
            every { repository.observeActivityLogs("pid-1", from, to) } returns flowOf(emptyList())

            val result = useCase("pid-1", from, to).first()

            assertTrue(result.isEmpty())
        }

    private fun fakeLog() =
        ActivityLog.Diaper(
            id = "log-1",
            personId = "pid-1",
            timestamp = Instant.now(),
            loggedBy = "uid-1",
            diaperType = DiaperType.WET,
            notes = null,
        )
}
