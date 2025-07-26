package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType

// Core mapping
data class ExtraContentColor(
    val iconBackground: Color,
    val containerBackground: Color,
    val borderColor: Color
)

@Composable
fun ColorScheme.extraContentColors(type: ExtraContentType): ExtraContentColor {
    val isDark = isSystemInDarkTheme()


    return when (type) {
        ExtraContentType.PARABLE -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF5A2A2A) else Color(0xFFFFEAEA),
            containerBackground = if (isDark) Color(0xFF2D1E1E) else Color(0xFFFFF8F8),
            borderColor = if (isDark) Color(0xFFFF8B8B) else Color(0xFFE86C6C)
        )

        ExtraContentType.SCHOLARLY_EXPLANATION -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF23422E) else Color(0xFFE8F5E9),
            containerBackground = if (isDark) Color(0xFF1F2920) else Color(0xFFF3FAF4),
            borderColor = if (isDark) Color(0xFF6B9C6B) else Color(0xFF1B5E20)
        )

        ExtraContentType.EXPLANATION -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF1A3B3E) else Color(0xFFE0F7FA),
            containerBackground = if (isDark) Color(0xFF0B2326) else Color(0xFFF2FDFF),
            borderColor = if (isDark) Color(0xFF0D8E9F) else Color(0xFF26C6DA)
        )

        ExtraContentType.TRANSLATION -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF2B2F2C) else Color(0xFFF0F5F0),
            containerBackground = if (isDark) Color(0xFF1E2722) else Color(0xFFF8FFF9),
            borderColor = if (isDark) Color(0xFF9E7CBF) else Color(0xFF512DA8)
        )

        ExtraContentType.HADITH -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF3B2F1E) else Color(0xFFFAF3E0),
            containerBackground = if (isDark) Color(0xFF2A241A) else Color(0xFFFCF9F4),
            borderColor = if (isDark) Color(0xFFC69C6D) else Color(0xFFB47D45)
        )

        ExtraContentType.NOTES -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF4A3F2B) else Color(0xFFFFF8E1),
            containerBackground = if (isDark) Color(0xFF2A261A) else Color(0xFFFFFCF2),
            borderColor = if (isDark) Color(0xFFD2B365) else Color(0xFFFFD54F)
        )

        ExtraContentType.WARNING -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF5A1C1C) else Color(0xFFFFE0E0),
            containerBackground = if (isDark) Color(0xFF3B1E1D) else Color(0xFFFFF6F6),
            borderColor = if (isDark) Color(0xFFF65E53) else Color(0xFFE53935)
        )

        ExtraContentType.BENEFIT -> ExtraContentColor(
            iconBackground = if (isDark) Color(0xFF4A3A1A) else Color(0xFFFFFBEA),
            containerBackground = if (isDark) Color(0xFF322A0F) else Color(0xFFFFFDF5),
            borderColor = if (isDark) Color(0xFFFFC107) else Color(0xFFFFB300)
        )
    }
}