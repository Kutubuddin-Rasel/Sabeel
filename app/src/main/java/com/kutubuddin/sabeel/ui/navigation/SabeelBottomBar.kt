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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@Composable
fun SabeelBottomBar(
    navController: NavHostController = rememberNavController()
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar(
        containerColor = SabeelColors.Surface,
        tonalElevation = 0.dp
    ) {
        SabeelTab.all.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            popUpTo(SabeelTab.Count.route) {
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
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label, fontSize = 11.sp) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SabeelColors.GoldPrimary,
                    selectedTextColor = SabeelColors.GoldPrimary,
                    unselectedIconColor = SabeelColors.TextSecondary,
                    unselectedTextColor = SabeelColors.TextSecondary,
                    indicatorColor = SabeelColors.GoldSurface
                )
            )
        }
    }
}
