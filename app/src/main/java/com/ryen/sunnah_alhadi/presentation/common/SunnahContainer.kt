@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCardCompact
import com.ryen.sunnah_alhadi.presentation.components.cards.innerShadow
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.presentation.util.buildMetaInfoIconsForSunnah
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme


@Composable
fun SunnahGridCardContainer(
    sunnahs: List<Sunnah>,
    onSunnahClick: (Int) -> Unit,
    screenSize: ScreenSize,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
            .height(600.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
            .innerShadow(
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                blur = 12.dp,
                offsetX = 0.dp,
                offsetY = 0.dp,
            )
    ) {
        val columns = when (screenSize) {
            ScreenSize.COMPACT -> 1
            ScreenSize.MEDIUM -> 2
            ScreenSize.EXPANDED -> 1
        }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columns),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = sunnahs,
                key = { index: Int, sunnah: Sunnah -> sunnah.id }
            ) { index: Int, sunnah: Sunnah ->

                val metaIcons = buildMetaInfoIconsForSunnah(sunnah)
                val gradientColors = CategoryUtils.categoryGradientColors(sunnah.categoryId)

                SunnahCardCompact(
                    title = sunnah.title,
                    extraIcons = metaIcons,
                    borderColor = gradientColors.first(),
                    modifier = modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(gradientColors.first().copy(alpha = 0.5f))
                        .clickable { onSunnahClick(index) }
                )
            }
        }
    }
}

@Preview
@Composable
fun SunnahCompactCardContainerPreview() {
    SunnahAlHadiTheme(windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))) {
        val sunnahs = listOf(
            Sunnah(
                id = "1",
                categoryId = 1,
                title = "Accountability For Rich Neighbours",
                body = listOf(
                    ContentBlock(ContentType.ARABIC_TEXT, ArabicSubtype.VERSE.name, "Verse"),
                    ContentBlock(ContentType.ENGLISH_TEXT, "NORMAL", "Translation")
                ),
                extra = listOf()
            ),
            Sunnah(
                id = "2",
                categoryId = 2,
                title = "A Believer Feeds His Neighbors",
                body = listOf(
                    ContentBlock(ContentType.ARABIC_TEXT, "PARABLE", "Parable")
                ),
                references = listOf()
            ),
            Sunnah(
                id = "3",
                categoryId = 3,
                title = "Additional Veiling While Eating",
                body = listOf(),
                references = listOf()
            )
        )
        SunnahGridCardContainer(
            sunnahs = sunnahs,
            onSunnahClick = {},
            screenSize = ScreenSize.COMPACT
        )
    }
}
