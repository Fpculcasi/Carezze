package com.fpculcasi.carezze.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.auth.ObserveAuthStateUseCase
import com.fpculcasi.carezze.domain.usecase.auth.SignOutUseCase
import com.fpculcasi.carezze.domain.usecase.user.ObserveUserUseCase
import com.fpculcasi.carezze.domain.usecase.user.SyncUserUseCase
import com.fpculcasi.carezze.ui.auth.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val observeAuthState: ObserveAuthStateUseCase,
        private val observeUser: ObserveUserUseCase,
        private val syncUser: SyncUserUseCase,
        private val signOutUseCase: SignOutUseCase,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        val authState: StateFlow<AuthUiState> =
            observeAuthState()
                .map { user ->
                    when {
                        user == null -> AuthUiState.SignedOut
                        user.isAnonymous -> AuthUiState.Anonymous(user)
                        else -> AuthUiState.Authenticated(user)
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = AuthUiState.Loading,
                )

        val userState: StateFlow<User?> =
            authRepository.currentUser?.id
                ?.let { observeUser(it) }
                ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
                ?: MutableStateFlow(null)

        fun updateDisplayName(name: String) {
            val current = userState.value ?: return
            viewModelScope.launch { syncUser(current.copy(displayName = name)) }
        }

        fun signOut() {
            viewModelScope.launch { signOutUseCase() }
        }
    }
