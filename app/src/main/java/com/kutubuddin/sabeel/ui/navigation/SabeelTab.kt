package com.kutubuddin.sabeel.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe 4-tab route definitions.
 *
 * Owns routes + icons only. Display labels are localized in [SabeelBottomBar]
 * via `LocalStrings` (keeps the tab model free of presentation language).
 */
sealed class SabeelTab(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : SabeelTab(
        route = "home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    object Count : SabeelTab(
        route = "count",
        icon = Icons.Outlined.Adjust,
        selectedIcon = Icons.Filled.Adjust
    )
    object Dhikr : SabeelTab(
        route = "dhikr",
        icon = Icons.Outlined.MenuBook,
        selectedIcon = Icons.Filled.MenuBook
    )
    object Settings : SabeelTab(
        route = "settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )

    companion object {
        val all = listOf(Home, Count, Dhikr, Settings)
    }
}
