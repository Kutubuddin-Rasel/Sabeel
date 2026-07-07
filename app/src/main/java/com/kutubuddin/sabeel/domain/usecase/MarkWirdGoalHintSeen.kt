package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.domain.repository.WirdGoalDiscoveryRepository
import javax.inject.Inject

/**
 * SRP: single action — record that the user has used a wird-edit entry
 * point, so the discoverability hint never renders again.
 * DIP: depends only on the [WirdGoalDiscoveryRepository] abstraction.
 */
class MarkWirdGoalHintSeen @Inject constructor(
    private val repository: WirdGoalDiscoveryRepository
) {
    suspend operator fun invoke() = repository.markGoalEditHintSeen()
}
