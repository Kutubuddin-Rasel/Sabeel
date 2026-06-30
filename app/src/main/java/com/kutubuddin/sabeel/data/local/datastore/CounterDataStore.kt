package com.kutubuddin.sabeel.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kutubuddin.sabeel.domain.model.DhikrType
import com.kutubuddin.sabeel.domain.model.SmartFlowVariant
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
        val KEY_ACTIVE_DHIKR = stringPreferencesKey("active_dhikr")
        val KEY_SMART_FLOW_ENABLED = booleanPreferencesKey("smart_flow_enabled")
        val KEY_SMART_FLOW_VARIANT = stringPreferencesKey("smart_flow_variant")
        val KEY_POCKET_MODE_ACTIVE = booleanPreferencesKey("pocket_mode_active")
    }

    val counterValueFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_COUNTER_VALUE] ?: 0
    }

    val activeDhikrFlow: Flow<DhikrType> = dataStore.data.map { preferences ->
        val name = preferences[KEY_ACTIVE_DHIKR] ?: DhikrType.SUBHANALLAH.name
        try { DhikrType.valueOf(name) } catch (e: Exception) { DhikrType.SUBHANALLAH }
    }

    val isSmartFlowEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_SMART_FLOW_ENABLED] ?: true
    }

    val smartFlowVariantFlow: Flow<SmartFlowVariant> = dataStore.data.map { preferences ->
        val name = preferences[KEY_SMART_FLOW_VARIANT] ?: SmartFlowVariant.CLASSIC.name
        try { SmartFlowVariant.valueOf(name) } catch (e: Exception) { SmartFlowVariant.CLASSIC }
    }

    val isPocketModeActiveFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_POCKET_MODE_ACTIVE] ?: false
    }

    suspend fun incrementCounter() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_COUNTER_VALUE] ?: 0
            preferences[KEY_COUNTER_VALUE] = current + 1
        }
    }

    suspend fun decrementCounter() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_COUNTER_VALUE] ?: 0
            preferences[KEY_COUNTER_VALUE] = maxOf(0, current - 1)
        }
    }

    suspend fun resetCounter() {
        dataStore.edit { preferences ->
            preferences[KEY_COUNTER_VALUE] = 0
        }
    }

    suspend fun setDhikr(dhikr: DhikrType) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_DHIKR] = dhikr.name
        }
    }

    suspend fun setSmartFlowEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SMART_FLOW_ENABLED] = enabled
        }
    }

    suspend fun setSmartFlowVariant(variant: SmartFlowVariant) {
        dataStore.edit { preferences ->
            preferences[KEY_SMART_FLOW_VARIANT] = variant.name
        }
    }

    suspend fun setPocketModeActive(active: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_POCKET_MODE_ACTIVE] = active
        }
    }
}
