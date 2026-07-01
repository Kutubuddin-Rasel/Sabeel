package com.kutubuddin.sabeel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import com.kutubuddin.sabeel.domain.repository.SettingsRepository
import com.kutubuddin.sabeel.service.PocketModeService
import com.kutubuddin.sabeel.ui.navigation.SabeelNavHost
import com.kutubuddin.sabeel.ui.tasbih.TasbihSideEffect
import com.kutubuddin.sabeel.ui.tasbih.TasbihViewModel
import com.kutubuddin.sabeel.ui.theme.SabeelTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single-Activity host for the Sabeel app.
 *
 * Responsibilities:
 * 1. Hosts the SabeelNavHost (DhikrSelection → Counting)
 * 2. Consumes service lifecycle side-effects from TasbihViewModel
 *    to start/stop PocketModeService as a foreground service
 *
 * SRP: Activity only handles Android lifecycle and side-effect plumbing.
 *      All business logic lives in TasbihViewModel.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var hapticEngine: HapticEngine

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: TasbihViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeServiceSideEffects()

        setContent {
            // Drive the theme from the SAVED setting, not isSystemInDarkTheme(),
            // so the Settings Dark/Light toggle is a real, instant choice.
            val theme by settingsRepository.theme.collectAsState(initial = "dark")
            SabeelTheme(darkTheme = theme != "light") {
                SabeelNavHost(
                    hapticEngine = hapticEngine,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    /**
     * Listens for service lifecycle side-effects and starts/stops
     * PocketModeService accordingly. This runs on the STARTED lifecycle
     * state to avoid operating on a stopped activity.
     */
    private fun observeServiceSideEffects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is TasbihSideEffect.StartPocketModeService -> {
                            val intent = Intent(this@MainActivity, PocketModeService::class.java)
                            startForegroundService(intent)
                        }
                        is TasbihSideEffect.StopPocketModeService -> {
                            val intent = Intent(this@MainActivity, PocketModeService::class.java)
                            stopService(intent)
                        }
                        else -> { /* All other effects consumed in TasbihScreen */ }
                    }
                }
            }
        }
    }
}