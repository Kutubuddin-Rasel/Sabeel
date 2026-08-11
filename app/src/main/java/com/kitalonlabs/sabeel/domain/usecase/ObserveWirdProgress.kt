package com.kitalonlabs.sabeel.domain.usecase

import com.kitalonlabs.sabeel.domain.model.WirdProgress
import com.kitalonlabs.sabeel.domain.model.WirdProgressItem
import com.kitalonlabs.sabeel.domain.model.liveContribution
import com.kitalonlabs.sabeel.domain.repository.DhikrRepository
import com.kitalonlabs.sabeel.domain.repository.SessionRepository
import com.kitalonlabs.sabeel.domain.repository.TasbihCounterObserver
import com.kitalonlabs.sabeel.domain.repository.WirdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.CoroutineDispatcher
import com.kitalonlabs.sabeel.di.DefaultDispatcher
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * Derived daily-wird progress: the plan joined with today's per-key session sums
 * and the merged catalog, with the live in-progress round folded in. Items whose
 * key no longer resolves (e.g. a deleted custom dhikr) are dropped.
 *
 * DIP + ISP: depends on [TasbihCounterObserver] — this use case only ever
 * reads the live count/dhikr, never mutates the counter or touches Smart
 * Flow/Pocket Mode/streak state, so it has no business depending on the full
 * [com.kitalonlabs.sabeel.domain.repository.TasbihRepository].
 */
class ObserveWirdProgress @Inject constructor(
    private val wirdRepository: WirdRepository,
    private val dhikrRepository: DhikrRepository,
    private val sessionRepository: SessionRepository,
    private val counterObserver: TasbihCounterObserver,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    operator fun invoke(dateKey: String): Flow<WirdProgress> = combine(
        wirdRepository.observeWird(),
        dhikrRepository.getAllDhikr(),
        sessionRepository.getCountsByKeyForDate(dateKey),
        counterObserver.activeDhikr,
        combine(counterObserver.activeCount, counterObserver.sessionStartCount) { a, b -> a to b }
    ) { plan, catalog, savedByKey, activeDhikr, countPair ->
        val activeCount = countPair.first
        val startCount = countPair.second
        val catalogByKey = catalog.associateBy { it.key }
        val items = plan.mapNotNull { item ->
            val d = catalogByKey[item.dhikrKey] ?: return@mapNotNull null
            val saved = savedByKey[item.dhikrKey] ?: 0
            val live = liveContribution(item.dhikrKey, activeDhikr.key, activeCount, startCount)
            WirdProgressItem(
                dhikrKey = item.dhikrKey,
                displayName = d.displayName,
                arabicText = d.arabicText,
                transliteration = d.transliteration,
                target = item.target,
                countToday = minOf(saved + live, item.target),
                position = item.position
            )
        }
        WirdProgress(items.toImmutableList())
    }.flowOn(defaultDispatcher)
}