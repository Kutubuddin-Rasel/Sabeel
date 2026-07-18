package com.kutubuddin.sabeel.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe 4-tab route definitions.
 *
 * Owns routes + icons only. Display labels are localized in [SabeelBottomBar]
 * via `LocalStrings` (keeps the tab model free of presentation language).
 */
sealed class SabeelTab(
    val graphRoute: String,
    val startRoute: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : SabeelTab(
        graphRoute = "home_graph",
        startRoute = "home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    object Count : SabeelTab(
        graphRoute = "count_graph",
        startRoute = "count",
        icon = Icons.Outlined.Adjust,
        selectedIcon = Icons.Filled.Adjust
    )
    object Dhikr : SabeelTab(
        graphRoute = "dhikr_graph",
        startRoute = "dhikr",
        icon = Icons.Outlined.MenuBook,
        selectedIcon = Icons.Filled.MenuBook
    )
    object Settings : SabeelTab(
        graphRoute = "settings_graph",
        startRoute = "settings",
        icon = Icons.Outlined.Tune,
        selectedIcon = Icons.Filled.Tune
    )

    companion object {
        val all = listOf(Home, Count, Dhikr, Settings)

        /**
         * True when [route] is one of the 4 top-level tab destinations.
         * Sub-screens pushed on top of a tab (e.g. "wird", "wird/edit") return
         * false — [SabeelNavHost] uses this to hide the bottom bar on those
         * screens.
         *
         * Compares only the path portion of [route]: Count's composable is
         * registered as "count?dhikrKey={dhikrKey}&target={target}", so a
         * literal equality check against `startRoute` ("count") always failed
         * and hid the bottom bar on the Count tab specifically, even though
         * it's a top-level tab like the other three.
         */
        fun isTopLevelRoute(route: String?): Boolean {
            if (route == null) return false
            val path = route.substringBefore("?")
            return all.any { it.startRoute == path || it.graphRoute == route }
        }
    }
}