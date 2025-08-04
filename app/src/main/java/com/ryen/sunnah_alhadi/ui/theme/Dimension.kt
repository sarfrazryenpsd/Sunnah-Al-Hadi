package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val LocalDynamicDimensions = staticCompositionLocalOf<DynamicDimensions> {
    error("No DynamicDimensions provided")
}

data class DynamicDimensions(
    val topicCardWidth: Dp,
    val topicCardHeight: Dp,
    val sunnahCardWidth: Dp,
    val sunnahCardHeight: Dp,
    val compactCardWidth: Dp,
    val compactCardHeight: Dp,
    val cardPadding: Dp,
    val cardSpacing: Dp,
    val iconSize: Dp,
    val imageSize: Dp,
)

fun ScreenSize.toDynamicDimensions(): DynamicDimensions = when (this) {
    ScreenSize.COMPACT -> DynamicDimensions(
        topicCardWidth = 300.dp,
        topicCardHeight = 140.dp,
        sunnahCardWidth = 140.dp,
        sunnahCardHeight = 80.dp,
        compactCardWidth = 120.dp,
        compactCardHeight = 60.dp,
        cardPadding = 12.dp,
        cardSpacing = 8.dp,
        iconSize = 20.dp,
        imageSize = 56.dp
    )
    ScreenSize.MEDIUM -> DynamicDimensions(
        topicCardWidth = 200.dp,
        topicCardHeight = 120.dp,
        sunnahCardWidth = 160.dp,
        sunnahCardHeight = 90.dp,
        compactCardWidth = 140.dp,
        compactCardHeight = 70.dp,
        cardPadding = 16.dp,
        cardSpacing = 12.dp,
        iconSize = 24.dp,
        imageSize = 64.dp
    )
    ScreenSize.EXPANDED -> DynamicDimensions(
        topicCardWidth = 240.dp,
        topicCardHeight = 140.dp,
        sunnahCardWidth = 200.dp,
        sunnahCardHeight = 120.dp,
        compactCardWidth = 160.dp,
        compactCardHeight = 80.dp,
        cardPadding = 20.dp,
        cardSpacing = 16.dp,
        iconSize = 28.dp,
        imageSize = 72.dp
    )
}
