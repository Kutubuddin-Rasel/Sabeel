package com.kutubuddin.sabeel.ui.wird

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.i18n.toLocalizedNumerals
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WirdEditScreen(
    viewModel: WirdEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val strings = LocalStrings.current
    var showPicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(SabeelColors.Background)) {
        Text(
            strings.wirdEditTitle,
            fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = SabeelColors.TextPrimary,
            modifier = Modifier.padding(20.dp)
        )
        HorizontalDivider(color = SabeelColors.Divider)

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.rows, key = { it.dhikrKey }) { row ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(SabeelColors.Surface).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column {
                        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = strings.wirdReorder,
                            tint = SabeelColors.TextSecondary,
                            modifier = Modifier.size(18.dp).clickable { viewModel.move(row.dhikrKey, up = true) })
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = strings.wirdReorder,
                            tint = SabeelColors.TextSecondary,
                            modifier = Modifier.size(18.dp).clickable { viewModel.move(row.dhikrKey, up = false) })
                    }
                    Text(row.displayName, Modifier.weight(1f), fontSize = 14.sp,
                        fontWeight = FontWeight.Medium, color = SabeelColors.TextPrimary)
                    // Target stepper
                    Icon(Icons.Filled.Remove, contentDescription = strings.wirdTargetA11y,
                        tint = SabeelColors.AccentTeal,
                        modifier = Modifier.size(22.dp).clickable { viewModel.updateTarget(row.dhikrKey, row.target - 1) })
                    Text(row.target.toLocalizedNumerals(state.language), fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, color = SabeelColors.TextPrimary,
                        modifier = Modifier.widthIn(min = 32.dp))
                    Icon(Icons.Filled.Add, contentDescription = strings.wirdTargetA11y,
                        tint = SabeelColors.AccentTeal,
                        modifier = Modifier.size(22.dp).clickable { viewModel.updateTarget(row.dhikrKey, row.target + 1) })
                    Icon(Icons.Outlined.Delete, contentDescription = strings.wirdRemove,
                        tint = SabeelColors.TextSecondary,
                        modifier = Modifier.size(20.dp).clickable { viewModel.remove(row.dhikrKey) })
                }
            }
        }

        Button(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = SabeelColors.Background)
            Spacer(Modifier.width(6.dp))
            Text(strings.wirdAddDhikr, color = SabeelColors.Background, fontWeight = FontWeight.Bold)
        }
    }

    if (showPicker) {
        ModalBottomSheet(onDismissRequest = { showPicker = false }, containerColor = SabeelColors.Surface) {
            LazyColumn(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                items(state.pickable, key = { it.key }) { d ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            viewModel.addDhikr(d.key, d.defaultTarget)
                            showPicker = false
                        }.padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(d.displayName.get(state.language), fontSize = 14.sp, color = SabeelColors.TextPrimary)
                        Text("${d.defaultTarget.toLocalizedNumerals(state.language)}×",
                            fontSize = 13.sp, color = SabeelColors.AccentTeal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
