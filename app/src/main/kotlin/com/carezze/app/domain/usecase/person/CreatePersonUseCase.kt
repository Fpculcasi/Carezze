package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.model.Person
import com.fpculcasi.carezze.domain.repository.PersonRepository
import javax.inject.Inject

class CreatePersonUseCase @Inject constructor(
    private val personRepository: PersonRepository,
) {
    suspend operator fun invoke(name: String, nickname: String?, userId: String): Result<Person> =
        personRepository.createPerson(name, nickname, userId)
}
