package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.data.local.PersonColorStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePersonColorUseCase @Inject constructor(
    private val store: PersonColorStore,
) {
    operator fun invoke(personId: String): Flow<Int> = store.observeColorIndex(personId)
}
