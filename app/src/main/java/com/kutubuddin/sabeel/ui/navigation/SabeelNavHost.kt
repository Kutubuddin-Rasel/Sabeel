package com.kutubuddin.sabeel.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.ui.dhikr.DhikrLibraryScreen
import com.kutubuddin.sabeel.ui.home.HomeScreen
import com.kutubuddin.sabeel.ui.settings.SettingsScreen
import com.kutubuddin.sabeel.ui.tasbih.TasbihScreen
import com.kutubuddin.sabeel.ui.tasbih.TasbihViewModel
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.wird.WirdScreen

/**
 * Root navigation graph — 4-tab bottom-nav architecture.
 *
 * Key design decisions:
 *  - Count tab is the start destination (fastest path to prayer).
 *  - TasbihViewModel is Activity-scoped via hiltViewModel() at the NavHost level
 *    so both Count and Dhikr tabs share the same instance.
 *  - saveState/restoreState on each tab switch preserves per-tab scroll state.
 */
@Composable
fun SabeelNavHost(
    hapticEngine: HapticEngine,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Activity-scoped — shared between Count tab and Dhikr tab's "Count Now" action
    val tasbihViewModel: TasbihViewModel = hiltViewModel()

    Scaffold(
        modifier = modifier,
        containerColor = SabeelColors.Background,
        bottomBar = { SabeelBottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SabeelTab.Count.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SabeelTab.Home.route) {
                HomeScreen(
                    onResumeCounting = { navController.switchToCountTab() },
                    onOpenWird = { navController.navigate("wird") }
                )
            }

            composable("wird") {
                WirdScreen(
                    onCountItem = { key, target ->
                        tasbihViewModel.processIntent(com.kutubuddin.sabeel.ui.tasbih.TasbihIntent.SetDhikr(key, target))
                        navController.switchToCountTab()
                    },
                    onEdit = { navController.navigate("wird/edit") }
                )
            }

            composable(SabeelTab.Count.route) {
                TasbihScreen(
                    viewModel = tasbihViewModel,
                    hapticEngine = hapticEngine
                )
            }

            composable(SabeelTab.Dhikr.route) {
                DhikrLibraryScreen(
                    tasbihViewModel = tasbihViewModel,
                    onCountNow = { navController.switchToCountTab() }
                )
            }

            composable(SabeelTab.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

/**
 * Switch to the Count tab exactly as tapping it in the bottom bar does.
 *
 * The Resume (Home) and Count-Now (Dhikr) actions previously used a bare
 * `navigate(Count) { launchSingleTop; restoreState }` without the bottom bar's
 * `popUpTo(Count) { saveState }`. That mismatch left an inconsistent back stack
 * and swallowed the first tap — so it took two presses to actually land on Count.
 * Routing both entry points through this one helper keeps navigation coherent.
 */
private fun NavHostController.switchToCountTab() {
    navigate(SabeelTab.Count.route) {
        popUpTo(SabeelTab.Count.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}


