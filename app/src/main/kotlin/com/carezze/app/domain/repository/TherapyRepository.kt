package com.fpculcasi.carezze.domain.repository

import com.fpculcasi.carezze.domain.model.Medication
import com.fpculcasi.carezze.domain.model.Therapy
import com.fpculcasi.carezze.domain.model.TherapyDuration
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TherapyRepository {
    fun observeTherapies(personId: String): Flow<List<Therapy>>
    suspend fun getTherapy(personId: String, therapyId: String): Result<Therapy>
    suspend fun createTherapy(
        personId: String,
        name: String,
        startDate: LocalDate,
        duration: TherapyDuration,
        medications: List<Medication>,
        userId: String,
    ): Result<Therapy>
    suspend fun updateTherapy(therapy: Therapy): Result<Unit>
    suspend fun deleteTherapy(personId: String, therapyId: String): Result<Unit>
}
