package com.fpculcasi.carezze.domain.usecase.activity

import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.repository.ActivityLogRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class ObserveActivityLogsUseCase
    @Inject
    constructor(
        private val activityLogRepository: ActivityLogRepository,
    ) {
        operator fun invoke(
            personId: String,
            from: Instant,
            to: Instant,
        ): Flow<List<ActivityLog>> = activityLogRepository.observeActivityLogs(personId, from, to)
    }
