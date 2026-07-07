package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.domain.model.WirdProgress
import com.kutubuddin.sabeel.domain.model.WirdProgressItem
import com.kutubuddin.sabeel.domain.model.liveContribution
import com.kutubuddin.sabeel.domain.repository.DhikrRepository
import com.kutubuddin.sabeel.domain.repository.SessionRepository
import com.kutubuddin.sabeel.domain.repository.TasbihCounterObserver
import com.kutubuddin.sabeel.domain.repository.WirdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Derived daily-wird progress: the plan joined with today's per-key session sums
 * and the merged catalog, with the live in-progress round folded in. Items whose
 * key no longer resolves (e.g. a deleted custom dhikr) are dropped.
 *
 * DIP + ISP: depends on [TasbihCounterObserver] — this use case only ever
 * reads the live count/dhikr, never mutates the counter or touches Smart
 * Flow/Pocket Mode/streak state, so it has no business depending on the full
 * [com.kutubuddin.sabeel.domain.repository.TasbihRepository].
 */
class ObserveWirdProgress @Inject constructor(
    private val wirdRepository: WirdRepository,
    private val dhikrRepository: DhikrRepository,
    private val sessionRepository: SessionRepository,
    private val counterObserver: TasbihCounterObserver
) {
    operator fun invoke(dateKey: String): Flow<WirdProgress> = combine(
        wirdRepository.observeWird(),
        dhikrRepository.getAllDhikr(),
        sessionRepository.getCountsByKeyForDate(dateKey),
        counterObserver.activeDhikr,
        counterObserver.activeCount
    ) { plan, catalog, savedByKey, activeDhikr, activeCount ->
        val catalogByKey = catalog.associateBy { it.key }
        val items = plan.mapNotNull { item ->
            val d = catalogByKey[item.dhikrKey] ?: return@mapNotNull null
            val saved = savedByKey[item.dhikrKey] ?: 0
            val live = liveContribution(item.dhikrKey, activeDhikr.key, activeCount, activeDhikr.target)
            WirdProgressItem(
                dhikrKey = item.dhikrKey,
                displayName = d.displayName,
                arabicText = d.arabicText,
                transliteration = d.transliteration,
                target = item.target,
                countToday = saved + live,
                position = item.position
            )
        }
        WirdProgress(items)
    }
}