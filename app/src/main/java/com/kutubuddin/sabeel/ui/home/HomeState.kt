package com.kutubuddin.sabeel.ui.home

import com.kutubuddin.sabeel.domain.model.LocalizedText
import com.kutubuddin.sabeel.domain.model.WirdGoalHintState
import com.kutubuddin.sabeel.domain.model.WirdProgress

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet

@Immutable
data class HomeState(
    val todaysSessions: ImmutableList<SessionSummary> = persistentListOf(),
    val totalToday: Int = 0,
    val wird: WirdSummary = WirdSummary(),
    val currentStreak: Int = 0,
    val totalAllTime: Int = 0,
    val totalSessionCount: Int = 0,
    val heroState: HomeHeroState = HomeHeroState.None,
    val greeting: GreetingType = GreetingType.DEFAULT,
    val language: String = "en",
    // JANK-04: date label owned by ViewModel and refreshed by a midnight-aligned ticker.
    // Previously computed in HomeScreen via remember(state.language) { LocalDate.now()... }
    // which cached the date until the language changed — causing a stale label after midnight.
    val todayLabel: String = ""
)

/** Represents the dynamic primary action (Hero CTA) on the Home Screen. */
@Immutable
sealed interface HomeHeroState {
    /** The user has a session to resume or a goal to continue. */
    data class Resume(
        val dhikrKey: String,
        val displayName: LocalizedText,
        val lastCount: Int,
        val target: Int,
        val isDailyGoal: Boolean
    ) : HomeHeroState

    /** The user has no Daily Goal set up. */
    object SetupGoal : HomeHeroState

    /** The goal is finished and no active library sessions exist. */
    object None : HomeHeroState
}

/**
 * UI-safe projection of [com.kutubuddin.sabeel.data.local.db.entity.DhikrSessionEntity].
 *
 * SRP/DIP: the Room entity must never cross into the Composable layer, and
 * resolving a display name from [com.kutubuddin.sabeel.domain.model.DhikrCatalog]
 * is a domain lookup, not rendering — both concerns are handled once, in
 * [HomeViewModel], never inside a Composable.
 */
@Immutable
data class SessionSummary(
    val dhikrKey: String,
    val displayName: LocalizedText,
    val count: Int,
    val isComplete: Boolean
)

/** Lean wird view for the Home summary card — numbers only, no item list. */
@Immutable
data class WirdSummary(
    val completed: Int = 0,
    val total: Int = 0,
    val countedSum: Int = 0,
    val targetSum: Int = 0,
    val isEmpty: Boolean = true,
    val nextItemName: LocalizedText? = null,
    val nextItemKey: String? = null,
    val nextItemTarget: Int? = null,
    // OPT-05: The Count tab needs these to derive isDailyGoalFinished and isDhikrInDailyGoal
    // without holding its own WirdViewModel. By including them here, WirdViewModel can be
    // scoped to home_graph and the Count tab reads HomeViewModel.state.wird instead.
    val allComplete: Boolean = false,
    val wirdItemKeys: ImmutableSet<String> = persistentSetOf()
)

fun WirdProgress.toSummary(): WirdSummary {
    val next = nextIncompleteItem
    return WirdSummary(
        completed = completed,
        total = total,
        countedSum = countedSum,
        targetSum = targetSum,
        isEmpty = items.isEmpty(),
        nextItemName = next?.displayName,
        nextItemKey = next?.dhikrKey,
        nextItemTarget = next?.target,
        allComplete = allComplete,
        wirdItemKeys = items.map { it.dhikrKey }.toImmutableSet()
    )
}