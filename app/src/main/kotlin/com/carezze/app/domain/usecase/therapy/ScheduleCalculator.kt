package com.fpculcasi.carezze.domain.usecase.therapy

object ScheduleCalculator {

    fun computeScheduledTimes(frequencyHours: Int, startHour: Int = 8): List<String> {
        require(frequencyHours > 0) { "frequencyHours must be > 0" }
        val effectiveFrequency = if (frequencyHours > 24) 24 else frequencyHours
        val dosesPerDay = 24 / effectiveFrequency
        return (0 until dosesPerDay).map { i ->
            val hour = (startHour + i * effectiveFrequency) % 24
            String.format("%02d:00", hour)
        }
    }
}
