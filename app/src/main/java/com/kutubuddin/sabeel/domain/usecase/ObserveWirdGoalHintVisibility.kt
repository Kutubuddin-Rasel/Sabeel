package com.kutubuddin.sabeel.domain.usecase

import com.kutubuddin.sabeel.domain.model.WirdGoalHintState
import com.kutubuddin.sabeel.domain.repository.WirdGoalDiscoveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * SRP: single action — resolve whether the "add/update your daily goal"
 * discoverability hint should render, as a [WirdGoalHintState].
 * DIP: depends only on the [WirdGoalDiscoveryRepository] abstraction.
 */
class ObserveWirdGoalHintVisibility @Inject constructor(
    private val repository: WirdGoalDiscoveryRepository
) {
    operator fun invoke(): Flow<WirdGoalHintState> =
        repository.isGoalEditHintUnseen.map { unseen ->
            if (unseen) WirdGoalHintState.Visible else WirdGoalHintState.Hidden
        }
}
