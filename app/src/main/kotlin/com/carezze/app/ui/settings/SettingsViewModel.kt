package com.fpculcasi.carezze.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.Language
import com.fpculcasi.carezze.domain.model.TemperatureUnit
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.usecase.user.ObserveUserUseCase
import com.fpculcasi.carezze.domain.usecase.user.SyncUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUser: ObserveUserUseCase,
    private val syncUser: SyncUserUseCase,
    private val authRepository: com.fpculcasi.carezze.domain.repository.AuthRepository,
) : ViewModel() {

    val settingsState: StateFlow<User?> = authRepository.currentUser?.id
        ?.let { observeUser(it) }
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
        ?: kotlinx.coroutines.flow.MutableStateFlow(null)

    fun setLanguage(language: Language) = updateUser { copy(language = language) }

    fun setTemperatureUnit(unit: TemperatureUnit) = updateUser { copy(temperatureUnit = unit) }

    fun setQuietHoursStart(time: String) = updateUser { copy(quietHoursStart = time) }

    fun setQuietHoursEnd(time: String) = updateUser { copy(quietHoursEnd = time) }

    private fun updateUser(transform: User.() -> User) {
        val current = settingsState.value ?: return
        viewModelScope.launch { syncUser(current.transform()) }
    }
}
