package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import javax.inject.Inject

class UpdatePersonUseCase
    @Inject
    constructor(
        private val personRepository: PersonRepository,
    ) {
        suspend operator fun invoke(person: Person): Result<Unit> = personRepository.updatePerson(person)
    }
