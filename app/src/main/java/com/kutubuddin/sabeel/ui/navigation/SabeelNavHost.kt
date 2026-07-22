package com.kutubuddin.sabeel.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.kutubuddin.sabeel.ui.theme.SabeelMotion

/** Sub-screen routes pushed on top of a tab — deliberately absent from [SabeelTab.all]. */
private object WirdRoutes {
    const val WIRD = "wird"
    const val WIRD_EDIT = "wird/edit"
}

// ── Sabeel Motion System ──────────────────────────────────────────────────
//
// Based on Material Design 3 "Emphasized" motion specs:
// • Enter (screen arrives):  400–500ms, LinearOutSlowIn (decelerates into place)
// • Exit  (screen leaves):   200ms,      FastOutLinearIn (accelerates away fast)
// • Tab cross-fade:          300ms,      linear fade (no spatial movement)
// • Sheet slide:             420ms enter / 260ms exit — sheet travels only
//                             55% of screen height so it doesn't feel like
//                             a full-page wipe. Decelerate easing makes it
//                             feel like it "lands" rather than slams in.
//
// Why easing matters MORE than duration:
// LinearOutSlowIn starts fast and decelerates into the final position,
// which matches how physical objects behave (momentum → rest). Without
// it, tween() uses linear timing — constant speed from start to finish,
// which always reads as "snappy" or "mechanical" regardless of duration.

// M3 "Emphasized Decelerate" — for elements entering the screen.
// Cubic: fast start, decelerates dramatically into rest. The "soft landing".
private val EaseOutCubic = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

// M3 "Emphasized Accelerate" — for elements leaving the screen.
// Cubic: starts slow, accelerates away. Clears the frame decisively.
private val EaseInCubic = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

// Sheet sub-routes (Wird, WirdEdit, AsmaUlHusna): slide-up from partial
// Longer enter gives the sheet time to decelerate gracefully into place
// Tab → Tab timing is now owned by SabeelMotion.Duration.TabEnter/TabExit.
private const val SHEET_ENTER_MS = 420
private const val SHEET_EXIT_MS  = 260

// Bottom bar — matches sheet timing so bar and sheet move in sync
private const val BAR_ENTER_MS = 380
private const val BAR_EXIT_MS  = 220

// Backwards compat alias — keeps any future code readable
private const val TRANSITION_ENTER_MS = SHEET_ENTER_MS
private const val TRANSITION_EXIT_MS  = SHEET_EXIT_MS

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
 *
 * ## Transition design
 * - **Tab → Tab:** cross-fade (300ms in / 220ms out). No slide — tabs are
 *   spatial peers at the same stack depth; a directional slide would imply
 *   a hierarchy that doesn't exist.
 * - **Tab → sub-route (Wird, WirdEdit, AsmaUlHusna):** slide up (sheet
 *   semantics) + fade in. Back navigation slides down, mirroring the push.
 * - **Bottom bar:** animated slide+fade so it doesn't hard-cut on sub-routes.
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
    // OPT-05: WirdViewModel is NOT instantiated here at root scope.
    // It is scoped to the home_graph NavBackStackEntry inside the navigation block below.
    // This stops 3 Room flows (wird items, progress, sessions) from running when the
    // user is on the Count or Settings tabs — they only run while home_graph is on stack.

    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val language = settingsState.language

    val backStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = SabeelTab.isTopLevelRoute(backStackEntry?.destination?.route)

    Scaffold(
        modifier = modifier,
        containerColor = SabeelColors.Background,
        bottomBar = {
            // Animated show/hide so the bar doesn't hard-cut when navigating
            // into a sub-route (Wird, WirdEdit, AsmaUlHusna). Slide down on
            // exit matches the sheet-sliding-up semantic of those sub-screens.
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    animationSpec = tween(BAR_ENTER_MS, easing = EaseOutCubic)
                ) { it } + fadeIn(tween(BAR_ENTER_MS, easing = EaseOutCubic)),
                exit  = slideOutVertically(
                    animationSpec = tween(BAR_EXIT_MS, easing = EaseInCubic)
                ) { it } + fadeOut(tween(BAR_EXIT_MS, easing = EaseInCubic))
            ) {
                SabeelBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SabeelTab.Count.graphRoute,
            modifier = Modifier.padding(innerPadding),
            // ── Tab → Tab transition (Apple-parity) ────────────────────────────────
            // Scale + fade with M3 Emphasized easing (defined in SabeelMotion).
            //   Enter: 0.96→1.0 scale + fade, EmphasizedDecelerate — screen "materialises".
            //   Exit:  1.0→0.98 scale + fade, EmphasizedAccelerate — screen "dissolves" fast.
            // popEnter/popExit mirror the same tokens for back-gesture symmetry.
            enterTransition    = { SabeelMotion.tabEnter() },
            exitTransition     = { SabeelMotion.tabExit() },
            popEnterTransition = { SabeelMotion.tabEnter() },
            popExitTransition  = { SabeelMotion.tabExit() }
        ) {
            navigation(startDestination = SabeelTab.Home.startRoute, route = SabeelTab.Home.graphRoute) {
                composable(SabeelTab.Home.startRoute) {
                    // OPT-05: WirdViewModel scoped to the home_graph NavBackStackEntry.
                    // hiltViewModel(backStackEntry) where backStackEntry is the graph-level
                    // entry means this ViewModel is created when home_graph is entered and
                    // destroyed when home_graph is popped — i.e. only while Home tab is alive.
                    val homeGraphEntry = remember(it) {
                        navController.getBackStackEntry(SabeelTab.Home.graphRoute)
                    }
                    val wirdViewModel: WirdViewModel = hiltViewModel(homeGraphEntry)
                    val wirdUiState by wirdViewModel.state.collectAsStateWithLifecycle()

                    HomeScreen(
                        onResumeCounting = { key, target ->
                            if (key != null && target != null) {
                                tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key, target, SessionOrigin.DAILY_GOAL))
                            }
                            navController.switchToCountTab()
                        },
                        onResumeSession = { key ->
                            tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key, origin = SessionOrigin.HOME))
                            navController.switchToCountTab()
                        },
                        onOpenWird = { navController.navigate(WirdRoutes.WIRD) },
                        showTooltip = !settingsState.hasSeenHomeTooltip,
                        onTooltipDismiss = {
                            settingsViewModel.processIntent(com.kutubuddin.sabeel.ui.settings.SettingsIntent.SetHasSeenHomeTooltip(true))
                        }
                    )
                }

                // Sub-route: slide up from bottom like a modal sheet.
                // • Enter: 55% of screen height (not full — full-screen wipe at
                //   300ms reads as "slamming in"). EaseOutCubic decelerates into
                //   position — the element "lands" rather than stopping abruptly.
                // • Exit:  fade only (no slide out) — background content stays
                //   still so the user's eye stays anchored.
                // • Pop:   reverse of enter (slide down, EaseInCubic).
                composable(
                    route = WirdRoutes.WIRD,
                    enterTransition = {
                        slideInVertically(
                            animationSpec = tween(SHEET_ENTER_MS, easing = EaseOutCubic)
                        ) { (it * 0.55f).toInt() } +
                        fadeIn(tween(SHEET_ENTER_MS, easing = EaseOutCubic))
                    },
                    exitTransition = {
                        // SMOOTH-06: iOS card-dismiss — keep background fully opaque under
                        // incoming modal sheets (like WIRD_EDIT) to prevent dark flashes.
                        ExitTransition.None
                    },
                    popEnterTransition = {
                        // SMOOTH-06: iOS card-dismiss — background is always rendered under the
                        // sheet; only the sheet exits. No enter animation prevents the dark flash
                        // that occurred when the parent faded in while the sheet slid down.
                        EnterTransition.None
                    },
                    popExitTransition = {
                        slideOutVertically(
                            animationSpec = tween(SHEET_EXIT_MS, easing = EaseInCubic)
                        ) { (it * 0.55f).toInt() } +
                        fadeOut(tween(SHEET_EXIT_MS, easing = EaseInCubic))
                    }
                ) {
                    WirdScreen(
                        onCountItem = { key, target ->
                            tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key, target, SessionOrigin.DAILY_GOAL))
                            navController.popBackStack(SabeelTab.Home.startRoute, inclusive = false)
                            navController.switchToCountTab()
                        },
                        onEdit = { navController.navigate(WirdRoutes.WIRD_EDIT) },
                        onBack = { navController.popBackStack() },
                        showTooltip = !settingsState.hasSeenWirdTooltip,
                        onTooltipDismiss = {
                            settingsViewModel.processIntent(com.kutubuddin.sabeel.ui.settings.SettingsIntent.SetHasSeenWirdTooltip(true))
                        }
                    )
                }

                composable(
                    route = WirdRoutes.WIRD_EDIT,
                    enterTransition = {
                        slideInVertically(
                            animationSpec = tween(SHEET_ENTER_MS, easing = EaseOutCubic)
                        ) { (it * 0.55f).toInt() } +
                        fadeIn(tween(SHEET_ENTER_MS, easing = EaseOutCubic))
                    },
                    exitTransition = {
                        fadeOut(tween(SHEET_EXIT_MS, easing = EaseInCubic))
                    },
                    popEnterTransition = {
                        // SMOOTH-06: see Wird route above — same iOS card-dismiss pattern.
                        EnterTransition.None
                    },
                    popExitTransition = {
                        slideOutVertically(
                            animationSpec = tween(SHEET_EXIT_MS, easing = EaseInCubic)
                        ) { (it * 0.55f).toInt() } +
                        fadeOut(tween(SHEET_EXIT_MS, easing = EaseInCubic))
                    }
                ) {
                    WirdEditScreen(
                        onBack = { navController.popBackStack() },
                        showTooltip = !settingsState.hasSeenWirdPickerTooltip,
                        onTooltipDismiss = {
                            settingsViewModel.processIntent(com.kutubuddin.sabeel.ui.settings.SettingsIntent.SetHasSeenWirdPickerTooltip(true))
                        }
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
                    // OPT-05: WirdViewModel no longer referenced here.
                    // Wird progress data flows through HomeViewModel.state.wird (WirdSummary),
                    // which is already alive for the full app session. WirdViewModel's 3 Room
                    // flows are now scoped to home_graph — they stop when the user is on this tab.
                    val homeViewModel: com.kutubuddin.sabeel.ui.home.HomeViewModel = hiltViewModel()
                    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
                    val tasbihState by tasbihViewModel.state.collectAsStateWithLifecycle()
                    val currentDhikrKey = tasbihState.currentDhikr.key

                    val isDailyGoalFinished = homeState.wird.allComplete
                    val isDhikrInDailyGoal = homeState.wird.wirdItemKeys.contains(currentDhikrKey)
                    val nextWirdItem = if (settingsState.autoProgressWird &&
                        homeState.wird.nextItemKey != null && homeState.wird.nextItemTarget != null
                    ) {
                        object {
                            val dhikrKey = homeState.wird.nextItemKey!!
                            val target = homeState.wird.nextItemTarget!!
                            val displayName = homeState.wird.nextItemName
                        }
                    } else null

                    // Self-Healing Deep Link Routing
                    val dhikrKeyArg = backStackEntry.arguments?.getString("dhikrKey")
                    val targetArg = backStackEntry.arguments?.getInt("target") ?: -1
                    LaunchedEffect(dhikrKeyArg, targetArg, homeState.wird.wirdItemKeys) {
                        if (dhikrKeyArg != null) {
                            if (targetArg > 0) {
                                val isItemInGoal = homeState.wird.wirdItemKeys.contains(dhikrKeyArg)
                                if (isItemInGoal) {
                                    tasbihViewModel.processIntent(
                                        TasbihIntent.SetDhikr(dhikrKeyArg, targetArg, SessionOrigin.DAILY_GOAL)
                                    )
                                } else {
                                    val nextKey = homeState.wird.nextItemKey
                                    val nextTarget = homeState.wird.nextItemTarget
                                    if (nextKey != null && nextTarget != null) {
                                        tasbihViewModel.processIntent(
                                            TasbihIntent.SetDhikr(nextKey, nextTarget, SessionOrigin.DAILY_GOAL)
                                        )
                                    }
                                }
                            } else {
                                tasbihViewModel.processIntent(
                                    TasbihIntent.SetDhikr(dhikrKeyArg)
                                )
                            }
                            backStackEntry.arguments?.remove("dhikrKey")
                            backStackEntry.arguments?.remove("target")
                        }
                    }

                    TasbihScreen(
                        viewModel = tasbihViewModel,
                        hapticEngine = hapticEngine,
                        language = language,
                        showTransliteration = settingsState.translitEnabled,
                        isDailyGoalFinished = isDailyGoalFinished,
                        isDhikrInDailyGoal = isDhikrInDailyGoal,
                        nextWirdItemName = nextWirdItem?.displayName?.get(language),
                        onContinueWird = {
                            if (settingsState.autoProgressWird) {
                                val currentHomeState = homeViewModel.state.value
                                val nextKey = currentHomeState.wird.nextItemKey
                                val nextTarget = currentHomeState.wird.nextItemTarget
                                if (nextKey != null && nextTarget != null) {
                                    tasbihViewModel.processIntent(
                                        TasbihIntent.SetDhikr(nextKey, nextTarget, SessionOrigin.DAILY_GOAL, preserveCount = true)
                                    )
                                }
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
                        },
                        showTooltip = !settingsState.hasSeenTasbihTooltip,
                        onTooltipDismiss = {
                            settingsViewModel.processIntent(com.kutubuddin.sabeel.ui.settings.SettingsIntent.SetHasSeenTasbihTooltip(true))
                        }
                    )
                }
            }

            navigation(startDestination = SabeelTab.Dhikr.startRoute, route = SabeelTab.Dhikr.graphRoute) {
                composable(SabeelTab.Dhikr.startRoute) {
                    DhikrLibraryScreen(
                        tasbihViewModel = tasbihViewModel,
                        onCountNow = { navController.switchToCountTab() },
                        onOpenGallery = { navController.navigate("asma_ul_husna_gallery") },
                        showTooltip = !settingsState.hasSeenLibraryTooltip,
                        onTooltipDismiss = {
                            settingsViewModel.processIntent(com.kutubuddin.sabeel.ui.settings.SettingsIntent.SetHasSeenLibraryTooltip(true))
                        }
                    )
                }
                // AsmaUlHusna is a sub-route of Dhikr — same sheet semantics as Wird.
                composable(
                    route = "asma_ul_husna_gallery",
                    enterTransition = {
                        slideInVertically(
                            animationSpec = tween(SHEET_ENTER_MS, easing = EaseOutCubic)
                        ) { (it * 0.55f).toInt() } +
                        fadeIn(tween(SHEET_ENTER_MS, easing = EaseOutCubic))
                    },
                    exitTransition = {
                        fadeOut(tween(SHEET_EXIT_MS, easing = EaseInCubic))
                    },
                    popEnterTransition = {
                        // SMOOTH-06: same iOS card-dismiss pattern for AsmaUlHusna sheet.
                        EnterTransition.None
                    },
                    popExitTransition = {
                        slideOutVertically(
                            animationSpec = tween(SHEET_EXIT_MS, easing = EaseInCubic)
                        ) { (it * 0.55f).toInt() } +
                        fadeOut(tween(SHEET_EXIT_MS, easing = EaseInCubic))
                    }
                ) {
                    com.kutubuddin.sabeel.ui.dhikr.AsmaUlHusnaGalleryScreen(
                        language = language,
                        showTransliteration = settingsState.translitEnabled,
                        onBack = { navController.popBackStack() },
                        onCountNow = { key ->
                            tasbihViewModel.processIntent(TasbihIntent.SetDhikr(key))
                            navController.switchToCountTab()
                        }
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