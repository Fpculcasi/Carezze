package com.fpculcasi.carezze.ui.therapy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.MedicationLog
import com.fpculcasi.carezze.domain.model.MedicationStatus
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.therapy.CreateTherapyUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.DeleteTherapyUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ObserveTherapiesUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ObserveLogsUseCase
import com.fpculcasi.carezze.domain.usecase.therapy.ScheduleCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class MedicationFormState(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val dosage: String = "1",
    val dosageUnit: String = "pillola",
    val frequencyHours: Int = 24,
)

data class AddTherapyFormState(
    val step: Int = 1,
    val therapyName: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val isFixed: Boolean = false,
    val fixedDays: String = "7",
    val medications: List<MedicationFormState> = listOf(MedicationFormState()),
)

@HiltViewModel
class TherapyViewModel @Inject constructor(
    private val observeTherapies: ObserveTherapiesUseCase,
    private val observeLogs: ObserveLogsUseCase,
    private val createTherapy: CreateTherapyUseCase,
    private val deleteTherapy: DeleteTherapyUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val userId: String? get() = authRepository.currentUser?.id

    private val _therapiesMap = mutableMapOf<String, StateFlow<List<Therapy>>>()
    private val _logsMap = mutableMapOf<String, StateFlow<List<MedicationLog>>>()

    fun therapiesFor(personId: String): StateFlow<List<Therapy>> =
        _therapiesMap.getOrPut(personId) {
            observeTherapies(personId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }

    fun logsFor(personId: String, therapyId: String): StateFlow<List<MedicationLog>> =
        _logsMap.getOrPut("$personId/$therapyId") {
            observeLogs(personId, therapyId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }

    fun progressFor(therapy: Therapy, logs: List<MedicationLog>): Float {
        val fixed = therapy.duration as? TherapyDuration.Fixed ?: return -1f
        val totalDoses = therapy.medications.sumOf { med ->
            val dosesPerDay = if (med.frequencyHours > 0) 24 / med.frequencyHours else 1
            dosesPerDay * fixed.days
        }
        if (totalDoses == 0) return 0f
        val taken = logs.count { it.status == MedicationStatus.TAKEN }
        return (taken.toFloat() / totalDoses).coerceIn(0f, 1f)
    }

    fun remainingDoses(therapy: Therapy, logs: List<MedicationLog>): Int {
        val fixed = therapy.duration as? TherapyDuration.Fixed ?: return -1
        val totalDoses = therapy.medications.sumOf { med ->
            val dosesPerDay = if (med.frequencyHours > 0) 24 / med.frequencyHours else 1
            dosesPerDay * fixed.days
        }
        val completed = logs.count { it.status == MedicationStatus.TAKEN || it.status == MedicationStatus.SKIPPED }
        return (totalDoses - completed).coerceAtLeast(0)
    }

    private val _form = MutableStateFlow(AddTherapyFormState())
    val form: StateFlow<AddTherapyFormState> = _form.asStateFlow()

    fun resetForm() { _form.value = AddTherapyFormState() }

    fun updateTherapyName(name: String) = _form.update { it.copy(therapyName = name) }
    fun updateStartDate(date: LocalDate) = _form.update { it.copy(startDate = date) }
    fun updateIsFixed(fixed: Boolean) = _form.update { it.copy(isFixed = fixed) }
    fun updateFixedDays(days: String) = _form.update { it.copy(fixedDays = days) }

    fun addMedication() = _form.update { it.copy(medications = it.medications + MedicationFormState()) }
    fun removeMedication(id: String) = _form.update { it.copy(medications = it.medications.filter { m -> m.id != id }) }
    fun updateMedication(updated: MedicationFormState) = _form.update {
        it.copy(medications = it.medications.map { m -> if (m.id == updated.id) updated else m })
    }

    fun nextStep() = _form.update { it.copy(step = it.step + 1) }
    fun prevStep() = _form.update { it.copy(step = it.step - 1) }

    fun submitTherapy(personId: String, onDone: () -> Unit) {
        val uid = userId ?: return
        val state = _form.value
        val duration = if (state.isFixed)
            TherapyDuration.Fixed(state.fixedDays.toIntOrNull() ?: 7)
        else
            TherapyDuration.Indefinite
        val medications = state.medications.mapNotNull { m ->
            if (m.name.isBlank()) return@mapNotNull null
            val dosage = m.dosage.toDoubleOrNull() ?: 1.0
            Medication(
                id = m.id,
                name = m.name,
                dosage = dosage,
                dosageUnit = m.dosageUnit,
                frequencyHours = m.frequencyHours,
                scheduledTimes = ScheduleCalculator.computeScheduledTimes(m.frequencyHours),
                startDate = state.startDate,
                notes = null,
            )
        }
        viewModelScope.launch {
            createTherapy(
                personId = personId,
                name = state.therapyName,
                startDate = state.startDate,
                duration = duration,
                medications = medications,
                userId = uid,
            )
            resetForm()
            onDone()
        }
    }

    fun deleteTherapy(personId: String, therapyId: String) {
        viewModelScope.launch { deleteTherapy(personId, therapyId) }
    }
}
