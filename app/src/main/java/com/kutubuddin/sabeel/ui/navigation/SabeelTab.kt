package com.kutubuddin.sabeel.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Type-safe 4-tab route definitions. */
sealed class SabeelTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : SabeelTab(
        route = "home",
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    object Count : SabeelTab(
        route = "count",
        label = "Count",
        icon = Icons.Outlined.AutoMode,
        selectedIcon = Icons.Filled.AutoMode
    )
    object Dhikr : SabeelTab(
        route = "dhikr",
        label = "Dhikr",
        icon = Icons.Outlined.MenuBook,
        selectedIcon = Icons.Filled.MenuBook
    )
    object Settings : SabeelTab(
        route = "settings",
        label = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )

    companion object {
        val all = listOf(Home, Count, Dhikr, Settings)
    }
}
