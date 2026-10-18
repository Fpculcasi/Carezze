package com.fpculcasi.carezze.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fpculcasi.carezze.data.local.db.dao.MedicationLogDao
import com.fpculcasi.carezze.data.local.db.dao.TherapyDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

const val TAG_MEDICATION_REMINDER = "medication_reminder"

@HiltWorker
class MedicationReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val therapyDao: TherapyDao,
    private val medicationLogDao: MedicationLogDao,
    private val notificationHelper: NotificationHelper,
) : CoroutineWorker(appContext, params) {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override suspend fun doWork(): Result {
        val zone = ZoneId.systemDefault()
        val now = LocalTime.now(zone)
        val today = LocalDate.now(zone)
        val windowEnd = now.plusMinutes(30)

        val activeTherapies = therapyDao.getActiveTherapies()

        for (therapy in activeTherapies) {
            val medications = parseMedications(therapy.medicationsJson)
            for ((medId, medName, times) in medications) {
                for (timeStr in times) {
                    val scheduledTime = runCatching { LocalTime.parse(timeStr, timeFormatter) }.getOrNull() ?: continue
                    if (scheduledTime.isBefore(now) || scheduledTime.isAfter(windowEnd)) continue

                    val slotInstant = today.atTime(scheduledTime).atZone(zone).toInstant()
                    val windowFrom = slotInstant.epochSecond - 60
                    val windowTo = slotInstant.epochSecond + 60

                    val confirmed = medicationLogDao.getConfirmedLogsInWindow(
                        therapyId = therapy.id,
                        medicationId = medId,
                        from = windowFrom,
                        to = windowTo,
                    )
                    if (confirmed.isEmpty()) {
                        val notifId = abs((therapy.id + medId + timeStr).hashCode())
                        notificationHelper.postMedicationReminder(
                            context = applicationContext,
                            notificationId = notifId,
                            personName = "Paziente",
                            medicationName = medName,
                            therapyName = therapy.name,
                            personId = therapy.personId,
                            therapyId = therapy.id,
                            medicationId = medId,
                            scheduledTimeEpoch = slotInstant.epochSecond,
                        )
                    }
                }
            }
        }
        return Result.success()
    }

    private data class MedInfo(val id: String, val name: String, val scheduledTimes: List<String>)

    private fun parseMedications(medicationsJson: String): List<MedInfo> =
        runCatching {
            Json.parseToJsonElement(medicationsJson).jsonArray.mapNotNull { elem ->
                val obj = elem.jsonObject
                val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val name = obj["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val times = obj["scheduledTimes"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                MedInfo(id, name, times)
            }
        }.getOrDefault(emptyList())

    companion object {
        fun computeDueDoses(
            scheduledTimes: List<String>,
            now: LocalTime,
            windowMinutes: Long = 30,
        ): List<String> {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val windowEnd = now.plusMinutes(windowMinutes)
            return scheduledTimes.filter { timeStr ->
                runCatching { LocalTime.parse(timeStr, formatter) }
                    .getOrNull()
                    ?.let { !it.isBefore(now) && !it.isAfter(windowEnd) } == true
            }
        }
    }
}
