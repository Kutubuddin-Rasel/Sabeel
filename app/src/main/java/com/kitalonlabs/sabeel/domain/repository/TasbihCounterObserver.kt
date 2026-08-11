package com.kitalonlabs.sabeel.domain.repository

import com.kitalonlabs.sabeel.domain.model.ActiveDhikr
import kotlinx.coroutines.flow.Flow

/**
 * ISP: the read-only slice of the counting screen's live state. Consumers that
 * only *display* the current dhikr/count (e.g. [com.kitalonlabs.sabeel.ui.home.HomeViewModel],
 * [com.kitalonlabs.sabeel.domain.usecase.ObserveWirdProgress]) depend on this
 * narrow contract instead of the full read/write [TasbihRepository], so they
 * can never accidentally call a mutation method they have no business calling.
 */
interface TasbihCounterObserver {
    val activeCount: Flow<Int>
    val activeDhikr: Flow<ActiveDhikr>
    val sessionStartCount: Flow<Int>
}