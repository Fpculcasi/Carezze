package com.fpculcasi.carezze.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.personColorDataStore: DataStore<Preferences> by preferencesDataStore(name = "person_colors")

@Singleton
class PersonColorStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun observeColorIndex(personId: String): Flow<Int> =
        context.personColorDataStore.data.map { prefs ->
            prefs[intPreferencesKey("color_$personId")] ?: 0
        }

    suspend fun setColorIndex(personId: String, index: Int) {
        context.personColorDataStore.edit { prefs ->
            prefs[intPreferencesKey("color_$personId")] = index
        }
    }
}
