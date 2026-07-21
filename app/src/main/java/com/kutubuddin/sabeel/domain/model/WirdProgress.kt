package com.kutubuddin.sabeel.domain.model

/** A wird item resolved for display with today's derived progress folded in. */
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class WirdProgressItem(
    val dhikrKey: String,
    val displayName: LocalizedText,
    val arabicText: String,
    val transliteration: LocalizedText?,
    val target: Int,
    val countToday: Int,
    val position: Int
) {
    val isComplete: Boolean get() = countToday >= target
}

/** The whole wird's progress for today. */
@Immutable
data class WirdProgress(val items: ImmutableList<WirdProgressItem>) {
    val completed: Int get() = items.count { it.isComplete }
    val total: Int get() = items.size
    val allComplete: Boolean get() = items.isNotEmpty() && items.all { it.isComplete }
    /** Sum-based bar fill: each item contributes at most its target. */
    val countedSum: Int get() = items.sumOf { minOf(it.countToday, it.target) }
    val targetSum: Int get() = items.sumOf { it.target }
    
    val nextIncompleteItem: WirdProgressItem? get() = items.firstOrNull { !it.isComplete }
}

/**
 * The live, not-yet-saved round's contribution to an item's today count.
 * Adds the active delta (activeCount - startCount) when it belongs to this item.
 * This guarantees no double-counting with the saved DB sessions.
 */
fun liveContribution(itemKey: String, activeKey: String, activeCount: Int, startCount: Int): Int =
    if (itemKey == activeKey && activeCount > startCount) activeCount - startCount else 0
