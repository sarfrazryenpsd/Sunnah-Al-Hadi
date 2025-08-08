package com.ryen.sunnah_alhadi.ui.theme

import android.os.Build
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Theme preferences
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
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




// CompositionLocal for app typography
val LocalAppTypography = compositionLocalOf<AppTypography> {
    error("No AppTypography provided")
}

val LocalScreenSize = compositionLocalOf<ScreenSize> {
    error("No ScreenSize provided")
}

// Extension for easy access
val MaterialTheme.appTypography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw IllegalStateException("No SharedTransitionScope provided")
}

// UPDATED THEME COMPOSABLE - Simplified and centralized
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SunnahAlHadiTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    isDynamicColorEnabled: Boolean = false,
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemDark
    }

    val dynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        isDynamicColorEnabled && dynamicColorAvailable -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Create typography once
    val screenSize = remember(windowSizeClass) { windowSizeClass.toScreenSize() }
    val scaleFactors = remember(screenSize) { ScaleFactors.from(screenSize) }
    val dynamicSizes = remember(scaleFactors) { DynamicSizes(scaleFactors) }
    val materialTypography = remember(dynamicSizes) { TypographyFactory.createMaterialTypography(dynamicSizes) }
    val appTypography = remember(dynamicSizes) { AppTypography(dynamicSizes) }

    val dimensions = remember(screenSize) { screenSize.toDynamicDimensions() }
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(50),
        large = RoundedCornerShape(size = 30.dp),
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = materialTypography,
        shapes = shapes,
        motionScheme = MotionScheme.expressive()
    ) {
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
                LocalDynamicDimensions provides dimensions,
                LocalAppTypography provides appTypography,
                LocalScreenSize provides screenSize
            ) {
                content()
            }
        }
    }
}

// Usage examples:
/*
@Composable
fun ExampleUsage() {
    // For Material Design components - use MaterialTheme.typography
    Text(
        text = "Material Design Title",
        style = MaterialTheme.typography.titleLarge
    )

    // For app-specific content - use MaterialTheme.appTypography
    Text(
        text = "Sunnah Title",
        style = MaterialTheme.appTypography.sunnahTitle
    )

    Text(
        text = "Arabic text",
        style = MaterialTheme.appTypography.arabicVerse
    )
}
*/

