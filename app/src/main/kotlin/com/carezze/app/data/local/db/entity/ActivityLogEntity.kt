package com.fpculcasi.carezze.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val personId: String,
    val type: String,
    val timestampEpochSecond: Long,
    val loggedBy: String,
    val dataJson: String,
    val syncStatus: String = "PENDING",
)
