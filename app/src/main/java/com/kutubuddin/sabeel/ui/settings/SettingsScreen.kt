package com.kutubuddin.sabeel.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
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
                onSelect = { viewModel.processIntent(SettingsIntent.SetTheme(it)) }
            )
        }

        item { Spacer(Modifier.height(4.dp)) }
        item { SettingsHeader(strings.settingsLanguageText) }

        item {
            // Language endonyms stay in their own script regardless of app
            // language — that is how a user recognises their own language.
            SettingsSegmentRow(
                label = strings.settingsLanguage,
                options = listOf("en" to "English", "ur" to "اردو", "bn" to "বাংলা"),
                selected = state.language,
                onSelect = { viewModel.processIntent(SettingsIntent.SetLanguage(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsTranslit,
                description = strings.settingsTranslitDesc,
                checked = state.translitEnabled,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetTranslit(it)) }
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
                onSelect = { viewModel.processIntent(SettingsIntent.SetHaptics(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsSound,
                description = strings.settingsSoundDesc,
                checked = state.soundEnabled,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetSoundOn(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsAutoReset,
                description = strings.settingsAutoResetDesc,
                checked = state.autoReset,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetAutoReset(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsShowStreaks,
                description = strings.settingsShowStreaksDesc,
                checked = state.showStreaks,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetShowStreaks(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = strings.settingsAutoProgressWird,
                description = strings.settingsAutoProgressWirdDesc,
                checked = state.autoProgressWird,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetAutoProgressWird(it)) }
            )
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
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
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
            colors = SwitchDefaults.colors(
                checkedThumbColor = SabeelColors.Background,
                checkedTrackColor = SabeelColors.AccentTeal,
                checkedBorderColor = SabeelColors.AccentTeal,
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
