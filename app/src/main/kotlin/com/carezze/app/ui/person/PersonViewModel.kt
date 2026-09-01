package com.fpculcasi.carezze.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.AuthRepository
import com.fpculcasi.carezze.domain.usecase.person.CreatePersonUseCase
import com.fpculcasi.carezze.domain.usecase.person.DeletePersonUseCase
import com.fpculcasi.carezze.domain.usecase.person.ObservePersonsUseCase
import com.fpculcasi.carezze.domain.usecase.person.UpdatePersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val observePersons: ObservePersonsUseCase,
    private val createPerson: CreatePersonUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val deletePersonUseCase: DeletePersonUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val userId: String? get() = authRepository.currentUser?.id

    val persons: StateFlow<List<Person>> = userId
        ?.let { observePersons(it) }
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        ?: MutableStateFlow(emptyList())

    fun createPerson(name: String, nickname: String?) {
        val uid = userId ?: return
        viewModelScope.launch {
            createPerson(name, nickname?.takeIf { it.isNotBlank() }, uid)
        }
    }

    fun updatePerson(person: Person) {
        viewModelScope.launch { updatePersonUseCase(person) }
    }

    fun deletePerson(personId: String) {
        viewModelScope.launch { deletePersonUseCase(personId) }
    }
}
