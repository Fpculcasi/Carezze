package com.fpculcasi.carezze.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fpculcasi.carezze.data.local.db.entity.MedicationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MedicationLogEntity)

    @Query(
        "SELECT * FROM medication_logs WHERE personId = :personId AND therapyId = :therapyId " +
            "ORDER BY scheduledTimeEpochSecond DESC",
    )
    fun observe(
        personId: String,
        therapyId: String,
    ): Flow<List<MedicationLogEntity>>

    @Query("UPDATE medication_logs SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(
        id: String,
        status: String,
    )
}
