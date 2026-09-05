package com.fpculcasi.carezze.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.User
import com.fpculcasi.carezze.domain.usecase.auth.CreateUserWithEmailUseCase
import com.fpculcasi.carezze.domain.usecase.auth.GetCurrentUserUseCase
import com.fpculcasi.carezze.domain.usecase.auth.LinkWithEmailUseCase
import com.fpculcasi.carezze.domain.usecase.auth.LinkWithGoogleUseCase
import com.fpculcasi.carezze.domain.usecase.auth.ObserveAuthStateUseCase
import com.fpculcasi.carezze.domain.usecase.auth.SignInAnonymouslyUseCase
import com.fpculcasi.carezze.domain.usecase.auth.SignInWithEmailUseCase
import com.fpculcasi.carezze.domain.usecase.auth.SignInWithGoogleUseCase
import com.fpculcasi.carezze.domain.usecase.user.SyncUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val signInAnonymously: SignInAnonymouslyUseCase,
        private val signInWithEmail: SignInWithEmailUseCase,
        private val createUserWithEmail: CreateUserWithEmailUseCase,
        private val linkWithEmail: LinkWithEmailUseCase,
        private val signInWithGoogle: SignInWithGoogleUseCase,
        private val linkWithGoogle: LinkWithGoogleUseCase,
        private val getCurrentUser: GetCurrentUserUseCase,
        private val observeAuthState: ObserveAuthStateUseCase,
        private val syncUser: SyncUserUseCase,
    ) : ViewModel() {
        val authState: StateFlow<AuthUiState> =
            observeAuthState()
                .map { user ->
                    if (user != null) viewModelScope.launch { syncUser(user) }
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

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

        fun clearError() {
            _errorMessage.value = null
        }

        fun continueLocally() {
            viewModelScope.launch {
                signInAnonymously().onFailure { _errorMessage.value = it.localizedMessage }
            }
        }

        fun signIn(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                signInWithEmail(email, password).onFailure { _errorMessage.value = it.localizedMessage }
            }
        }

        fun registerOrLink(
            email: String,
            password: String,
        ) {
            viewModelScope.launch {
                val currentUser = getCurrentUser()
                val result =
                    if (currentUser?.isAnonymous == true) {
                        linkWithEmail(email, password)
                    } else {
                        createUserWithEmail(email, password)
                    }
                result.onFailure { _errorMessage.value = it.localizedMessage }
            }
        }

        fun signInOrLinkWithGoogle(idToken: String) {
            viewModelScope.launch {
                val currentUser = getCurrentUser()
                val result =
                    if (currentUser?.isAnonymous == true) {
                        linkWithGoogle(idToken)
                    } else {
                        signInWithGoogle(idToken)
                    }
                result.onFailure { _errorMessage.value = it.localizedMessage }
            }
        }
    }

sealed interface AuthUiState {
    data object Loading : AuthUiState

    data object SignedOut : AuthUiState

    data class Anonymous(val user: User) : AuthUiState

    data class Authenticated(val user: User) : AuthUiState
}
