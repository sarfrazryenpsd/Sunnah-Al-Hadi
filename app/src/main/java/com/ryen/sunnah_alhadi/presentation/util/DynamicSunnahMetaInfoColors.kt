package com.ryen.sunnah_alhadi.presentation.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ryen.sunnah_alhadi.R

enum class SunnahMetaInfoType {
    VERSE,
    SUPPLICATION,
    REFERENCE
}

data class SunnahMetaInfoStyle(
    @param:DrawableRes val icon: Int,
    val background: Color,
    val border: Color,
    val iconColor: Color
)

@Composable
fun getSunnahMetaInfoStyle(type: SunnahMetaInfoType): SunnahMetaInfoStyle {
    val isDark = isSystemInDarkTheme()

    return when (type) {
        SunnahMetaInfoType.VERSE -> if (isDark) {
            SunnahMetaInfoStyle(
                icon = R.drawable.ec_verse,
                background = Color(0xFF2F352E),
                border = Color(0xFF3E5D4E),
                iconColor = Color(0xFFA8CBB7) // soft green, contrasts well
            )
        } else {
            SunnahMetaInfoStyle(
                icon = R.drawable.ec_verse,
                background = Color(0xFFF5F2E9),
                border = Color(0xFF3E5D4E),
                iconColor = Color(0xFF3E5D4E) // matches border
            )
        }

        SunnahMetaInfoType.SUPPLICATION -> if (isDark) {
            SunnahMetaInfoStyle(
                icon = R.drawable.ec_supplication,
                background = Color(0xFF362F3D),
                border = Color(0xFF8A4D9E),
                iconColor = Color(0xFFD8B4E2) // soft lavender
            )
        } else {
            SunnahMetaInfoStyle(
                icon = R.drawable.ec_supplication,
                background = Color(0xFFF4ECF8),
                border = Color(0xFF8A4D9E),
                iconColor = Color(0xFF8A4D9E)
            )
        }

        SunnahMetaInfoType.REFERENCE -> if (isDark) {
            SunnahMetaInfoStyle(
                icon = R.drawable.ec_reference,
                background = Color(0xFF3A2E2A),
                border = Color(0xFF97A387),
                iconColor = Color(0xFFC5D1B6) // warm green-gray
            )
        } else {
            SunnahMetaInfoStyle(
                icon = R.drawable.ec_reference,
                background = Color(0xFFF9F3EB),
                border = Color(0xFF97A387),
                iconColor = Color(0xFF97A387)
            )
        }
    }
}

