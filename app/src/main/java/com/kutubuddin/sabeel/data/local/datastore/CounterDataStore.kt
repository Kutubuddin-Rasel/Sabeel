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
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CounterDataStore @Inject constructor(
    @Named("counter") private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KEY_COUNTER_VALUE = intPreferencesKey("counter_value")
        val KEY_ACTIVE_DHIKR = stringPreferencesKey("active_dhikr")
        val KEY_ACTIVE_TARGET_OVERRIDE = intPreferencesKey("active_target_override")
        val KEY_SMART_FLOW_ENABLED = booleanPreferencesKey("smart_flow_enabled")
        val KEY_SMART_FLOW_VARIANT = stringPreferencesKey("smart_flow_variant")
    }

    val counterValueFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[KEY_COUNTER_VALUE] ?: 0
    }

    /**
     * The active dhikr as a plain String key. Any catalog entry (not just the 6
     * DhikrType values) can be persisted, so every dhikr is countable. Defaults
     * to SubhanAllah's key when nothing is stored.
     */
    val activeDhikrKeyFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_ACTIVE_DHIKR] ?: DhikrType.SUBHANALLAH.name
    }

    /**
     * A per-active override for the dhikr's target (e.g. tap-to-count from the
     * Wird screen). Null when absent or 0, so the resolved catalog default applies.
     */
    val activeTargetOverrideFlow: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[KEY_ACTIVE_TARGET_OVERRIDE]?.takeIf { it > 0 }
    }

    val isSmartFlowEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_SMART_FLOW_ENABLED] ?: true
    }

    val smartFlowVariantFlow: Flow<SmartFlowVariant> = dataStore.data.map { preferences ->
        val name = preferences[KEY_SMART_FLOW_VARIANT] ?: SmartFlowVariant.CLASSIC.name
        try { SmartFlowVariant.valueOf(name) } catch (e: Exception) { SmartFlowVariant.CLASSIC }
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

    suspend fun setCounter(value: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_COUNTER_VALUE] = value
        }
    }

    suspend fun resetCounter() {
        dataStore.edit { preferences ->
            preferences[KEY_COUNTER_VALUE] = 0
        }
    }

    suspend fun setDhikrKey(key: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_DHIKR] = key
            preferences[KEY_ACTIVE_TARGET_OVERRIDE] = 0   // library launches use the default target
        }
    }

    suspend fun setDhikrKeyWithTarget(key: String, target: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_DHIKR] = key
            preferences[KEY_ACTIVE_TARGET_OVERRIDE] = target
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


}
