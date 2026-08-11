package com.kitalonlabs.sabeel.ui.wird

import com.kitalonlabs.sabeel.domain.model.DhikrItem
import com.kitalonlabs.sabeel.domain.model.WirdItem

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.persistentListOf

/** A wird row resolved for editing (plan + display name). */
@Immutable
data class WirdEditRow(
    val dhikrKey: String,
    val displayName: String,
    val target: Int,
    val position: Int
)

@Immutable
data class WirdEditState(
    val rows: ImmutableList<WirdEditRow> = persistentListOf(),
    val pickable: ImmutableList<DhikrItem> = persistentListOf(), // non-SmartFlow, not already added
    val language: String = "en"
)

/**
 * Pure projection of the plan + catalog into the editable view state.
 *
 * Framework-free so it can be unit-tested without any coroutine/Android
 * machinery (mirrors [com.kitalonlabs.sabeel.domain.model.liveContribution] and
 * `applyTargetOverride`). Two invariants live here and are locked by tests:
 *  - Rows take their [WirdEditRow.target]/[WirdEditRow.position] from the PLAN
 *    and their display name from the CATALOG; a plan key absent from the catalog
 *    is dropped (a dangling key can't be edited).
 *  - `pickable` excludes Smart-Flow entries and anything already in the plan.
 *    Excluding Smart-Flow is structural, not cosmetic: a single-int target
 *    override has no clean meaning for a multi-step sequence, so a wird item must
 *    never be able to launch one (Task 7 finding).
 */
fun resolveWirdEditState(
    plan: List<WirdItem>,
    catalog: List<DhikrItem>,
    language: String
): WirdEditState {
    val byKey = catalog.associateBy { it.key }
    val rows = plan.mapNotNull { item ->
        byKey[item.dhikrKey]?.let {
            WirdEditRow(item.dhikrKey, it.displayName.get(language), item.target, item.position)
        }
    }
    val addedKeys = plan.map { it.dhikrKey }.toSet()
    val pickable = catalog.filter { !it.isSmartFlow && it.key !in addedKeys }.toImmutableList()
    return WirdEditState(rows = rows.toImmutableList(), pickable = pickable, language = language)
}

/**
 * Swap [key] with its neighbor (previous if [up], else next).
 *
 * Returns the reordered key list, or null when the move is a no-op — the key is
 * absent, or it is already at the edge in the requested direction. Callers skip
 * the persist when null, so an edge tap costs nothing.
 */
fun swapAdjacent(order: ImmutableList<String>, key: String, up: Boolean): ImmutableList<String>? {
    val i = order.indexOf(key)
    if (i < 0) return null
    val j = if (up) i - 1 else i + 1
    if (j !in order.indices) return null
    val out = order.toMutableList()
    out[i] = out[j].also { out[j] = out[i] }
    return out.toImmutableList()
}
