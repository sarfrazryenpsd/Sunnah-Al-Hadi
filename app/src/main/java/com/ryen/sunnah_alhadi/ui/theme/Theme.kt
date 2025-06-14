package com.ryen.sunnah_alhadi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.presentation.util.DynamicTypographyProvider

// Theme preferences
enum class ThemeMode {
    LIGHT, DARK, SYSTEM, DYNAMIC
}

// Create light color scheme
private val LightColorScheme = lightColorScheme(
    primary = SunnahColors.LightPrimary,
    onPrimary = SunnahColors.LightOnPrimary,
    primaryContainer = SunnahColors.LightPrimaryContainer,
    onPrimaryContainer = SunnahColors.LightOnPrimaryContainer,
    secondary = SunnahColors.LightSecondary,
    onSecondary = SunnahColors.LightOnSecondary,
    background = SunnahColors.LightBackground,
    onBackground = SunnahColors.LightOnBackground,
    surface = SunnahColors.LightSurface,
    onSurface = SunnahColors.LightOnSurface,
    surfaceVariant = SunnahColors.LightSurfaceVariant,
    onSurfaceVariant = SunnahColors.LightOnSurfaceVariant,
    outline = SunnahColors.LightOutline,
    outlineVariant = SunnahColors.LightOutlineVariant,
    scrim = SunnahColors.LightScrim,
    inverseSurface = SunnahColors.LightInverseSurface,
    inverseOnSurface = SunnahColors.LightInverseOnSurface,
    inversePrimary = SunnahColors.LightInversePrimary,
    surfaceTint = SunnahColors.LightSurfaceTint
)

// Create dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = SunnahColors.DarkPrimary,
    onPrimary = SunnahColors.DarkOnPrimary,
    primaryContainer = SunnahColors.DarkPrimaryContainer,
    onPrimaryContainer = SunnahColors.DarkOnPrimaryContainer,
    secondary = SunnahColors.DarkSecondary,
    onSecondary = SunnahColors.DarkOnSecondary,
    background = SunnahColors.DarkBackground,
    onBackground = SunnahColors.DarkOnBackground,
    surface = SunnahColors.DarkSurface,
    onSurface = SunnahColors.DarkOnSurface,
    surfaceVariant = SunnahColors.DarkSurfaceVariant,
    onSurfaceVariant = SunnahColors.DarkOnSurfaceVariant,
    outline = SunnahColors.DarkOutline,
    outlineVariant = SunnahColors.DarkOutlineVariant,
    scrim = SunnahColors.DarkScrim,
    inverseSurface = SunnahColors.DarkInverseSurface,
    inverseOnSurface = SunnahColors.DarkInverseOnSurface,
    inversePrimary = SunnahColors.DarkInversePrimary,
    surfaceTint = SunnahColors.DarkSurfaceTint
)

// Dynamic Typography that works with MaterialTheme
@Composable
fun createDynamicTypography(windowSizeClass: WindowSizeClass): Typography {
    val screenSize = remember(windowSizeClass) { windowSizeClass.toScreenSize() }
    val scaleFactors = remember(screenSize) { screenSize.getScaleFactors() }
    val textConfig = remember(screenSize, scaleFactors) {
        DynamicTextConfig(screenSize, scaleFactors)
    }
    val lineHeightConfig = remember(scaleFactors) {
        DynamicLineHeightConfig(scaleFactors)
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.displayLarge,
            lineHeight = lineHeightConfig.displayLarge,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.displayMedium,
            lineHeight = lineHeightConfig.displayMedium,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Bold,
            fontSize = textConfig.displaySmall,
            lineHeight = lineHeightConfig.displaySmall,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = textConfig.headlineLarge,
            lineHeight = lineHeightConfig.headlineLarge,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = textConfig.headlineMedium,
            lineHeight = lineHeightConfig.headlineMedium,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.SemiBold,
            fontSize = textConfig.headlineSmall,
            lineHeight = lineHeightConfig.headlineSmall,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.titleLarge,
            lineHeight = lineHeightConfig.titleLarge,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.titleMedium,
            lineHeight = lineHeightConfig.titleMedium,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.titleSmall,
            lineHeight = lineHeightConfig.titleSmall,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.bodyLarge,
            lineHeight = lineHeightConfig.bodyLarge,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.bodyMedium,
            lineHeight = lineHeightConfig.bodyMedium,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Normal,
            fontSize = textConfig.bodySmall,
            lineHeight = lineHeightConfig.bodySmall,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.labelLarge,
            lineHeight = lineHeightConfig.labelLarge,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.labelMedium,
            lineHeight = lineHeightConfig.labelMedium,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = figtree,
            fontWeight = FontWeight.Medium,
            fontSize = textConfig.labelSmall,
            lineHeight = lineHeightConfig.labelSmall,
            letterSpacing = 0.5.sp
        )
    )
}

// Main Theme Composable
@Composable
fun SunnahAlHadiTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isSystemDark = isSystemInDarkTheme()

    // Determine if we should use dark theme
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.DYNAMIC -> isSystemDark
    }

    // Determine color scheme
    val colorScheme = when {
        themeMode == ThemeMode.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Create dynamic typography
    val typography = createDynamicTypography(windowSizeClass)



    // Provide the theme with both color scheme and typography
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = {
            // Provide dynamic typography context for content-specific typography
            DynamicTypographyProvider(windowSizeClass = windowSizeClass) {
                content()
            }
        }
    )
}

