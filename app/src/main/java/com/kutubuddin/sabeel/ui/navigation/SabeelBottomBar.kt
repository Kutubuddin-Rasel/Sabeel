package com.kutubuddin.sabeel.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.UiStrings
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@Composable
fun SabeelBottomBar(
    navController: NavHostController = rememberNavController()
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination
    val strings = LocalStrings.current

    NavigationBar(
        containerColor = SabeelColors.Surface,
        tonalElevation = 0.dp
    ) {
        SabeelTab.all.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.route == tab.graphRoute } == true
            val label = strings.labelFor(tab)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) {
                        // Double-Tap to Root: pop back to the root of the selected graph
                        navController.popBackStack(route = tab.startRoute, inclusive = false)
                    } else {
                        navController.navigate(tab.graphRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.icon,
                        contentDescription = label
                    )
                },
                label = { Text(label, fontSize = 11.sp) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SabeelColors.AccentTeal,
                    selectedTextColor = SabeelColors.AccentTeal,
                    unselectedIconColor = SabeelColors.TextSecondary,
                    unselectedTextColor = SabeelColors.TextSecondary,
                    indicatorColor = SabeelColors.AccentTealSurface
                )
            )
        }
    }
}

/** Localized display label for a tab. Lives here (navigation → i18n) so the
 *  i18n layer never has to know about navigation types. */
private fun UiStrings.labelFor(tab: SabeelTab): String = when (tab) {
    SabeelTab.Home -> navHome
    SabeelTab.Count -> navCount
    SabeelTab.Dhikr -> navDhikr
    SabeelTab.Settings -> navSettings
}
