package com.fpculcasi.carezze.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.fpculcasi.carezze.R
import com.fpculcasi.carezze.data.service.ACTION_MEDICATION_TAKEN
import com.fpculcasi.carezze.data.service.EXTRA_MEDICATION_ID
import com.fpculcasi.carezze.data.service.EXTRA_NOTIF_ID
import com.fpculcasi.carezze.data.service.EXTRA_PERSON_ID
import com.fpculcasi.carezze.data.service.EXTRA_SCHEDULED_TIME
import com.fpculcasi.carezze.data.service.EXTRA_THERAPY_ID
import com.fpculcasi.carezze.data.service.NotificationActionReceiver
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

const val CHANNEL_MEDICATION = "medication_reminder"
const val CHANNEL_INACTIVITY = "inactivity_alert"

@Singleton
class NotificationHelper @Inject constructor() {

    fun ensureChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_MEDICATION) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_MEDICATION,
                    "Promemoria farmaci",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply { description = "Notifiche per dosi imminenti" },
            )
        }
        if (manager.getNotificationChannel(CHANNEL_INACTIVITY) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_INACTIVITY,
                    "Avvisi inattività",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = "Avvisi quando mancano registrazioni recenti" },
            )
        }
    }

    fun postMedicationReminder(
        context: Context,
        notificationId: Int,
        personName: String,
        medicationName: String,
        therapyName: String,
        contentIntent: PendingIntent? = null,
        personId: String? = null,
        therapyId: String? = null,
        medicationId: String? = null,
        scheduledTimeEpoch: Long = 0L,
    ) {
        ensureChannels(context)
        val manager = context.getSystemService(NotificationManager::class.java)
        val builder = NotificationCompat.Builder(context, CHANNEL_MEDICATION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Farmaco per $personName")
            .setContentText("$medicationName — $therapyName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        if (contentIntent != null) builder.setContentIntent(contentIntent)
        if (personId != null && therapyId != null && medicationId != null) {
            val actionIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = ACTION_MEDICATION_TAKEN
                putExtra(EXTRA_PERSON_ID, personId)
                putExtra(EXTRA_THERAPY_ID, therapyId)
                putExtra(EXTRA_MEDICATION_ID, medicationId)
                putExtra(EXTRA_SCHEDULED_TIME, scheduledTimeEpoch)
                putExtra(EXTRA_NOTIF_ID, notificationId)
            }
            val actionPending = PendingIntent.getBroadcast(
                context,
                abs((personId + therapyId + medicationId).hashCode()),
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            builder.addAction(0, "Preso", actionPending)
        }
        manager.notify(notificationId, builder.build())
    }

    fun postInactivityAlert(
        context: Context,
        notificationId: Int,
        personName: String,
        activityType: String,
        contentIntent: PendingIntent? = null,
    ) {
        ensureChannels(context)
        val manager = context.getSystemService(NotificationManager::class.java)
        val builder = NotificationCompat.Builder(context, CHANNEL_INACTIVITY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nessuna registrazione recente")
            .setContentText("$activityType mancante per $personName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        if (contentIntent != null) builder.setContentIntent(contentIntent)
        manager.notify(notificationId, builder.build())
    }
}
