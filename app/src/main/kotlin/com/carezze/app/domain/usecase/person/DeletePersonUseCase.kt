package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.domain.repository.PersonRepository
import javax.inject.Inject

class DeletePersonUseCase @Inject constructor(
    private val personRepository: PersonRepository,
) {
    suspend operator fun invoke(personId: String): Result<Unit> = personRepository.deletePerson(personId)
}
