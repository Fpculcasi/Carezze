package com.fpculcasi.carezze.domain.usecase.activity

import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.repository.ActivityLogRepository
import javax.inject.Inject

class LogActivityUseCase
    @Inject
    constructor(
        private val activityLogRepository: ActivityLogRepository,
    ) {
        suspend operator fun invoke(
            personId: String,
            log: ActivityLog,
        ): Result<ActivityLog> = activityLogRepository.logActivity(personId, log)
    }
