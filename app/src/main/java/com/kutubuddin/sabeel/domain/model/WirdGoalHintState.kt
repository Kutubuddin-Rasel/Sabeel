package com.kutubuddin.sabeel.domain.model

/**
 * OCP: sealed family for the wird-goal discoverability hint. New variants
 * (e.g. a re-prompt after N days of an empty wird) extend this set without
 * modifying [com.kutubuddin.sabeel.domain.usecase.ObserveWirdGoalHintVisibility]
 * or any existing `when` consumer beyond adding the new branch.
 */
sealed interface WirdGoalHintState {
    data object Hidden : WirdGoalHintState
    data object Visible : WirdGoalHintState
}
