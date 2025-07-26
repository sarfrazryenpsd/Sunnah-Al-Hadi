package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class MetaInfoType {
    VERSE,
    SUPPLICATION,
    REFERENCE
}
data class MetaInfoStyle(
    val background: Color,
    val border: Color
)

@Composable
fun getSunnahMetaInfoStyle(type: MetaInfoType): MetaInfoStyle {
    val isDark = isSystemInDarkTheme()

    return when (type) {
        MetaInfoType.VERSE -> if (isDark) {
            MetaInfoStyle(background = Color(0xFF2F352E), border = Color(0xFF3E5D4E))
        } else {
            MetaInfoStyle(background = Color(0xFFF5F2E9), border = Color(0xFF3E5D4E))
        }

        MetaInfoType.SUPPLICATION -> if (isDark) {
            MetaInfoStyle(background = Color(0xFF362F3D), border = Color(0xFF8A4D9E))
        } else {
            MetaInfoStyle(background = Color(0xFFF4ECF8), border = Color(0xFF8A4D9E))
        }

        MetaInfoType.REFERENCE -> if (isDark) {
            MetaInfoStyle(background = Color(0xFF3A2E2A), border = Color(0xFF97A387))
        } else {
            MetaInfoStyle(background = Color(0xFFF9F3EB), border = Color(0xFF97A387))
        }
    }
}
