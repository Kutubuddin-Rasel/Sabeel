package com.kutubuddin.sabeel.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kutubuddin.sabeel.ui.theme.SabeelColors

/**
 * Minimal header for any screen reached by pushing onto the nav back stack —
 * i.e. anything that is NOT one of the 4 bottom-nav tabs ([com.kutubuddin.sabeel.ui.navigation.SabeelTab]).
 *
 * SRP: renders a back affordance + title + optional trailing [actions] only.
 * All navigation is hoisted via [onBack] — this composable owns no
 * NavController, no ViewModel, no business logic, so it is a pure function of
 * its parameters and reusable, unmodified, across every future sub-screen (OCP).
 *
 * Uses the auto-mirrored back arrow so it points the correct reading direction
 * under Urdu's RTL layout, matching the rest of the app's direction-aware
 * chrome (see DhikrLibraryScreen's LTR/RTL continue-arrow).
 */
@Composable
fun SabeelTopBar(
    title: String,
    onBack: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = backContentDescription,
                tint = SabeelColors.TextPrimary
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = SabeelColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        actions()
    }
}