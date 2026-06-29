package com.kutubuddin.sabeel.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KEY_COUNTER_VALUE = intPreferencesKey("counter_value")
    }

    val counterValueFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_COUNTER_VALUE] ?: 0
    }

    suspend fun incrementCounter() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_COUNTER_VALUE] ?: 0
            preferences[KEY_COUNTER_VALUE] = current + 1
        }
    }

    suspend fun resetCounter() {
        dataStore.edit { preferences ->
            preferences[KEY_COUNTER_VALUE] = 0
        }
    }
}
