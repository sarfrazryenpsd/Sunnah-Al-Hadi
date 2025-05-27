package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Provider composable to setup dynamic typography
@Composable
fun DynamicTypographyProvider(
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    val screenSize = remember(windowSizeClass) { windowSizeClass.toScreenSize() }
    val scaleFactors = remember(screenSize) { screenSize.getScaleFactors() }
    val textConfig = remember(screenSize, scaleFactors) {
        DynamicTextConfig(screenSize, scaleFactors)
    }
    val lineHeightConfig = remember(scaleFactors) {
        DynamicLineHeightConfig(scaleFactors)
    }

    CompositionLocalProvider(
        LocalDynamicTextConfig provides textConfig,
        LocalDynamicLineHeightConfig provides lineHeightConfig
    ) {
        content()
    }
}

// Dynamic Typography objects that adapt to screen size
object DynamicAppTypography {
    @Composable
    fun displayLarge(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.displayLarge,
            lineHeight = lineHeightConfig.displayLarge,
            letterSpacing = (-0.25).sp
        )
    }

    @Composable
    fun displayMedium(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.displayMedium,
            lineHeight = lineHeightConfig.displayMedium,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun displaySmall(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.displaySmall,
            lineHeight = lineHeightConfig.displaySmall,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun headlineLarge(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = textConfig.headlineLarge,
            lineHeight = lineHeightConfig.headlineLarge,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun headlineMedium(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = textConfig.headlineMedium,
            lineHeight = lineHeightConfig.headlineMedium,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun headlineSmall(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = textConfig.headlineSmall,
            lineHeight = lineHeightConfig.headlineSmall,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun titleLarge(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.titleLarge,
            lineHeight = lineHeightConfig.titleLarge,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun titleMedium(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.titleMedium,
            lineHeight = lineHeightConfig.titleMedium,
            letterSpacing = 0.15.sp
        )
    }

    @Composable
    fun titleSmall(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.titleSmall,
            lineHeight = lineHeightConfig.titleSmall,
            letterSpacing = 0.1.sp
        )
    }

    @Composable
    fun bodyLarge(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.bodyLarge,
            lineHeight = lineHeightConfig.bodyLarge,
            letterSpacing = 0.15.sp
        )
    }

    @Composable
    fun bodyMedium(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.bodyMedium,
            lineHeight = lineHeightConfig.bodyMedium,
            letterSpacing = 0.25.sp
        )
    }

    @Composable
    fun bodySmall(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.bodySmall,
            lineHeight = lineHeightConfig.bodySmall,
            letterSpacing = 0.4.sp
        )
    }

    @Composable
    fun labelLarge(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.labelLarge,
            lineHeight = lineHeightConfig.labelLarge,
            letterSpacing = 0.1.sp
        )
    }

    @Composable
    fun labelMedium(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.labelMedium,
            lineHeight = lineHeightConfig.labelMedium,
            letterSpacing = 0.5.sp
        )
    }

    @Composable
    fun labelSmall(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.labelSmall,
            lineHeight = lineHeightConfig.labelSmall,
            letterSpacing = 0.5.sp
        )
    }
}

// Dynamic Content Typography
object DynamicContentTypography {
    @Composable
    fun sunnahTitle(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = amiri,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.sunnahTitle,
            lineHeight = lineHeightConfig.sunnahTitle,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun topicHeading(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = cinzel_decorative,
            fontWeight = FontWeight.Black,
            fontSize = textConfig.topicHeading,
            lineHeight = lineHeightConfig.topicHeading,
            letterSpacing = 0.5.sp
        )
    }

    @Composable
    fun englishBodyNormal(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = notoSerifJP,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.englishBodyNormal,
            lineHeight = lineHeightConfig.englishBodyNormal,
            letterSpacing = 0.15.sp
        )
    }

    @Composable
    fun englishBodyTranslation(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = cormorant_garamond,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
            fontSize = textConfig.englishBodyTranslation,
            lineHeight = lineHeightConfig.englishBodyTranslation,
            letterSpacing = 0.15.sp
        )
    }

    @Composable
    fun reference(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = notoSerifJP,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.reference,
            lineHeight = lineHeightConfig.reference,
            letterSpacing = 0.25.sp
        )
    }
}

// Dynamic Arabic Typography
object DynamicArabicTypography {
    @Composable
    fun honorific(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = mirza,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.arabicHonorific,
            lineHeight = lineHeightConfig.arabicHonorific,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun supplication(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = lateef,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.arabicSupplication,
            lineHeight = lineHeightConfig.arabicSupplication,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun quranicVerse(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = lateef,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.arabicVerse,
            lineHeight = lineHeightConfig.arabicVerse,
            letterSpacing = 0.sp
        )
    }

    @Composable
    fun other(): TextStyle {
        val textConfig = LocalDynamicTextConfig.current
        val lineHeightConfig = LocalDynamicLineHeightConfig.current
        return TextStyle(
            fontFamily = notoNaskhArabic,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.arabicOther,
            lineHeight = lineHeightConfig.arabicOther,
            letterSpacing = 0.sp
        )
    }
}

// Accessibility helper for minimum touch targets
object AccessibilityConstants {
    val MIN_TOUCH_TARGET = 48.dp
    val RECOMMENDED_LINE_HEIGHT_RATIO = 1.5f
}

