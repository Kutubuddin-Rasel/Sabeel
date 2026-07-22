package com.kutubuddin.sabeel.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kutubuddin.sabeel.ui.theme.SabeelColors

@Composable
fun OnboardingScreen(
    currentLanguage: String,
    onLanguageSelect: (String) -> Unit,
    onFinish: () -> Unit
) {
    Scaffold(
        containerColor = SabeelColors.Background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Icon Circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SabeelColors.Surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Language,
                    contentDescription = null,
                    tint = SabeelColors.AccentTeal,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (currentLanguage == "bn") "আপনার ভাষা বেছে নিন" else "Choose Your Language",
                style = MaterialTheme.typography.headlineMedium,
                color = SabeelColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (currentLanguage == "bn") "সাবিল আপনার পছন্দ অনুযায়ী মানিয়ে নেয়। আপনার যাত্রা শুরু করতে ভাষা নির্বাচন করুন।" else "Sabeel adapts to you. Select your preferred language to begin your journey.",
                style = MaterialTheme.typography.bodyLarge,
                color = SabeelColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            LanguageSelectionCard(
                currentLanguage = currentLanguage,
                onLanguageSelect = onLanguageSelect
            )
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (currentLanguage == "bn") "শুরু করুন" else "Get Started",
                    color = SabeelColors.Background,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LanguageSelectionCard(
    currentLanguage: String,
    onLanguageSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SabeelColors.Surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LanguageOption(
            title = "English",
            isSelected = currentLanguage == "en",
            onClick = { onLanguageSelect("en") },
            modifier = Modifier.weight(1f)
        )
        LanguageOption(
            title = "বাংলা",
            isSelected = currentLanguage == "bn",
            onClick = { onLanguageSelect("bn") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LanguageOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SabeelColors.SurfaceElevated else SabeelColors.Surface)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) SabeelColors.AccentTeal else SabeelColors.TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
