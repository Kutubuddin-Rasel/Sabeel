package com.kutubuddin.sabeel.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.runtime.LaunchedEffect
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.ui.dhikr.DhikrLibraryScreen
import com.kutubuddin.sabeel.ui.home.HomeScreen
import com.kutubuddin.sabeel.ui.settings.SettingsScreen
import com.kutubuddin.sabeel.ui.settings.SettingsViewModel
import com.kutubuddin.sabeel.ui.tasbih.SessionOrigin
import com.kutubuddin.sabeel.ui.tasbih.TasbihIntent
import com.kutubuddin.sabeel.ui.tasbih.TasbihScreen
import com.kutubuddin.sabeel.ui.tasbih.TasbihViewModel
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.wird.WirdEditScreen
import com.kutubuddin.sabeel.ui.wird.WirdScreen
import com.kutubuddin.sabeel.ui.wird.WirdViewModel

/** Sub-screen routes pushed on top of a tab — deliberately absent from [SabeelTab.all]. */
private object WirdRoutes {
    const val WIRD = "wird"
    const val WIRD_EDIT = "wird/edit"
}

/**
 * Root navigation graph — DIP: the top-level route graph is where ViewModels
 * are instantiated (`hiltViewModel()`) and handed down as data/callbacks; no
 * nested Composable in this app instantiates its own dependencies.
 *
 * [settingsViewModel]'s state is the single source of truth for `language`/
 * `showStreaks` across tabs — [TasbihScreen] now receives them as plain
 * params instead of re-deriving them from a second ViewModel instance.
 *
 * Home no longer wires an `onEditWird` callback: [WirdRoutes.WIRD_EDIT] is
 * reachable only from [WirdScreen]'s own `onEdit`, which is the single edit
 * entry point for the daily plan now that Home's `EditGoalChip` is gone.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SabeelNavHost(
    hapticEngine: HapticEngine,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val tasbihViewModel: TasbihViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val wirdViewModel: WirdViewModel = hiltViewModel()

    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val wirdUiState by wirdViewModel.state.collectAsStateWithLifecycle()
    val language = settingsState.language

    val backStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = SabeelTab.isTopLevelRoute(backStackEntry?.destination?.route)

    Scaffold(
        modifier = modifier,
        containerColor = SabeelColors.Background,
        bottomBar = {
            if (showBottomBar) {
                SabeelBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SabeelTab.Count.graphRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation(startDestination = SabeelTab.Home.startRoute, route = SabeelTab.Home.graphRoute) {
                composable(SabeelTab.Home.startRoute) {
                    HomeScreen(
                        onResumeCounting = { navController.switchToCountTab() },
                        onOpenWird = { navController.navigate(WirdRoutes.WIRD) }
                    )
                }

                composable(WirdRoutes.WIRD) {
                    WirdScreen(
                        onCountItem = { key, target ->
                            tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key, target, SessionOrigin.DAILY_GOAL))
                            // Destroy completed flow so it isn't saved in the tab backstack
                            navController.popBackStack(SabeelTab.Home.startRoute, inclusive = false)
                            navController.switchToCountTab()
                        },
                        onEdit = { navController.navigate(WirdRoutes.WIRD_EDIT) },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(WirdRoutes.WIRD_EDIT) {
                    WirdEditScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            navigation(startDestination = SabeelTab.Count.startRoute + "?dhikrKey={dhikrKey}&target={target}", route = SabeelTab.Count.graphRoute) {
                composable(
                    route = SabeelTab.Count.startRoute + "?dhikrKey={dhikrKey}&target={target}",
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "sabeel://count?dhikrKey={dhikrKey}&target={target}" }
                    ),
                    arguments = listOf(
                        navArgument("dhikrKey") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("target") { type = NavType.IntType; defaultValue = -1 }
                    )
                ) { backStackEntry ->
                    val tasbihState by tasbihViewModel.state.collectAsStateWithLifecycle()
                    val currentDhikrKey = tasbihState.currentDhikr.key
                    val isDailyGoalFinished = wirdUiState.progress.allComplete
                    val isDhikrInDailyGoal = wirdUiState.progress.items.any { it.dhikrKey == currentDhikrKey }

                    val nextWirdItem = if (settingsState.autoProgressWird)
                        wirdUiState.progress.nextIncompleteItem else null

                    // Self-Healing Deep Link Routing
                    val dhikrKeyArg = backStackEntry.arguments?.getString("dhikrKey")
                    val targetArg = backStackEntry.arguments?.getInt("target") ?: -1
                    LaunchedEffect(dhikrKeyArg, targetArg, wirdUiState.progress.items) {
                        if (dhikrKeyArg != null && targetArg > 0) {
                            val itemInGoal = wirdUiState.progress.items.find { it.dhikrKey == dhikrKeyArg }
                            if (itemInGoal != null && !itemInGoal.isComplete) {
                                tasbihViewModel.processIntent(
                                    TasbihIntent.SetDhikr(dhikrKeyArg, targetArg, SessionOrigin.DAILY_GOAL)
                                )
                            } else {
                                // Fallback to next incomplete item if the original is finished or removed
                                val nextWird = wirdUiState.progress.nextIncompleteItem
                                if (nextWird != null) {
                                    tasbihViewModel.processIntent(
                                        TasbihIntent.SetDhikr(nextWird.dhikrKey, nextWird.target, SessionOrigin.DAILY_GOAL)
                                    )
                                }
                            }
                            // Clear arguments so it doesn't re-trigger on orientation change
                            backStackEntry.arguments?.remove("dhikrKey")
                            backStackEntry.arguments?.remove("target")
                        }
                    }

                    TasbihScreen(
                        viewModel = tasbihViewModel,
                        hapticEngine = hapticEngine,
                        language = language,
                        showStreaks = settingsState.showStreaks,
                        isDailyGoalFinished = isDailyGoalFinished,
                        isDhikrInDailyGoal = isDhikrInDailyGoal,
                        nextWirdItemName = nextWirdItem?.displayName?.get(language),
                        onContinueWird = nextWirdItem?.let { item ->
                            {
                                tasbihViewModel.processIntent(
                                    TasbihIntent.SetDhikr(item.dhikrKey, item.target, SessionOrigin.DAILY_GOAL)
                                )
                            }
                        },
                        onNavigateHome = {
                            navController.navigate(SabeelTab.Home.graphRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateLibrary = {
                            navController.navigate(SabeelTab.Dhikr.graphRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

            navigation(startDestination = SabeelTab.Dhikr.startRoute, route = SabeelTab.Dhikr.graphRoute) {
                composable(SabeelTab.Dhikr.startRoute) {
                    DhikrLibraryScreen(
                        tasbihViewModel = tasbihViewModel,
                        onCountNow = { navController.switchToCountTab() }
                    )
                }
            }

            navigation(startDestination = SabeelTab.Settings.startRoute, route = SabeelTab.Settings.graphRoute) {
                composable(SabeelTab.Settings.startRoute) {
                    SettingsScreen()
                }
            }
        }
    }
}

private fun NavHostController.switchToCountTab() {
    navigate(SabeelTab.Count.graphRoute) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}