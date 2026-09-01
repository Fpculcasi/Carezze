package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.ActivityLog
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ActivityLogRepository {
    suspend fun logActivity(personId: String, log: ActivityLog): Result<ActivityLog>
    fun observeActivityLogs(personId: String, from: Instant, to: Instant): Flow<List<ActivityLog>>
}
