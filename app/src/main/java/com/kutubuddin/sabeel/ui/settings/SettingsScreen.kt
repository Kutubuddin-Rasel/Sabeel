package com.kutubuddin.sabeel.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.collections.immutable.persistentListOf
import com.kutubuddin.sabeel.ui.components.CollapsibleSectionHeader
import com.kutubuddin.sabeel.ui.components.SabeelSectionHeader
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.settings.components.*
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Route-level wrapper (SRP/DIP): sole owner of [viewModel] injection and state collection. */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(state = state, onIntent = viewModel::processIntent)
}

/**
 * Pure, stateless settings rendering (SRP): a function of [state] only,
 * emitting every change via [onIntent]. No ViewModel reference.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    val strings = LocalStrings.current
    var showAdvanced by rememberSaveable { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Appearance ---
        item { SettingsHeader(strings.settingsAppearance) }
        item {
            SabeelSettingsCard {
                SettingsBaseRow(
                    title = strings.settingsTheme,
                    showDivider = false
                )
                SettingsSegmentRow(
                    options = persistentListOf(
                        SegmentOption("dark", strings.settingsThemeDark, Icons.Outlined.DarkMode),
                        SegmentOption("light", strings.settingsThemeLight, Icons.Outlined.LightMode)
                    ),
                    selected = state.theme,
                    onSelect = { onIntent(SettingsIntent.SetTheme(it)) }
                )
            }
        }

        item { Spacer(Modifier.height(14.dp)) }

        // --- Language & Text ---
        item { SettingsHeader(strings.settingsLanguageText) }
        item {
            SabeelSettingsCard {
                SettingsBaseRow(
                    title = strings.settingsLanguage,
                    icon = Icons.Default.Language,
                    showDivider = false
                )
                SettingsSegmentRow(
                    options = persistentListOf(
                        SegmentOption("en", "English"),
                        SegmentOption("bn", "বাংলা")
                    ),
                    selected = state.language,
                    onSelect = { onIntent(SettingsIntent.SetLanguage(it)) }
                )
                SettingsBaseRow(
                    title = strings.settingsTranslit,
                    description = strings.settingsTranslitDesc,
                    icon = Icons.Default.ViewStream,
                    action = {
                        SabeelSwitch(
                            checked = state.translitEnabled,
                            onCheckedChange = { 
                                haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                                onIntent(SettingsIntent.SetTranslit(it)) 
                            }
                        )
                    }
                )
            }
        }

        item { Spacer(Modifier.height(14.dp)) }

        // --- Counting Behaviour ---
        item { SettingsHeader(strings.settingsCountingBehaviour) }
        item {
            SabeelSettingsCard {
                SettingsBaseRow(
                    title = strings.settingsHaptics,
                    icon = Icons.Default.Vibration,
                    showDivider = false
                )
                SettingsSegmentRow(
                    options = persistentListOf(
                        SegmentOption("off", strings.settingsHapticOff),
                        SegmentOption("light", strings.settingsHapticLight),
                        SegmentOption("medium", strings.settingsHapticMedium),
                        SegmentOption("strong", strings.settingsHapticStrong)
                    ),
                    selected = state.hapticsLevel,
                    onSelect = { level ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onIntent(SettingsIntent.SetHaptics(level))
                    }
                )


                SettingsBaseRow(
                    title = strings.settingsAdvanced,
                    icon = Icons.Default.Tune,
                    onClick = { showAdvanced = !showAdvanced },
                    action = {
                        val rotation by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = if (showAdvanced) 180f else 0f,
                            label = "advanced_chevron_rotation"
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = rotation },
                            tint = SabeelColors.TextHint
                        )
                    }
                )
                AnimatedVisibility(visible = showAdvanced) {
                    Column {
                        SettingsBaseRow(
                            title = strings.settingsAutoProgressWird,
                            description = strings.settingsAutoProgressWirdDesc,
                            action = {
                                SabeelSwitch(
                                    checked = state.autoProgressWird,
                                    onCheckedChange = { 
                                        haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                                        onIntent(SettingsIntent.SetAutoProgressWird(it)) 
                                    }
                                )
                            }
                        )
                        SettingsBaseRow(
                            title = strings.settingsSmartFlow,
                            description = strings.settingsSmartFlowDesc,
                            action = {
                                SabeelSwitch(
                                    checked = state.isSmartFlowEnabled,
                                    onCheckedChange = { 
                                        haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                                        onIntent(SettingsIntent.SetSmartFlowEnabled(it)) 
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(14.dp)) }

        // --- Daily Reminders ---
        item { SettingsHeader(strings.settingsDailyReminders) }
        item {
            SabeelSettingsCard {
                SettingsBaseRow(
                    title = strings.settingsDailyReminders,
                    description = strings.settingsDailyRemindersDesc,
                    icon = Icons.Default.Notifications,
                    showDivider = false,
                    action = {
                        SabeelSwitch(
                            checked = state.dailyReminderEnabled,
                            onCheckedChange = { 
                                haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                                onIntent(SettingsIntent.SetDailyReminderEnabled(it)) 
                            }
                        )
                    }
                )
                if (state.dailyReminderEnabled) {
                    val parts = state.dailyReminderTime.split(":")
                    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
                    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30
                    val formatter = remember { DateTimeFormatter.ofPattern("h:mm a") }
                    val displayTime = try {
                        LocalTime.of(hour, minute).format(formatter)
                    } catch (e: Exception) { state.dailyReminderTime }

                    var showTimePicker by rememberSaveable { mutableStateOf(false) }
                    val timePickerState = rememberTimePickerState(
                        initialHour = hour, initialMinute = minute
                    )

                    SettingsBaseRow(
                        title = strings.settingsReminderTime,
                        icon = Icons.Default.AccessTime,
                        onClick = { showTimePicker = true },
                        action = {
                            Text(
                                text = displayTime,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = SabeelColors.AccentTeal
                            )
                        }
                    )

                    if (showTimePicker) {
                        BasicAlertDialog(onDismissRequest = { showTimePicker = false }) {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SabeelColors.SurfaceElevated)
                                    .padding(horizontal = 24.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = strings.settingsReminderTime,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SabeelColors.TextPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )
                                TimePicker(
                                    state = timePickerState,
                                    colors = TimePickerDefaults.colors(
                                        clockDialColor = SabeelColors.Surface,
                                        selectorColor = SabeelColors.AccentTeal,
                                        clockDialSelectedContentColor = SabeelColors.Background,
                                        clockDialUnselectedContentColor = SabeelColors.TextPrimary,
                                        periodSelectorBorderColor = SabeelColors.BorderIdle,
                                        periodSelectorSelectedContainerColor = SabeelColors.AccentTeal,
                                        periodSelectorUnselectedContainerColor = SabeelColors.Surface,
                                        periodSelectorSelectedContentColor = SabeelColors.Background,
                                        periodSelectorUnselectedContentColor = SabeelColors.TextSecondary,
                                        timeSelectorSelectedContainerColor = SabeelColors.AccentTeal.copy(alpha = 0.2f),
                                        timeSelectorUnselectedContainerColor = SabeelColors.Surface,
                                        timeSelectorSelectedContentColor = SabeelColors.AccentTeal,
                                        timeSelectorUnselectedContentColor = SabeelColors.TextPrimary,
                                    )
                                )
                                Spacer(Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showTimePicker = false }) {
                                        Text(strings.a11yDismiss, color = SabeelColors.TextSecondary)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    TextButton(onClick = {
                                        val timeString = String.format(
                                            "%02d:%02d",
                                            timePickerState.hour,
                                            timePickerState.minute
                                        )
                                        onIntent(SettingsIntent.SetDailyReminderTime(timeString))
                                        showTimePicker = false
                                    }) {
                                        Text(strings.wirdDone, color = SabeelColors.AccentTeal)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(14.dp)) }

        // --- About ---
        item { SettingsHeader(strings.settingsAbout) }
        item {
            SabeelSettingsCard {
                SettingsBaseRow(
                    title = strings.settingsVersion,
                    action = {
                        Text(
                            text = "1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SabeelColors.TextSecondary
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader(text: String) =
    SabeelSectionHeader(
        text = text,
        letterSpacing = 1.8.dp,
        modifier = Modifier.padding(bottom = 4.dp)
    )


