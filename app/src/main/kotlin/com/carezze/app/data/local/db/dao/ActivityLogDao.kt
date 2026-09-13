package com.fpculcasi.carezze.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fpculcasi.carezze.data.local.db.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ActivityLogEntity)

    @Query(
        "SELECT * FROM activity_logs WHERE personId = :personId " +
            "AND timestampEpochSecond BETWEEN :fromEpoch AND :toEpoch " +
            "ORDER BY timestampEpochSecond DESC",
    )
    fun observe(
        personId: String,
        fromEpoch: Long,
        toEpoch: Long,
    ): Flow<List<ActivityLogEntity>>

    @Query("UPDATE activity_logs SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(
        id: String,
        status: String,
    )
}
