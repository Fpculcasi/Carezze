package com.fpculcasi.carezze.domain.model

import java.time.Instant
import java.time.LocalDate

data class Therapy(
    val id: String,
    val personId: String,
    val name: String,
    val createdBy: String,
    val startDate: LocalDate,
    val duration: TherapyDuration,
    val isActive: Boolean,
    val members: Map<String, MemberRole>,
    val medications: List<Medication>,
)

sealed class TherapyDuration {
    object Indefinite : TherapyDuration()
    data class Fixed(val days: Int) : TherapyDuration()
}

data class Medication(
    val id: String,
    val name: String,
    val dosage: Double,
    val dosageUnit: String,
    val frequencyHours: Int,
    val scheduledTimes: List<String>,
    val startDate: LocalDate,
    val notes: String?,
)

data class MedicationLog(
    val id: String,
    val therapyId: String,
    val medicationId: String,
    val scheduledTime: Instant,
    val actualTime: Instant?,
    val status: MedicationStatus,
    val loggedBy: String?,
)

enum class MedicationStatus { TAKEN, SKIPPED, PENDING }
