package com.fpculcasi.carezze.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.activity.ObserveActivityLogsUseCase
import com.fpculcasi.carezze.domain.usecase.person.ObservePersonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

enum class DashboardViewMode { CARD, FEED }

@HiltViewModel
class DashboardViewModel
    @Inject
    constructor(
        private val observePersons: ObservePersonsUseCase,
        private val observeActivityLogs: ObserveActivityLogsUseCase,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val userId: String? get() = authRepository.currentUser?.id

        val persons: StateFlow<List<Person>> =
            userId
                ?.let { observePersons(it) }
                ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
                ?: MutableStateFlow(emptyList())

        val selectedPersonId = MutableStateFlow<String?>(null)

        val viewMode = MutableStateFlow(DashboardViewMode.CARD)

        val recentLogs: StateFlow<List<ActivityLog>> =
            combine(persons, selectedPersonId) { personList, selectedId ->
                if (selectedId == null) personList else personList.filter { it.id == selectedId }
            }.flatMapLatest { relevant ->
                if (relevant.isEmpty()) return@flatMapLatest flowOf(emptyList())
                val from = Instant.now().minus(7, ChronoUnit.DAYS)
                val to = Instant.now()
                val flows = relevant.map { observeActivityLogs(it.id, from, to) }
                if (flows.size == 1) {
                    flows[0]
                } else {
                    combine(flows) { arrays -> arrays.flatMap { it }.sortedByDescending { it.timestamp } }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        fun selectPerson(id: String?) {
            selectedPersonId.value = id
        }

        fun toggleViewMode() {
            viewMode.value =
                if (viewMode.value == DashboardViewMode.CARD) DashboardViewMode.FEED else DashboardViewMode.CARD
        }
    }
