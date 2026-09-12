package com.fpculcasi.carezze.domain.usecase.person

import com.fpculcasi.carezze.data.local.PersonColorStore
import javax.inject.Inject

class SetPersonColorUseCase @Inject constructor(
    private val store: PersonColorStore,
) {
    suspend operator fun invoke(personId: String, colorIndex: Int) =
        store.setColorIndex(personId, colorIndex)
}
