package com.fpculcasi.carezze.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fpculcasi.carezze.data.local.db.entity.TherapyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TherapyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TherapyEntity)

    @Query("SELECT * FROM therapies WHERE personId = :personId")
    fun observe(personId: String): Flow<List<TherapyEntity>>

    @Query("SELECT * FROM therapies WHERE isActive = 1")
    suspend fun getActiveTherapies(): List<TherapyEntity>

    @Query("UPDATE therapies SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(
        id: String,
        status: String,
    )
}
