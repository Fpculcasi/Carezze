package com.fpculcasi.carezze.domain.usecase.therapy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ScheduleCalculatorTest {
    @Test
    fun `24h frequency returns single dose at start hour`() {
        assertEquals(listOf("08:00"), ScheduleCalculator.computeScheduledTimes(24, 8))
    }

    @Test
    fun `8h frequency returns 3 doses`() {
        assertEquals(listOf("08:00", "16:00", "00:00"), ScheduleCalculator.computeScheduledTimes(8, 8))
    }

    @Test
    fun `6h frequency starting at midnight returns 4 doses`() {
        assertEquals(listOf("00:00", "06:00", "12:00", "18:00"), ScheduleCalculator.computeScheduledTimes(6, 0))
    }

    @Test
    fun `12h frequency returns 2 doses`() {
        assertEquals(listOf("08:00", "20:00"), ScheduleCalculator.computeScheduledTimes(12, 8))
    }

    @Test
    fun `frequency greater than 24 treated as 24`() {
        assertEquals(listOf("08:00"), ScheduleCalculator.computeScheduledTimes(48, 8))
    }

    @Test
    fun `zero frequency throws IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            ScheduleCalculator.computeScheduledTimes(0, 8)
        }
    }
}
