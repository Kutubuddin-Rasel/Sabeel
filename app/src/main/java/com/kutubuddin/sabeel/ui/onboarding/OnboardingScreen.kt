package com.kutubuddin.sabeel.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.SabeelMotion

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    currentLanguage: String,
    onLanguageSelect: (String) -> Unit,
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Focus on Dhikr",
            description = "Tap the large circle to count. Experience deeply meditative Dhikr with advanced haptic feedback, letting you focus without constantly looking at your phone.",
            icon = Icons.Rounded.TouchApp
        ),
        OnboardingPage(
            title = "Daily Spiritual Habits",
            description = "Build a resilient routine with the Daily Wird. Track your essential daily recitations seamlessly and grow your connection.",
            icon = Icons.Rounded.Shield
        ),
        OnboardingPage(
            title = "Extensive Library",
            description = "Explore a rich collection of Dhikr and Asma Ul Husna, beautifully rendered in Uthmanic typography with full transliteration.",
            icon = Icons.AutoMirrored.Rounded.MenuBook
        ),
        OnboardingPage(
            title = "Choose Your Language",
            description = "Sabeel adapts to you. Select your preferred language to begin your journey.",
            icon = Icons.Rounded.Language
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

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
            Spacer(modifier = Modifier.height(48.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    isLastPage = page == pages.size - 1,
                    currentLanguage = currentLanguage,
                    onLanguageSelect = onLanguageSelect
                )
            }

            // Bottom Navigation & Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isSelected) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) SabeelColors.AccentTeal
                                    else SabeelColors.SurfaceElevated
                                )
                        )
                    }
                }

                // Next / Get Started Button
                AnimatedVisibility(
                    visible = pagerState.currentPage == pages.size - 1,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    modifier = Modifier.height(48.dp)
                ) {
                    Button(
                        onClick = onFinish,
                        colors = ButtonDefaults.buttonColors(containerColor = SabeelColors.AccentTeal),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Get Started",
                            color = SabeelColors.Background,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (pagerState.currentPage != pages.size - 1) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage + 1,
                                    animationSpec = tween(
                                        durationMillis = SabeelMotion.Duration.SheetEnter,
                                        easing = SabeelMotion.EmphasizedDecelerate
                                    )
                                )
                            }
                        }
                    ) {
                        Text(
                            text = "Next",
                            color = SabeelColors.TextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isLastPage: Boolean,
    currentLanguage: String,
    onLanguageSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(SabeelColors.Surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = SabeelColors.AccentTeal,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = SabeelColors.TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = SabeelColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (isLastPage) {
            Spacer(modifier = Modifier.height(48.dp))
            LanguageSelectionCard(
                currentLanguage = currentLanguage,
                onLanguageSelect = onLanguageSelect
            )
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
