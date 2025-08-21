@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.cards.innerShadow
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.presentation.util.buildMetaInfoIconsForSunnah
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize


@Composable
fun SunnahGridCardContainer(
    sunnahs: List<Sunnah>,
    onSunnahClick: (Int) -> Unit,
    screenSize: ScreenSize,
    modifier: Modifier = Modifier,
    searchQuery: String = "", // Add search query parameter
    showAnimations: Boolean = true // Add animation control
) {

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
            .height(640.dp)
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

                if (showAnimations) {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { it / 2 }
                        ) + fadeIn(animationSpec = tween(300)),
                        exit = slideOutVertically(
                            animationSpec = tween(300),
                            targetOffsetY = { -it / 2 }
                        ) + fadeOut(animationSpec = tween(300))
                    ) {
                        SunnahCardCompactWithHighlight(
                            title = sunnah.title,
                            extraIcons = metaIcons,
                            borderColor = gradientColors.first(),
                            searchQuery = searchQuery,
                            modifier = modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(gradientColors.first().copy(alpha = 0.5f))
                                .clickable { onSunnahClick(index) }
                        )
                    }
                } else {
                    SunnahCardCompactWithHighlight(
                        title = sunnah.title,
                        extraIcons = metaIcons,
                        borderColor = gradientColors.first(),
                        searchQuery = searchQuery,
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
}

@Composable
fun SunnahCardCompactWithHighlight(
    modifier: Modifier = Modifier,
    title: String,
    extraIcons: List<@Composable () -> Unit>,
    borderColor: Color,
    searchQuery: String = "",
) {
    Card(
        modifier = modifier
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Highlighted title
            HighlightedText(
                text = title,
                searchQuery = searchQuery,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (extraIcons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    extraIcons.forEach { icon ->
                        icon()
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    searchQuery: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    highlightColor: Color = MaterialTheme.colorScheme.primaryContainer,
    highlightTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    if (searchQuery.isEmpty() || searchQuery.length < 2) {
        Text(
            text = text,
            style = style,
            modifier = modifier
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        val cleanQuery = searchQuery.trim()
        val lowerText = text.lowercase()
        val lowerQuery = cleanQuery.lowercase()

        var currentIndex = 0
        var matchIndex = lowerText.indexOf(lowerQuery, currentIndex)

        while (matchIndex != -1 && matchIndex < text.length) {
            // Add text before match
            if (matchIndex > currentIndex) {
                append(text.substring(currentIndex, matchIndex))
            }

            // Add highlighted match
            val matchEnd = (matchIndex + cleanQuery.length).coerceAtMost(text.length)
            withStyle(
                style = SpanStyle(
                    background = highlightColor,
                    color = highlightTextColor
                )
            ) {
                append(text.substring(matchIndex, matchEnd))
            }

            currentIndex = matchEnd
            matchIndex = lowerText.indexOf(lowerQuery, currentIndex)
        }

        // Add remaining text
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier
    )
}
