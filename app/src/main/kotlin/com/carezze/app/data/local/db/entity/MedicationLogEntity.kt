package com.fpculcasi.carezze.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_logs")
data class MedicationLogEntity(
    @PrimaryKey val id: String,
    val therapyId: String,
    val personId: String,
    val medicationId: String,
    val scheduledTimeEpochSecond: Long,
    val actualTimeEpochSecond: Long?,
    val status: String,
    val loggedBy: String?,
    val isManual: Boolean = false,
    val syncStatus: String = "PENDING",
)
