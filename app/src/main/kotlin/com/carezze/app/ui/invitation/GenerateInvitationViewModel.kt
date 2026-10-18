package com.fpculcasi.carezze.ui.invitation

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.data.util.QrCodeGenerator
import com.fpculcasi.carezze.domain.model.Invitation
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.invitation.GenerateInvitationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GenerateInvitationUiState {
    data object Loading : GenerateInvitationUiState
    data class Success(val invitation: Invitation, val qrBitmap: Bitmap) : GenerateInvitationUiState
    data class Error(val message: String) : GenerateInvitationUiState
}

@HiltViewModel
class GenerateInvitationViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val generateInvitationUseCase: GenerateInvitationUseCase,
        private val qrCodeGenerator: QrCodeGenerator,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val personId: String = checkNotNull(savedStateHandle["personId"])
        private val personName: String = checkNotNull(savedStateHandle["personName"])

        private val _uiState = MutableStateFlow<GenerateInvitationUiState>(GenerateInvitationUiState.Loading)
        val uiState: StateFlow<GenerateInvitationUiState> = _uiState

        init {
            generateInvitation()
        }

        fun generateInvitation() {
            val user = authRepository.currentUser ?: run {
                _uiState.value = GenerateInvitationUiState.Error("Accesso richiesto per condividere")
                return
            }
            _uiState.value = GenerateInvitationUiState.Loading
            viewModelScope.launch {
                generateInvitationUseCase(
                    type = InvitationType.PERSON,
                    targetId = personId,
                    personId = null,
                    userId = user.id,
                    userName = user.displayName,
                    targetName = personName,
                ).fold(
                    onSuccess = { invitation ->
                        val bitmap = qrCodeGenerator.generate(invitation.code)
                        _uiState.value = GenerateInvitationUiState.Success(invitation, bitmap)
                    },
                    onFailure = { e ->
                        _uiState.value = GenerateInvitationUiState.Error(e.message ?: "Errore generazione invito")
                    },
                )
            }
        }
    }
