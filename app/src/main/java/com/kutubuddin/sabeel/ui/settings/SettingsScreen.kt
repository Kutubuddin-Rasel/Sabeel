package com.kutubuddin.sabeel.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.components.CollapsibleSectionHeader
import com.kutubuddin.sabeel.ui.components.SabeelSectionHeader
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SabeelColors.Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { SettingsHeader(strings.settingsAppearance) }

        item {
            SettingsSegmentRow(
                label = strings.settingsTheme,
                options = listOf("dark" to strings.settingsThemeDark, "light" to strings.settingsThemeLight),
                selected = state.theme,
                onSelect = { onIntent(SettingsIntent.SetTheme(it)) }
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
        item { SettingsHeader(strings.settingsLanguageText) }

        item {
            SettingsSegmentRow(
                label = strings.settingsLanguage,
                options = listOf("en" to "English", "ur" to "اردو", "bn" to "বাংলা"),
                selected = state.language,
                onSelect = { onIntent(SettingsIntent.SetLanguage(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsTranslit,
                description = strings.settingsTranslitDesc,
                checked = state.translitEnabled,
                onCheckedChange = { onIntent(SettingsIntent.SetTranslit(it)) }
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
        item { SettingsHeader(strings.settingsCountingBehaviour) }

        item {
            SettingsSegmentRow(
                label = strings.settingsHaptics,
                options = listOf(
                    "off" to strings.settingsHapticOff,
                    "light" to strings.settingsHapticLight,
                    "medium" to strings.settingsHapticMedium,
                    "strong" to strings.settingsHapticStrong
                ),
                selected = state.hapticsLevel,
                onSelect = { onIntent(SettingsIntent.SetHaptics(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsLeftHanded,
                description = strings.settingsLeftHandedDesc,
                checked = state.leftHanded,
                onCheckedChange = { onIntent(SettingsIntent.SetLeftHanded(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsSound,
                description = strings.settingsSoundDesc,
                checked = state.soundEnabled,
                onCheckedChange = { onIntent(SettingsIntent.SetSoundOn(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsAutoReset,
                description = strings.settingsAutoResetDesc,
                checked = state.autoReset,
                onCheckedChange = { onIntent(SettingsIntent.SetAutoReset(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsShowStreaks,
                description = if (state.showStreaks) strings.settingsShowStreaksDescOn else strings.settingsShowStreaksDescOff,
                checked = state.showStreaks,
                onCheckedChange = { onIntent(SettingsIntent.SetShowStreaks(it)) }
            )
        }
        item {
            CollapsibleSectionHeader(
                text = strings.settingsAdvanced,
                isExpanded = showAdvanced,
                onClick = { showAdvanced = !showAdvanced }
            )
        }
        item {
            AnimatedVisibility(visible = showAdvanced) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsToggleRow(
                        label = strings.settingsAutoProgressWird,
                        description = strings.settingsAutoProgressWirdDesc,
                        checked = state.autoProgressWird,
                        onCheckedChange = { onIntent(SettingsIntent.SetAutoProgressWird(it)) }
                    )
                    SettingsToggleRow(
                        label = strings.settingsSmartFlow,
                        description = strings.settingsSmartFlowDesc,
                        checked = state.isSmartFlowEnabled,
                        onCheckedChange = { onIntent(SettingsIntent.SetSmartFlowEnabled(it)) }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
        item { SettingsHeader(strings.settingsDailyReminders) }

        item {
            SettingsToggleRow(
                label = strings.settingsDailyReminders,
                description = strings.settingsDailyRemindersDesc,
                checked = state.dailyReminderEnabled,
                onCheckedChange = { onIntent(SettingsIntent.SetDailyReminderEnabled(it)) }
            )
        }
        if (state.dailyReminderEnabled) {
            item {
                val parts = state.dailyReminderTime.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30
                val displayTime = try {
                    LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("h:mm a"))
                } catch (e: Exception) { state.dailyReminderTime }

                // IX-NEW: replace native TimePickerDialog (light-themed system dialog) with
                // a fully themed M3 TimePicker inside a BasicAlertDialog so the dark
                // Sakīnah aesthetic is never broken by a system UI intrusion.
                var showTimePicker by rememberSaveable { mutableStateOf(false) }
                val timePickerState = rememberTimePickerState(
                    initialHour = hour, initialMinute = minute
                )

                SettingsActionRow(
                    label = strings.settingsReminderTime,
                    value = displayTime,
                    onClick = { showTimePicker = true }
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

        item { Spacer(Modifier.height(16.dp)) }
        item { SettingsHeader(strings.settingsAbout) }

        item {
            SettingsInfoRow(strings.settingsFont, "KFGQPC Uthmanic Script Hafs")
        }
        item {
            SettingsInfoRow(strings.settingsVersion, "1.0.0")
        }
    }
}

// Delegated to shared SabeelSectionHeader — kept as a local alias for zero call-site churn.
// Settings uses 1.8sp letter-spacing (tighter, more structured than Home's 1.5sp).
@Composable
private fun SettingsHeader(text: String) =
    SabeelSectionHeader(
        text = text,
        letterSpacing = 1.8.dp,
        modifier = Modifier.padding(bottom = 4.dp)
    )

@Composable
private fun SettingsSegmentRow(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { (value, display) ->
                val isSelected = selected == value
                // TY-02 fix: Urdu/Bengali render visually lighter than English
                // at the same requested weight (font-fallback quirk, not
                // intentional hierarchy) — most noticeable right here, where
                // all three scripts sit side by side before any language is
                // even selected. Boost matches the app-wide per-script scale
                // in Type.kt's getScaledTypography (+2sp ur, +1sp bn) so a
                // user's own language doesn't read as visually secondary.
                val scriptBoost = when (value) {
                    "ur" -> 2
                    "bn" -> 1
                    else -> 0
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SabeelColors.AccentTeal else SabeelColors.SurfaceElevated)
                        .clickable { onSelect(value) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = display,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            // TextUnit has no + operator (Sp + Em would be
                            // meaningless), so scale via .value like Type.kt's
                            // own scaleTypography() already does.
                            fontSize = (MaterialTheme.typography.labelLarge.fontSize.value + scriptBoost).sp
                        ),
                        color = if (isSelected) SabeelColors.Background else SabeelColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // LocalHapticFeedback is the Compose-idiomatic haptic API — no DI wiring
    // needed, available in any Composable. ToggleOn/Off give distinct feedback
    // for each direction of the switch, which is especially useful here since
    // settings toggles have no sound cue to complement the thumb animation.
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = { newValue ->
                // Fire haptic BEFORE calling through so the feedback is
                // synchronous with the finger-release event, not delayed
                // by any state propagation latency above.
                haptic.performHapticFeedback(
                    if (newValue) HapticFeedbackType.ToggleOn
                    else HapticFeedbackType.ToggleOff
                )
                onCheckedChange(newValue)
            },
            // IX-02 fix: rather than flip the thumb between a light and dark
            // color per state (two variables changing at once, which cost
            // the eye a fixed anchor to track), the thumb keeps its existing,
            // already-well-contrasted per-state color, and gains a checkmark
            // icon that only appears when ON — a shape/icon cue that reads
            // at a glance and doesn't depend on color perception at all.
            thumbContent = if (checked) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                        tint = SabeelColors.AccentTeal
                    )
                }
            } else null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SabeelColors.Background,
                checkedTrackColor = SabeelColors.AccentTeal,
                checkedBorderColor = SabeelColors.AccentTeal,
                checkedIconColor = SabeelColors.AccentTeal,
                uncheckedThumbColor = SabeelColors.TextSecondary,
                uncheckedTrackColor = SabeelColors.SurfaceElevated,
                uncheckedBorderColor = SabeelColors.BorderIdle
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = SabeelColors.TextSecondary)
    }
}

@Composable
private fun SettingsActionRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = SabeelColors.TextPrimary)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = SabeelColors.AccentTeal)
    }
}