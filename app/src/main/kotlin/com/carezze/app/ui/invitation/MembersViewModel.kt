package com.fpculcasi.carezze.ui.invitation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.InvitationType
import com.fpculcasi.carezze.domain.model.MemberRole
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.invitation.RevokeAccessUseCase
import com.fpculcasi.carezze.domain.usecase.person.ObservePersonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemberItem(
    val userId: String,
    val role: MemberRole,
)

data class MembersUiState(
    val personName: String = "",
    val members: List<MemberItem> = emptyList(),
    val currentUserId: String = "",
    val isOwner: Boolean = false,
    val isRevoking: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class MembersViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observePersonsUseCase: ObservePersonsUseCase,
        private val revokeAccessUseCase: RevokeAccessUseCase,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val personId: String = checkNotNull(savedStateHandle["personId"])
        private val currentUserId = authRepository.currentUser?.id ?: ""

        val uiState: StateFlow<MembersUiState> =
            observePersonsUseCase(currentUserId)
                .map { persons ->
                    val person = persons.firstOrNull { it.id == personId }
                    MembersUiState(
                        personName = person?.name ?: "",
                        members = person?.members?.map { (uid, role) -> MemberItem(uid, role) } ?: emptyList(),
                        currentUserId = currentUserId,
                        isOwner = person?.members?.get(currentUserId) == MemberRole.OWNER,
                    )
                }
                .catch { emit(MembersUiState(error = it.message)) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MembersUiState())

        fun revokeAccess(memberUserId: String) {
            viewModelScope.launch {
                revokeAccessUseCase(
                    targetId = personId,
                    type = InvitationType.PERSON,
                    memberUserId = memberUserId,
                )
            }
        }
    }
