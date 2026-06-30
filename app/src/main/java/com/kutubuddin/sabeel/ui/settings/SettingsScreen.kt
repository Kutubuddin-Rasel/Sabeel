package com.kutubuddin.sabeel.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SabeelColors.Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { SettingsHeader("Appearance") }

        item {
            SettingsSegmentRow(
                label = "Theme",
                options = listOf("dark" to "Dark", "light" to "Light"),
                selected = state.theme,
                onSelect = { viewModel.processIntent(SettingsIntent.SetTheme(it)) }
            )
        }

        item { Spacer(Modifier.height(4.dp)) }
        item { SettingsHeader("Language & Text") }

        item {
            SettingsSegmentRow(
                label = "Translation",
                options = listOf("en" to "English", "ur" to "اردو", "bn" to "বাংলা"),
                selected = state.language,
                onSelect = { viewModel.processIntent(SettingsIntent.SetLanguage(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = "Show Transliteration",
                description = "Romanized pronunciation under Arabic",
                checked = state.translitEnabled,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetTranslit(it)) }
            )
        }

        item { Spacer(Modifier.height(4.dp)) }
        item { SettingsHeader("Counting Behaviour") }

        item {
            SettingsSegmentRow(
                label = "Haptic Feedback",
                options = listOf("off" to "Off", "light" to "Light", "medium" to "Medium", "strong" to "Strong"),
                selected = state.hapticsLevel,
                onSelect = { viewModel.processIntent(SettingsIntent.SetHaptics(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = "Sound on Milestone",
                description = "Subtle chime at 33, 100 etc.",
                checked = state.soundEnabled,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetSoundOn(it)) }
            )
        }
        item {
            SettingsToggleRow(
                label = "Auto-reset on Completion",
                description = "Counter resets when target is hit",
                checked = state.autoReset,
                onCheckedChange = { viewModel.processIntent(SettingsIntent.SetAutoReset(it)) }
            )
        }

        item { Spacer(Modifier.height(4.dp)) }
        item { SettingsHeader("Daily Goal") }

        item {
            DailyGoalRow(
                goal = state.dailyGoal,
                onGoalChange = { viewModel.processIntent(SettingsIntent.SetDailyGoal(it)) }
            )
        }

        item { Spacer(Modifier.height(4.dp)) }
        item { SettingsHeader("About") }

        item {
            SettingsInfoRow("Font", "KFGQPC Uthmanic Script Hafs")
        }
        item {
            SettingsInfoRow("Version", "1.0.0")
        }
    }
}

@Composable
private fun SettingsHeader(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.8.sp,
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
        Text(label, fontSize = 14.sp, color = SabeelColors.TextPrimary, fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (value, display) ->
                val isSelected = selected == value
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SabeelColors.GoldPrimary else SabeelColors.SurfaceElevated)
                        .clickable { onSelect(value) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = display,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
            Text(label, fontSize = 14.sp, color = SabeelColors.TextPrimary, fontWeight = FontWeight.Medium)
            Text(description, fontSize = 11.sp, color = SabeelColors.TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SabeelColors.Background,
                checkedTrackColor = SabeelColors.GoldPrimary,
                uncheckedThumbColor = SabeelColors.TextSecondary,
                uncheckedTrackColor = SabeelColors.SurfaceElevated
            )
        )
    }
}

@Composable
private fun DailyGoalRow(goal: Int, onGoalChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SabeelColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Daily target", fontSize = 14.sp, color = SabeelColors.TextPrimary, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(
                onClick = { if (goal > 50) onGoalChange(goal - 50) },
                colors = ButtonDefaults.textButtonColors(contentColor = SabeelColors.GoldPrimary)
            ) { Text("−", fontSize = 20.sp) }
            Text("$goal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SabeelColors.TextPrimary)
            TextButton(
                onClick = { if (goal < 1000) onGoalChange(goal + 50) },
                colors = ButtonDefaults.textButtonColors(contentColor = SabeelColors.GoldPrimary)
            ) { Text("+", fontSize = 20.sp) }
        }
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
        Text(label, fontSize = 14.sp, color = SabeelColors.TextPrimary)
        Text(value, fontSize = 13.sp, color = SabeelColors.TextSecondary)
    }
}
