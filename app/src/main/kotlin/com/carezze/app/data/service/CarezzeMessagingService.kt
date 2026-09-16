package com.fpculcasi.carezze.data.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.fpculcasi.carezze.MainActivity
import com.fpculcasi.carezze.data.worker.NotificationHelper
import com.fpculcasi.carezze.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

const val EXTRA_ROUTE_TYPE = "route_type"
const val EXTRA_PERSON_ID = "person_id"
const val EXTRA_THERAPY_ID = "therapy_id"

@AndroidEntryPoint
class CarezzeMessagingService : FirebaseMessagingService() {

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        if (data.isEmpty()) return

        val type = data["type"] ?: return
        val personId = data["personId"] ?: ""
        val therapyId = data["therapyId"] ?: ""
        val personName = data["personName"] ?: "Paziente"
        val medicationName = data["medicationName"] ?: ""
        val therapyName = data["therapyName"] ?: ""

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ROUTE_TYPE, type)
            putExtra(EXTRA_PERSON_ID, personId)
            putExtra(EXTRA_THERAPY_ID, therapyId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            abs((personId + therapyId + type).hashCode()),
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notifId = abs((personId + therapyId + type).hashCode())
        when (type) {
            "medication_reminder" -> notificationHelper.postMedicationReminder(
                context = this,
                notificationId = notifId,
                personName = personName,
                medicationName = medicationName,
                therapyName = therapyName,
                contentIntent = pendingIntent,
            )
            "inactivity_alert" -> notificationHelper.postInactivityAlert(
                context = this,
                notificationId = notifId,
                personName = personName,
                activityType = data["activityType"] ?: "",
                contentIntent = pendingIntent,
            )
            else -> Log.w("FCM", "Unknown notification type: $type")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = firebaseAuth.currentUser?.uid ?: return
        serviceScope.launch {
            userRepository.updateFcmToken(uid, token)
                .onFailure { Log.e("FCM", "Token update failed", it) }
                .onSuccess { Log.d("FCM", "FCM token updated for $uid") }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
