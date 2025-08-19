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
import androidx.compose.runtime.staticCompositionLocalOf
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
    inversePrimary = SunnahColors.LightInversePrimary,
    secondary = SunnahColors.LightSecondary,
    onSecondary = SunnahColors.LightOnSecondary,
    secondaryContainer = SunnahColors.LightSecondaryContainer,
    onSecondaryContainer = SunnahColors.LightOnSecondaryContainer,
    tertiary = SunnahColors.LightTertiary,
    onTertiary = SunnahColors.LightOnTertiary,
    tertiaryContainer = SunnahColors.LightTertiaryContainer,
    onTertiaryContainer = SunnahColors.LightOnTertiaryContainer,
    background = SunnahColors.LightBackground,
    onBackground = SunnahColors.LightOnBackground,
    surface = SunnahColors.LightSurface,
    onSurface = SunnahColors.LightOnSurface,
    surfaceVariant = SunnahColors.LightSurfaceVariant,
    onSurfaceVariant = SunnahColors.LightOnSurfaceVariant,
    surfaceTint = SunnahColors.LightSurfaceTint,
    inverseSurface = SunnahColors.LightInverseSurface,
    inverseOnSurface = SunnahColors.LightInverseOnSurface,
    error = SunnahColors.LightError,
    onError = SunnahColors.LightOnError,
    errorContainer = SunnahColors.LightErrorContainer,
    onErrorContainer = SunnahColors.LightOnErrorContainer,
    outline = SunnahColors.LightOutline,
    outlineVariant = SunnahColors.LightOutlineVariant,
    scrim = SunnahColors.LightScrim,
    surfaceContainer = SunnahColors.LightSurfaceContainer,
    surfaceContainerHigh = SunnahColors.LightSurfaceContainerHigh,
    surfaceContainerHighest = SunnahColors.LightSurfaceContainerHighest,
)

// Create dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = SunnahColors.DarkPrimary,
    onPrimary = SunnahColors.DarkOnPrimary,
    primaryContainer = SunnahColors.DarkPrimaryContainer,
    onPrimaryContainer = SunnahColors.DarkOnPrimaryContainer,
    inversePrimary = SunnahColors.DarkInversePrimary,
    secondary = SunnahColors.DarkSecondary,
    onSecondary = SunnahColors.DarkOnSecondary,
    secondaryContainer = SunnahColors.DarkSecondaryContainer,
    onSecondaryContainer = SunnahColors.DarkOnSecondaryContainer,
    tertiary = SunnahColors.DarkTertiary,
    onTertiary = SunnahColors.DarkOnTertiary,
    tertiaryContainer = SunnahColors.DarkTertiaryContainer,
    onTertiaryContainer = SunnahColors.DarkOnTertiaryContainer,
    background = SunnahColors.DarkBackground,
    onBackground = SunnahColors.DarkOnBackground,
    surface = SunnahColors.DarkSurface,
    onSurface = SunnahColors.DarkOnSurface,
    surfaceVariant = SunnahColors.DarkSurfaceVariant,
    onSurfaceVariant = SunnahColors.DarkOnSurfaceVariant,
    surfaceTint = SunnahColors.DarkSurfaceTint,
    inverseSurface = SunnahColors.DarkInverseSurface,
    inverseOnSurface = SunnahColors.DarkInverseOnSurface,
    error = SunnahColors.DarkError,
    onError = SunnahColors.DarkOnError,
    errorContainer = SunnahColors.DarkErrorContainer,
    onErrorContainer = SunnahColors.DarkOnErrorContainer,
    outline = SunnahColors.DarkOutline,
    outlineVariant = SunnahColors.DarkOutlineVariant,
    scrim = SunnahColors.DarkScrim,
    surfaceContainer = SunnahColors.DarkSurfaceContainer,
    surfaceContainerHigh = SunnahColors.DarkSurfaceContainerHigh,
    surfaceContainerHighest = SunnahColors.DarkSurfaceContainerHighest,
)




// CompositionLocal for app typography
val LocalAppTypography = compositionLocalOf<AppTypography> {
    error("No AppTypography provided")
}

val LocalScreenSize = compositionLocalOf<ScreenSize> {
    error("No ScreenSize provided")
}

val LocalDynamicDimensions = staticCompositionLocalOf<DynamicDimensions> {
    error("No DynamicDimensions provided")
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    throw IllegalStateException("No SharedTransitionScope provided")
}

// Extension properties for easy access
val MaterialTheme.appTypography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current

val MaterialTheme.appDimensions: DynamicDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDynamicDimensions.current


// =============================================================================
// UPDATED THEME COMPOSABLE
// =============================================================================

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

    // Create all dimension and typography objects
    val screenSize = remember(windowSizeClass) { windowSizeClass.toScreenSize() }
    val scaleFactors = remember(screenSize) { ScaleFactors.from(screenSize) }
    val typographySizes = remember(scaleFactors) { AppTypographySizes(scaleFactors) }
    val materialTypography = remember(typographySizes) { TypographyFactory.createMaterialTypography(typographySizes) }
    val appTypography = remember(typographySizes) { AppTypography(typographySizes) }

    val baseDimensions = remember(screenSize) { screenSize.toDynamicDimensions() }

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(baseDimensions.cardRadiusS),
        small = RoundedCornerShape(baseDimensions.cardRadiusS),
        medium = RoundedCornerShape(baseDimensions.cardRadiusM),
        large = RoundedCornerShape(baseDimensions.cardRadiusL),
        extraLarge = RoundedCornerShape(baseDimensions.cardRadiusL)
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
                LocalDynamicDimensions provides baseDimensions,
                LocalAppTypography provides appTypography,
                LocalScreenSize provides screenSize,
            ) {
                content()
            }
        }
    }
}

// =============================================================================
// MIGRATION GUIDE - How to update your existing code
// =============================================================================

/*
MIGRATION STEPS:

1. Replace your existing typography calls:
   OLD: MaterialTheme.typography.headlineLarge
   NEW: MaterialTheme.appTypography.pageTitle

2. Replace dimension access:
   OLD: MaterialTheme.dimensions.cardPadding
   NEW: MaterialTheme.appDimensions.cardPaddingM

3. Use screen-specific dimensions:
   OLD: hardcoded values
   NEW: MaterialTheme.homeScreenDimensions.headerTopMargin

4. Typography mapping for your existing code:
   - PAGE TITLE (64sp) → MaterialTheme.appTypography.pageTitle
   - Settings Title (48sp) → MaterialTheme.appTypography.sectionHeader
   - SARFRAZ (40sp) → MaterialTheme.appTypography.cardTitle
   - Featured Topics (32sp) → MaterialTheme.appTypography.sectionHeader
   - See All (24sp) → MaterialTheme.appTypography.buttonLabel
   - Body text (24sp) → MaterialTheme.appTypography.bodyPrimary
   - Arabic large (48sp) → MaterialTheme.appTypography.arabicTitle
   - Arabic medium (32sp) → MaterialTheme.appTypography.arabicBody
   - Arabic small (16sp) → MaterialTheme.appTypography.arabicReference

5. Example usage in composables:
   Text(
       text = "ASSALAMUAILAIKUM",
       style = MaterialTheme.appTypography.pageTitle,
       modifier = Modifier.padding(top = MaterialTheme.homeScreenDimensions.greetingTopMargin)
   )
*/

