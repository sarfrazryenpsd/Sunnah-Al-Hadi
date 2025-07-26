package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

@Composable
fun categoryCardGradient(categoryId: Int): Brush {
    val isDark = isSystemInDarkTheme()

    val baseColors = when (categoryId) {
        0 -> listOf(Color(0xFFBBE4A2), Color(0xFFA0CE8C))
        1 -> listOf(Color(0xFFF3D9B1), Color(0xFFE5C7A1))
        2 -> listOf(Color(0xFFE5D0EC), Color(0xFFD5BFE0))
        3 -> listOf(Color(0xFFFFF1DB), Color(0xFFEBD9C3))
        4 -> listOf(Color(0xFFF4B4A8), Color(0xFFE89C91))
        5 -> listOf(Color(0xFFB5D8F2), Color(0xFF9BC3E4))
        6 -> listOf(Color(0xFFFFCDB2), Color(0xFFF8B49A))
        7 -> listOf(Color(0xFFF7D38C), Color(0xFFEFCB77))
        8 -> listOf(Color(0xFFE5C8EF), Color(0xFFD8B2E3))
        9 -> listOf(Color(0xFFFFF1DB), Color(0xFFE9DDC7))
        10 -> listOf(Color(0xFFC4F0DC), Color(0xFFA6DEC8))
        11 -> listOf(Color(0xFFFFD8B8), Color(0xFFFEC09A))
        12 -> listOf(Color(0xFFE0E0E0), Color(0xFFCFCFCF))
        13 -> listOf(Color(0xFFEAD3BA), Color(0xFFD8BDA5))
        14 -> listOf(Color(0xFFA9D2F1), Color(0xFF90C1E5))
        15 -> listOf(Color(0xFFFFD3A5), Color(0xFFFEC18B))
        16 -> listOf(Color(0xFFD3E4BE), Color(0xFFB8CFA5))
        17 -> listOf(Color(0xFFBBE4A2), Color(0xFFA0CE8C))
        18 -> listOf(Color(0xFFFFE3CF), Color(0xFFEECFB9))
        19 -> listOf(Color(0xFFFAF4EC), Color(0xFFEDE3D2))
        20 -> listOf(Color(0xFFF7D38C), Color(0xFFEBC06A))
        21 -> listOf(Color(0xFFE0E0E0), Color(0xFFCBCBCB))
        22 -> listOf(Color(0xFFB6DAF4), Color(0xFF9CC5E2))
        23 -> listOf(Color(0xFFF7E9D0), Color(0xFFE7D3B9))
        24 -> listOf(Color(0xFFC4E7FA), Color(0xFFA7D2EA))
        25 -> listOf(Color(0xFFFFBABA), Color(0xFFEFA3A3))
        26 -> listOf(Color(0xFFE2DAD2), Color(0xFFCDBFB4))
        27 -> listOf(Color(0xFFD6B29D), Color(0xFFBD9A88))
        28 -> listOf(Color(0xFFC8E0D1), Color(0xFFACCEBB))
        29 -> listOf(Color(0xFFC8E0D1), Color(0xFFA9C9BA))
        else -> listOf(Color(0xFFE0E0E0), Color(0xFFB0B0B0)) // fallback
    }

    val finalColors = if (isDark) {
        baseColors.map { it.darken(0.15f).copy(alpha = 0.85f) }
    } else {
        baseColors
    }

    return Brush.linearGradient(colors = finalColors)
}


fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}