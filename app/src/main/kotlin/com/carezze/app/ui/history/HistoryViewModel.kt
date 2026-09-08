package com.fpculcasi.carezze.ui.history

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.usecase.activity.ObserveActivityLogsUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ObserveLogsUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ObserveTherapiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

sealed class HistoryEvent {
    abstract val timestamp: Instant

    data class Activity(val log: ActivityLog) : HistoryEvent() {
        override val timestamp: Instant get() = log.timestamp
    }

    data class Medication(
        val log: MedicationLog,
        val therapyName: String,
        val medicationName: String,
        val dosage: String,
    ) : HistoryEvent() {
        override val timestamp: Instant get() = log.scheduledTime
    }
}

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val observeActivityLogs: ObserveActivityLogsUseCase,
        private val observeTherapies: ObserveTherapiesUseCase,
        private val observeLogs: ObserveLogsUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val personId: String = checkNotNull(savedStateHandle["personId"])

        private val from: Instant = Instant.now().minus(30, ChronoUnit.DAYS)
        private val to: Instant = Instant.now()

        private val activityEvents: Flow<List<HistoryEvent>> =
            observeActivityLogs(personId, from, to)
                .catch { e ->
                    Log.e("HistoryViewModel", "activityLogs($personId) error", e)
                    emit(emptyList())
                }
                .map { logs -> logs.map { HistoryEvent.Activity(it) } }

        private val medicationEvents: Flow<List<HistoryEvent>> =
            observeTherapies(personId)
                .catch { e ->
                    Log.e("HistoryViewModel", "therapies($personId) error", e)
                    emit(emptyList())
                }
                .flatMapLatest { therapies ->
                    if (therapies.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(
                            therapies.map { therapy ->
                                observeLogs(personId, therapy.id)
                                    .catch { emit(emptyList()) }
                                    .map { logs ->
                                        logs
                                            .filter { it.scheduledTime >= from && it.scheduledTime <= to }
                                            .mapNotNull { log ->
                                                val medication =
                                                    therapy.medications.find { m -> m.id == log.medicationId }
                                                        ?: return@mapNotNull null
                                                HistoryEvent.Medication(
                                                    log = log,
                                                    therapyName = therapy.name,
                                                    medicationName = medication.name,
                                                    dosage = "${medication.dosage} ${medication.dosageUnit}",
                                                )
                                            }
                                    }
                            },
                        ) { lists -> lists.flatMap { it } }
                    }
                }

        val events: StateFlow<List<HistoryEvent>> =
            combine(activityEvents, medicationEvents) { activities, medications ->
                (activities + medications).sortedByDescending { it.timestamp }
            }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }
