package com.fpculcasi.carezze

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fpculcasi.carezze.data.worker.InactivityCheckWorker
import com.fpculcasi.carezze.data.worker.MedicationReminderWorker
import com.fpculcasi.carezze.data.worker.TAG_INACTIVITY_CHECK
import com.fpculcasi.carezze.data.worker.TAG_MEDICATION_REMINDER
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CarezzeApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleWorkers()
    }

    private fun scheduleWorkers() {
        val wm = WorkManager.getInstance(this)
        wm.enqueueUniquePeriodicWork(
            TAG_MEDICATION_REMINDER,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<MedicationReminderWorker>(15, TimeUnit.MINUTES)
                .addTag(TAG_MEDICATION_REMINDER)
                .build(),
        )
        wm.enqueueUniquePeriodicWork(
            TAG_INACTIVITY_CHECK,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<InactivityCheckWorker>(30, TimeUnit.MINUTES)
                .addTag(TAG_INACTIVITY_CHECK)
                .build(),
        )
    }
}
