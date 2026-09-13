package com.fpculcasi.carezze.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fpculcasi.carezze.data.local.db.dao.ActivityLogDao
import com.fpculcasi.carezze.data.local.db.dao.MedicationLogDao
import com.fpculcasi.carezze.data.local.db.dao.TherapyDao
import com.fpculcasi.carezze.data.local.db.entity.ActivityLogEntity
import com.fpculcasi.carezze.data.local.db.entity.MedicationLogEntity
import com.fpculcasi.carezze.data.local.db.entity.TherapyEntity

@Database(
    entities = [ActivityLogEntity::class, TherapyEntity::class, MedicationLogEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CarezzeDatabase : RoomDatabase() {
    abstract fun activityLogDao(): ActivityLogDao

    abstract fun therapyDao(): TherapyDao

    abstract fun medicationLogDao(): MedicationLogDao
}
