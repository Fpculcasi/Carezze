package com.fpculcasi.carezze.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fpculcasi.carezze.data.local.db.dao.ActivityLogDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.math.abs

const val TAG_INACTIVITY_CHECK = "inactivity_check"

private val INACTIVITY_THRESHOLDS_HOURS = mapOf(
    "diaper" to 4L,
    "meal" to 4L,
    "sleep_start" to 24L,
    "temperature" to 48L,
    "weight" to 168L, // 7 days
)

@HiltWorker
class InactivityCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val activityLogDao: ActivityLogDao,
    private val notificationHelper: NotificationHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = Instant.now().epochSecond
        val sevenDaysAgo = now - TimeUnit.DAYS.toSeconds(7)

        val personIds = activityLogDao.getDistinctPersonIds(sevenDaysAgo)

        for (personId in personIds) {
            for ((type, thresholdHours) in INACTIVITY_THRESHOLDS_HOURS) {
                val lastLog = activityLogDao.getLastLogByType(personId, type) ?: continue
                val deltaHours = (now - lastLog.timestampEpochSecond) / 3600L
                if (deltaHours >= thresholdHours) {
                    val notifId = abs((personId + type + "inactivity").hashCode())
                    notificationHelper.postInactivityAlert(
                        context = applicationContext,
                        notificationId = notifId,
                        personName = personId.take(8),
                        activityType = type,
                    )
                }
            }
        }
        return Result.success()
    }
}
