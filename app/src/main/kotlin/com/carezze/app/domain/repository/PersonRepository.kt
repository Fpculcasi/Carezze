package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun observePersons(userId: String): Flow<List<Person>>

    suspend fun getPerson(personId: String): Result<Person>

    suspend fun createPerson(
        name: String,
        nickname: String?,
        userId: String,
    ): Result<Person>

    suspend fun updatePerson(person: Person): Result<Unit>

    suspend fun deletePerson(personId: String): Result<Unit>
}
