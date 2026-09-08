package com.fpculcasi.carezze.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.DiaperType
import com.fpculcasi.carezze.domain.model.MealType
import com.fpculcasi.carezze.domain.model.MealUnit
import com.fpculcasi.carezze.domain.model.MeasurementMethod
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.WeightUnit
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.activity.LogActivityUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.AddManualMedicationLogUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ObserveTherapiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

enum class ActivityLogType { MEAL, DIAPER, SLEEP_START, SLEEP_END, TEMPERATURE, WEIGHT, HYGIENE, THERAPY }

data class QuickLogUiState(
    val selectedType: ActivityLogType? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val therapies: List<Therapy> = emptyList(),
    val selectedTherapyId: String? = null,
    val selectedMedicationId: String? = null,
)

@HiltViewModel
class QuickLogViewModel
    @Inject
    constructor(
        private val logActivity: LogActivityUseCase,
        private val authRepository: AuthRepository,
        private val observeTherapies: ObserveTherapiesUseCase,
        private val addManualMedicationLog: AddManualMedicationLogUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(QuickLogUiState())
        val state: StateFlow<QuickLogUiState> = _state.asStateFlow()

        private val userId: String get() = authRepository.currentUser?.id ?: ""
        private var therapiesJob: Job? = null

        fun selectType(
            type: ActivityLogType,
            personId: String? = null,
        ) {
            _state.update { it.copy(selectedType = type) }
            if (type == ActivityLogType.THERAPY && personId != null) loadTherapies(personId)
        }

        fun clearType() {
            therapiesJob?.cancel()
            therapiesJob = null
            _state.update {
                it.copy(
                    selectedType = null,
                    error = null,
                    isSaved = false,
                    therapies = emptyList(),
                    selectedTherapyId = null,
                    selectedMedicationId = null,
                )
            }
        }

        fun selectTherapy(therapyId: String) {
            _state.update { it.copy(selectedTherapyId = therapyId, selectedMedicationId = null) }
        }

        fun clearTherapy() {
            _state.update { it.copy(selectedTherapyId = null, selectedMedicationId = null) }
        }

        fun selectMedication(medicationId: String) {
            _state.update { it.copy(selectedMedicationId = medicationId) }
        }

        fun clearMedication() {
            _state.update { it.copy(selectedMedicationId = null) }
        }

        fun logMedication(
            personId: String,
            therapyId: String,
            medicationId: String,
        ) {
            val uid = authRepository.currentUser?.id ?: return
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                val result = addManualMedicationLog(personId, therapyId, medicationId, Instant.now(), uid)
                _state.update { state ->
                    if (result.isSuccess) {
                        state.copy(isLoading = false, isSaved = true)
                    } else {
                        state.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Errore")
                    }
                }
            }
        }

        private fun loadTherapies(personId: String) {
            therapiesJob?.cancel()
            therapiesJob =
                viewModelScope.launch {
                    observeTherapies(personId)
                        .catch { e ->
                            Log.e("QuickLogViewModel", "loadTherapies($personId) error", e)
                            _state.update { it.copy(error = e.message) }
                        }
                        .collect { list ->
                            _state.update { it.copy(therapies = list.filter { t -> t.isActive }) }
                        }
                }
        }

        fun logMeal(
            personId: String,
            amount: Double?,
            amountUnit: MealUnit?,
            mealType: MealType?,
            notes: String?,
        ) {
            save(
                personId,
                ActivityLog.Meal(
                    UUID.randomUUID().toString(),
                    personId,
                    Instant.now(),
                    userId,
                    amount,
                    amountUnit,
                    mealType,
                    notes?.ifBlank {
                        null
                    },
                ),
            )
        }

        fun logDiaper(
            personId: String,
            diaperType: DiaperType,
            notes: String?,
        ) {
            save(
                personId,
                ActivityLog.Diaper(
                    UUID.randomUUID().toString(),
                    personId,
                    Instant.now(),
                    userId,
                    diaperType,
                    notes?.ifBlank { null },
                ),
            )
        }

        fun logSleep(
            personId: String,
            isStart: Boolean,
        ) {
            val log =
                if (isStart) {
                    ActivityLog.SleepStart(UUID.randomUUID().toString(), personId, Instant.now(), userId)
                } else {
                    ActivityLog.SleepEnd(UUID.randomUUID().toString(), personId, Instant.now(), userId)
                }
            save(personId, log)
        }

        fun logTemperature(
            personId: String,
            temp: Double,
            unit: TemperatureUnit,
            method: MeasurementMethod?,
            notes: String?,
        ) {
            save(
                personId,
                ActivityLog.Temperature(
                    UUID.randomUUID().toString(),
                    personId,
                    Instant.now(),
                    userId,
                    temp,
                    unit,
                    method,
                    notes?.ifBlank {
                        null
                    },
                ),
            )
        }

        fun logWeight(
            personId: String,
            weight: Double,
            weightUnit: WeightUnit,
            notes: String?,
        ) {
            save(
                personId,
                ActivityLog.Weight(
                    UUID.randomUUID().toString(), personId, Instant.now(), userId, weight, weightUnit, null, null,
                    notes?.ifBlank {
                        null
                    },
                ),
            )
        }

        fun logHygiene(
            personId: String,
            notes: String?,
        ) {
            save(
                personId,
                ActivityLog.Hygiene(
                    UUID.randomUUID().toString(),
                    personId,
                    Instant.now(),
                    userId,
                    notes?.ifBlank { null },
                ),
            )
        }

        private fun save(
            personId: String,
            log: ActivityLog,
        ) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                val result = logActivity(personId, log)
                _state.update { state ->
                    if (result.isSuccess) {
                        state.copy(isLoading = false, isSaved = true)
                    } else {
                        state.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Errore")
                    }
                }
            }
        }
    }
