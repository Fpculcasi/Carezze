package com.fpculcasi.carezze.ui.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.invitation.RedeemInvitationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RedeemInvitationUiState {
    data object Idle : RedeemInvitationUiState
    data object Loading : RedeemInvitationUiState
    data class Success(val invitation: Invitation) : RedeemInvitationUiState
    data class Error(val message: String) : RedeemInvitationUiState
}

@HiltViewModel
class RedeemInvitationViewModel
    @Inject
    constructor(
        private val redeemInvitationUseCase: RedeemInvitationUseCase,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<RedeemInvitationUiState>(RedeemInvitationUiState.Idle)
        val uiState: StateFlow<RedeemInvitationUiState> = _uiState

        fun redeem(code: String) {
            val user = authRepository.currentUser ?: run {
                _uiState.value = RedeemInvitationUiState.Error("Accesso richiesto per riscattare un invito")
                return
            }
            if (code.length != 8) {
                _uiState.value = RedeemInvitationUiState.Error("Il codice deve essere di 8 caratteri")
                return
            }
            _uiState.value = RedeemInvitationUiState.Loading
            viewModelScope.launch {
                redeemInvitationUseCase(code.uppercase().trim(), user.id).fold(
                    onSuccess = { invitation ->
                        _uiState.value = RedeemInvitationUiState.Success(invitation)
                    },
                    onFailure = { e ->
                        val msg = when {
                            e.message?.contains("already used") == true -> "Codice già utilizzato"
                            e.message?.contains("expired") == true -> "Codice scaduto"
                            e.message?.contains("No invitation") == true -> "Codice non trovato"
                            else -> e.message ?: "Errore sconosciuto"
                        }
                        _uiState.value = RedeemInvitationUiState.Error(msg)
                    },
                )
            }
        }

        fun reset() {
            _uiState.value = RedeemInvitationUiState.Idle
        }
    }
