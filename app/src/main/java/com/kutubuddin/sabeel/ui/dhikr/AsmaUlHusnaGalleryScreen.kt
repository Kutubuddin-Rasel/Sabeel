package com.kutubuddin.sabeel.ui.dhikr

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kutubuddin.sabeel.domain.model.DhikrCatalog
import com.kutubuddin.sabeel.domain.model.DhikrItem
import com.kutubuddin.sabeel.ui.components.SabeelTopBar
import com.kutubuddin.sabeel.ui.i18n.LocalStrings
import com.kutubuddin.sabeel.ui.theme.SabeelColors
import com.kutubuddin.sabeel.ui.theme.arabicStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsmaUlHusnaGalleryScreen(
    language: String,
    showTransliteration: Boolean = true,
    onBack: () -> Unit,
    onCountNow: (key: String) -> Unit
) {
    val strings = LocalStrings.current
    val asmaUlHusnaList = DhikrCatalog.asmaUlHusnaList

    Scaffold(
        topBar = {
            SabeelTopBar(
                title = strings.categoryLabel(com.kutubuddin.sabeel.domain.model.DhikrCategory.ASMA_UL_HUSNA),
                onBack = onBack,
                backContentDescription = strings.a11yBack
            )
        },
        containerColor = SabeelColors.Background
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(asmaUlHusnaList, key = { it.key }) { item ->
                AsmaUlHusnaGridCell(
                    item = item,
                    language = language,
                    showTransliteration = showTransliteration,
                    // animateItem() gives each cell a fade+placement slide-in.
                    // On a 3-col grid this produces a natural top-to-bottom
                    // stagger as rows of 3 render sequentially.
                    modifier = Modifier.animateItem(),
                    onClick = { onCountNow(item.key) }
                )
            }
        }
    }
}

@Composable
private fun AsmaUlHusnaGridCell(
    item: DhikrItem,
    language: String,
    showTransliteration: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(SabeelColors.Surface)
            .border(1.dp, SabeelColors.BorderIdle, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = item.arabicText,
            color = SabeelColors.ArabicText,
            style = arabicStyle.copy(fontSize = 24.sp, lineHeight = 46.sp), // 24 × 1.9 = 45.6sp — diacritic safety rule
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (showTransliteration) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.transliteration?.get(language) ?: "",
                color = SabeelColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
