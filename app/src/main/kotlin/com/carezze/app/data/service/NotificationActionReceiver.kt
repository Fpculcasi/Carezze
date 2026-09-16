package com.fpculcasi.carezze.data.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.usecase.therapy.LogMedicationUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

const val ACTION_MEDICATION_TAKEN = "com.fpculcasi.carezze.ACTION_MEDICATION_TAKEN"
const val EXTRA_NOTIF_ID = "notif_id"
const val EXTRA_MEDICATION_ID = "medication_id"
const val EXTRA_SCHEDULED_TIME = "scheduled_time_epoch"

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var logMedicationUseCase: LogMedicationUseCase
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_MEDICATION_TAKEN) return

        val personId = intent.getStringExtra(EXTRA_PERSON_ID) ?: return
        val therapyId = intent.getStringExtra(EXTRA_THERAPY_ID) ?: return
        val medicationId = intent.getStringExtra(EXTRA_MEDICATION_ID) ?: return
        val scheduledTimeEpoch = intent.getLongExtra(EXTRA_SCHEDULED_TIME, -1L)
        val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, -1)
        val uid = firebaseAuth.currentUser?.uid ?: return

        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                logMedicationUseCase(
                    personId = personId,
                    therapyId = therapyId,
                    medicationId = medicationId,
                    scheduledTime = if (scheduledTimeEpoch > 0) Instant.ofEpochSecond(scheduledTimeEpoch) else Instant.now(),
                    status = MedicationStatus.TAKEN,
                    userId = uid,
                )
                if (notifId >= 0) {
                    context.getSystemService(NotificationManager::class.java).cancel(notifId)
                }
            } catch (e: Exception) {
                Log.e("NotificationAction", "Failed to log medication", e)
            } finally {
                pending.finish()
            }
        }
    }
}
