package com.fpculcasi.carezze.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.ActivityLog
import com.fpculcasi.carezze.domain.usecase.activity.ObserveActivityLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val observeActivityLogs: ObserveActivityLogsUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val personId: String = checkNotNull(savedStateHandle["personId"])

        private val from: Instant = Instant.now().minus(30, ChronoUnit.DAYS)
        private val to: Instant = Instant.now()

        val logs: StateFlow<List<ActivityLog>> =
            observeActivityLogs(personId, from, to)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }
