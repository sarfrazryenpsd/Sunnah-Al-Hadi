package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.ryen.sunnah_alhadi.R


object CategoryUtils{

    private val gradientMap = mapOf(
        0 to listOf(Color(0xFFBBE4A2), Color(0xFFA0CE8C)), // Walking
        1 to listOf(Color(0xFFF3D9B1), Color(0xFFE5C7A1)), // Wearing Shoes
        2 to listOf(Color(0xFFE5D0EC), Color(0xFFD5BFE0)), // Sitting
        3 to listOf(Color(0xFFFFF1DB), Color(0xFFEBD9C3)), // Entering Home
        4 to listOf(Color(0xFFF4B4A8), Color(0xFFE89C91)),
        5 to listOf(Color(0xFFB5D8F2), Color(0xFF9BC3E4)),
        6 to listOf(Color(0xFFFFCDB2), Color(0xFFF8B49A)),
        7 to listOf(Color(0xFFF7D38C), Color(0xFFEFCB77)),
        8 to listOf(Color(0xFFE5C8EF), Color(0xFFD8B2E3)),
        9 to listOf(Color(0xFFFFF1DB), Color(0xFFE9DDC7)),
        10 to listOf(Color(0xFFC4F0DC), Color(0xFFA6DEC8)),
        11 to listOf(Color(0xFFFFD8B8), Color(0xFFFEC09A)),
        12 to listOf(Color(0xFFE0E0E0), Color(0xFFCFCFCF)),
        13 to listOf(Color(0xFFEAD3BA), Color(0xFFD8BDA5)),
        14 to listOf(Color(0xFFA9D2F1), Color(0xFF90C1E5)),
        15 to listOf(Color(0xFFFFD3A5), Color(0xFFFEC18B)),
        16 to listOf(Color(0xFFD3E4BE), Color(0xFFB8CFA5)),
        17 to listOf(Color(0xFFBBE4A2), Color(0xFFA0CE8C)),
        18 to listOf(Color(0xFFFFE3CF), Color(0xFFEECFB9)),
        19 to listOf(Color(0xFFFAF4EC), Color(0xFFEDE3D2)),
        20 to listOf(Color(0xFFF7D38C), Color(0xFFEBC06A)),
        21 to listOf(Color(0xFFE0E0E0), Color(0xFFCBCBCB)),
        22 to listOf(Color(0xFFB6DAF4), Color(0xFF9CC5E2)),
        23 to listOf(Color(0xFFF7E9D0), Color(0xFFE7D3B9)),
        24 to listOf(Color(0xFFC4E7FA), Color(0xFFA7D2EA)),
        25 to listOf(Color(0xFFFFBABA), Color(0xFFEFA3A3)),
        26 to listOf(Color(0xFFE2DAD2), Color(0xFFCDBFB4)),
        27 to listOf(Color(0xFFD6B29D), Color(0xFFBD9A88)),
        28 to listOf(Color(0xFFC8E0D1), Color(0xFFACCEBB)),
        29 to listOf(Color(0xFFC8E0D1), Color(0xFFA9C9BA))
    )

    @Composable
    fun categoryGradient(categoryId: Int): Brush {
        val isDark = isSystemInDarkTheme()



        val colors = gradientMap[categoryId] ?: listOf(Color.Gray, Color.DarkGray)

        val adjustedColors = if (isDark) {
            colors.map { it.darken(0.15f).copy(alpha = 0.85f) }
        } else {
            colors
        }

        return Brush.linearGradient(adjustedColors)
    }

    @Composable
    fun categoryGradientColors(categoryId: Int): List<Color> {
        val isDark = isSystemInDarkTheme()

        val colors = gradientMap[categoryId] ?: listOf(Color.Gray, Color.DarkGray)

        val adjustedColors = if (isDark) {
            colors.map { it.darken(0.15f).copy(alpha = 0.85f) }
        } else {
            colors
        }

        return adjustedColors
    }


    fun Color.darken(factor: Float): Color {
        return Color(
            red = (red * (1 - factor)).coerceIn(0f, 1f),
            green = (green * (1 - factor)).coerceIn(0f, 1f),
            blue = (blue * (1 - factor)).coerceIn(0f, 1f),
            alpha = alpha
        )
    }

    val categoryImageMap = mapOf(
        0 to R.drawable.walking_00,
        1 to R.drawable.wearing_shoes_01,
        2 to R.drawable.sitting_02,
        3 to R.drawable.entering_leaving_03,
        4 to R.drawable.neighbors_04,
        5 to R.drawable.drinking_05,
        6 to R.drawable.eating_06,
        7 to R.drawable.hospitality_07,
        8 to R.drawable.treating_relatives_08,
        9 to R.drawable.salam_09,
        10 to R.drawable.handshake_10,
        11 to R.drawable.conversation_11,
        12 to R.drawable.sneezing_12,
        13 to R.drawable.kohl_13,
        14 to R.drawable.sleeping_14,
        15 to R.drawable.hairstyles_15,
        16 to R.drawable.oil_combing_16,
        17 to R.drawable.miswak_17,
        18 to R.drawable.nails_18,
        19 to R.drawable.clothing_19,
        20 to R.drawable.imamah_20,
        21 to R.drawable.ring_21,
        22 to R.drawable.aqeeqah_22,
        23 to R.drawable.naming_23,
        24 to R.drawable.travelling_24,
        25 to R.drawable.visiting_sick_25,
        26 to R.drawable.shrouding_26,
        27 to R.drawable.funerals_27,
        28 to R.drawable.burials_28,
        29 to R.drawable.graveyards_29
    )
}