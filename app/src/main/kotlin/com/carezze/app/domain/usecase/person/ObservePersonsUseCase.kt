package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePersonsUseCase @Inject constructor(
    private val personRepository: PersonRepository,
) {
    operator fun invoke(userId: String): Flow<List<Person>> = personRepository.observePersons(userId)
}
