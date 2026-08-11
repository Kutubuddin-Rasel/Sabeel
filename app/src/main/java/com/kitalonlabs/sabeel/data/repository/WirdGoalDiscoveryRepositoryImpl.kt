package com.kitalonlabs.sabeel.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kitalonlabs.sabeel.di.IoDispatcher
import com.kitalonlabs.sabeel.domain.repository.WirdGoalDiscoveryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LSP: wraps writes in [ioDispatcher] internally so any consumer (ViewModel)
 * can call [markGoalEditHintSeen] from the Main dispatcher without assuming
 * — or needing to know — which dispatcher the persistence layer uses.
 */
@Singleton
class WirdGoalDiscoveryRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WirdGoalDiscoveryRepository {

    companion object {
        val KEY_GOAL_EDIT_HINT_SEEN = booleanPreferencesKey("wird_goal_edit_hint_seen")
    }

    override val isGoalEditHintUnseen: Flow<Boolean> =
        dataStore.data.map { prefs -> !(prefs[KEY_GOAL_EDIT_HINT_SEEN] ?: false) }

    override suspend fun markGoalEditHintSeen() = withContext(ioDispatcher) {
        dataStore.edit { it[KEY_GOAL_EDIT_HINT_SEEN] = true }
        Unit
    }
}
