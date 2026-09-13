package com.fpculcasi.carezze.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "therapies")
data class TherapyEntity(
    @PrimaryKey val id: String,
    val personId: String,
    val name: String,
    val createdBy: String,
    val startDateEpochDay: Long,
    val durationJson: String,
    val isActive: Boolean,
    val endDateEpochDay: Long?,
    val membersJson: String,
    val medicationsJson: String,
    val syncStatus: String = "PENDING",
)
