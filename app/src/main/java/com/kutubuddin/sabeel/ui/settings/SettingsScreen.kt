package com.kutubuddin.sabeel.ui.settings

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import android.app.TimePickerDialog
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
@Composable
fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    val strings = LocalStrings.current

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

        item { Spacer(Modifier.height(4.dp)) }
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

        item { Spacer(Modifier.height(4.dp)) }
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
            SettingsToggleRow(
                label = strings.settingsAutoProgressWird,
                description = strings.settingsAutoProgressWirdDesc,
                checked = state.autoProgressWird,
                onCheckedChange = { onIntent(SettingsIntent.SetAutoProgressWird(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsSmartFlow,
                description = strings.settingsSmartFlowDesc,
                checked = state.isSmartFlowEnabled,
                onCheckedChange = { onIntent(SettingsIntent.SetSmartFlowEnabled(it)) }
            )
        }

        item { Spacer(Modifier.height(4.dp)) }
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
                val context = LocalContext.current
                val parts = state.dailyReminderTime.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30
                
                // Format for display (e.g., 08:30 PM)
                val displayTime = try {
                    LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("h:mm a"))
                } catch (e: Exception) {
                    state.dailyReminderTime
                }

                SettingsActionRow(
                    label = strings.settingsReminderTime,
                    value = displayTime,
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                                onIntent(SettingsIntent.SetDailyReminderTime(timeString))
                            },
                            hour,
                            minute,
                            false // 12-hour format depending on locale can be true/false, false shows AM/PM
                        ).show()
                    }
                )
            }
        }

        item { Spacer(Modifier.height(4.dp)) }
        item { SettingsHeader(strings.settingsAbout) }

        item {
            SettingsInfoRow(strings.settingsFont, "KFGQPC Uthmanic Script Hafs")
        }
        item {
            SettingsInfoRow(strings.settingsVersion, "1.0.0")
        }
    }
}

@Composable
private fun SettingsHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.8.sp),
        color = SabeelColors.TextSecondary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

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
            onCheckedChange = onCheckedChange,
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