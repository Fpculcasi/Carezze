package com.fpculcasi.carezze.data.worker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalTime

class MedicationReminderWorkerTest {

    @Test
    fun `dose exactly at now is included`() {
        val now = LocalTime.of(8, 0)
        val result = MedicationReminderWorker.computeDueDoses(listOf("08:00"), now)
        assertEquals(listOf("08:00"), result)
    }

    @Test
    fun `dose at window boundary 30min is included`() {
        val now = LocalTime.of(8, 0)
        val result = MedicationReminderWorker.computeDueDoses(listOf("08:30"), now)
        assertEquals(listOf("08:30"), result)
    }

    @Test
    fun `dose just past window is excluded`() {
        val now = LocalTime.of(8, 0)
        val result = MedicationReminderWorker.computeDueDoses(listOf("08:31"), now)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `dose before now is excluded`() {
        val now = LocalTime.of(8, 0)
        val result = MedicationReminderWorker.computeDueDoses(listOf("07:59"), now)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `multiple doses - only in-window ones returned`() {
        val now = LocalTime.of(12, 0)
        val times = listOf("11:59", "12:00", "12:15", "12:30", "12:31", "13:00")
        val result = MedicationReminderWorker.computeDueDoses(times, now)
        assertEquals(listOf("12:00", "12:15", "12:30"), result)
    }

    @Test
    fun `empty list returns empty`() {
        val result = MedicationReminderWorker.computeDueDoses(emptyList(), LocalTime.of(10, 0))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invalid time format is skipped`() {
        val now = LocalTime.of(10, 0)
        val result = MedicationReminderWorker.computeDueDoses(listOf("not-a-time", "10:00"), now)
        assertEquals(listOf("10:00"), result)
    }

    @Test
    fun `custom window of 60 minutes works`() {
        val now = LocalTime.of(9, 0)
        val times = listOf("09:00", "09:30", "10:00", "10:01")
        val result = MedicationReminderWorker.computeDueDoses(times, now, windowMinutes = 60)
        assertEquals(listOf("09:00", "09:30", "10:00"), result)
    }
}
