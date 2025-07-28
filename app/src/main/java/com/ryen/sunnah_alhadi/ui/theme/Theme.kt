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
import androidx.compose.material3.Typography
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.util.DefaultCategoryGradientProvider
import com.ryen.sunnah_alhadi.presentation.util.LocalCategoryGradients

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


// SINGLE SOURCE OF TRUTH - All typography configurations
object TypographyConfig {

    // Font families (your existing ones)
    val amiri = FontFamily(
        Font(R.font.amiri_regular, FontWeight.Normal),
        Font(R.font.amiri_bold, FontWeight.Bold),
        Font(R.font.amiri_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.amiri_bold_italic, FontWeight.Bold, FontStyle.Italic)
    )

    val lora = FontFamily(
        Font(R.font.lora_regular, FontWeight.Normal),
        Font(R.font.lora_bold, FontWeight.Bold),
        Font(R.font.lora_medium, FontWeight.Medium)
    )

    val cormorant_garamond = FontFamily(
        Font(R.font.cormorant_garamond_bold, FontWeight.Bold),
        Font(R.font.cormorant_garamond_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.cormorant_garamond_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.cormorant_garamond_light, FontWeight.Light),
        Font(R.font.cormorant_garamond_light_italic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.cormorant_garamond_medium, FontWeight.Medium),
        Font(R.font.cormorant_garamond_medium_italic, FontWeight.Medium, FontStyle.Italic),
        Font(R.font.cormorant_garamond_regular, FontWeight.Normal),
        Font(R.font.cormorant_garamond_semi_bold, FontWeight.SemiBold),
        Font(R.font.cormorant_garamond_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic)
    )

    val figtree = FontFamily(
        Font(R.font.figtree_black, FontWeight.Black),
        Font(R.font.figtree_black_italic, FontWeight.Black, FontStyle.Italic),
        Font(R.font.figtree_bold, FontWeight.Bold),
        Font(R.font.figtree_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.figtree_extra_bold, FontWeight.ExtraBold),
        Font(R.font.figtree_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(R.font.figtree_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.figtree_light, FontWeight.Light),
        Font(R.font.figtree_light_italic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.figtree_medium, FontWeight.Medium),
        Font(R.font.figtree_medium_italic, FontWeight.Medium, FontStyle.Italic),
        Font(R.font.figtree_regular, FontWeight.Normal),
        Font(R.font.figtree_semi_bold, FontWeight.SemiBold),
        Font(R.font.figtree_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic)
    )

    val lateef = FontFamily(
        Font(R.font.lateef_bold, FontWeight.Bold),
        Font(R.font.lateef_extra_bold, FontWeight.ExtraBold),
        Font(R.font.lateef_extra_light, FontWeight.ExtraLight),
        Font(R.font.lateef_light, FontWeight.Light),
        Font(R.font.lateef_medium, FontWeight.Medium),
        Font(R.font.lateef_regular, FontWeight.Normal),
        Font(R.font.lateef_semi_bold, FontWeight.SemiBold),
    )

    val mirza = FontFamily(
        Font(R.font.mirza_regular, FontWeight.Normal),
        Font(R.font.mirza_bold, FontWeight.Bold),
        Font(R.font.mirza_semi_bold, FontWeight.SemiBold),
        Font(R.font.mirza_medium, FontWeight.Medium),
    )

    val notoNaskhArabic = FontFamily(
        Font(R.font.noto_naskh_arabic_regular, FontWeight.Normal),
        Font(R.font.noto_naskh_arabic_medium, FontWeight.Medium),
        Font(R.font.noto_naskh_arabic_bold, FontWeight.Bold),
        Font(R.font.noto_naskh_arabic_semi_bold, FontWeight.SemiBold),
    )

    val notoSerifJP = FontFamily(
        Font(R.font.noto_serif_jp_regular, FontWeight.Normal),
        Font(R.font.noto_serif_jp_bold, FontWeight.Bold),
        Font(R.font.noto_serif_jp_medium, FontWeight.Medium),
        Font(R.font.noto_serif_jp_semi_bold, FontWeight.SemiBold),
        Font(R.font.noto_serif_jp_light, FontWeight.Light),
        Font(R.font.noto_serif_jp_extra_light, FontWeight.ExtraLight),
        Font(R.font.noto_serif_jp_extra_bold, FontWeight.ExtraBold),
        Font(R.font.noto_serif_jp_black, FontWeight.Black),
    )

    // Typography categories
    enum class TypographyCategory {
        MATERIAL,    // Material Design 3 standard typography
        CONTENT,     // App-specific content typography
        ARABIC       // Arabic text typography
    }

    // Typography variants
    sealed class TypographyVariant(
        val category: TypographyCategory,
        val fontFamily: FontFamily,
        val fontWeight: FontWeight,
        val fontStyle: FontStyle = FontStyle.Normal,
        val letterSpacingFactor: Float = 0f
    ) {
        // Material Design variants
        object DisplayLarge : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Bold, letterSpacingFactor = -0.25f)
        object DisplayMedium : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Bold)
        object DisplaySmall : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Bold)
        object HeadlineLarge : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.SemiBold)
        object HeadlineMedium : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.SemiBold)
        object HeadlineSmall : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.SemiBold)
        object TitleLarge : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Medium)
        object TitleMedium : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Medium, letterSpacingFactor = 0.15f)
        object TitleSmall : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Medium, letterSpacingFactor = 0.1f)
        object BodyLarge : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Normal, letterSpacingFactor = 0.15f)
        object BodyMedium : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Normal, letterSpacingFactor = 0.25f)
        object BodySmall : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Normal, letterSpacingFactor = 0.4f)
        object LabelLarge : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Medium, letterSpacingFactor = 0.1f)
        object LabelMedium : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Medium, letterSpacingFactor = 0.5f)
        object LabelSmall : TypographyVariant(TypographyCategory.MATERIAL, figtree, FontWeight.Medium, letterSpacingFactor = 0.5f)

        // Content variants
        object SunnahTitle : TypographyVariant(TypographyCategory.CONTENT, amiri, FontWeight.Bold)
        object TopicHeading : TypographyVariant(TypographyCategory.CONTENT, lora, FontWeight.Black, letterSpacingFactor = 0.5f)
        object EnglishBodyNormal : TypographyVariant(TypographyCategory.CONTENT, notoSerifJP, FontWeight.Normal, letterSpacingFactor = 0.15f)
        object EnglishBodyTranslation : TypographyVariant(TypographyCategory.CONTENT, cormorant_garamond, FontWeight.SemiBold, FontStyle.Italic, 0.15f)
        object Reference : TypographyVariant(TypographyCategory.CONTENT, notoSerifJP, FontWeight.Medium, letterSpacingFactor = 0.25f)

        // Arabic variants
        object ArabicHonorific : TypographyVariant(TypographyCategory.ARABIC, mirza, FontWeight.Bold)
        object ArabicSupplication : TypographyVariant(TypographyCategory.ARABIC, lateef, FontWeight.Medium)
        object ArabicVerse : TypographyVariant(TypographyCategory.ARABIC, lateef, FontWeight.Bold)
        object ArabicOther : TypographyVariant(TypographyCategory.ARABIC, notoNaskhArabic, FontWeight.Bold)
    }
}

// Dynamic scaling configuration
data class ScaleFactors(
    val textScale: Float,
    val lineHeightScale: Float
) {
    companion object {
        fun from(screenSize: ScreenSize): ScaleFactors {
            return when (screenSize) {
                ScreenSize.COMPACT -> ScaleFactors(0.9f, 0.9f)
                ScreenSize.MEDIUM -> ScaleFactors(1.0f, 1.0f)
                ScreenSize.EXPANDED -> ScaleFactors(1.2f, 1.2f)
            }
        }
    }
}

// Typography size definitions (your existing logic)
class DynamicSizes(private val scaleFactors: ScaleFactors) {
    // Material sizes
    val displayLarge = (57 * scaleFactors.textScale).sp
    val displayMedium = (45 * scaleFactors.textScale).sp
    val displaySmall = (36 * scaleFactors.textScale).sp
    val headlineLarge = (32 * scaleFactors.textScale).sp
    val headlineMedium = (28 * scaleFactors.textScale).sp
    val headlineSmall = (24 * scaleFactors.textScale).sp
    val titleLarge = (22 * scaleFactors.textScale).sp
    val titleMedium = (16 * scaleFactors.textScale).sp
    val titleSmall = (14 * scaleFactors.textScale).sp
    val bodyLarge = (16 * scaleFactors.textScale).sp
    val bodyMedium = (14 * scaleFactors.textScale).sp
    val bodySmall = (12 * scaleFactors.textScale).sp
    val labelLarge = (14 * scaleFactors.textScale).sp
    val labelMedium = (12 * scaleFactors.textScale).sp
    val labelSmall = (11 * scaleFactors.textScale).sp

    // Content sizes
    val sunnahTitle = (20 * scaleFactors.textScale).sp
    val topicHeading = (18 * scaleFactors.textScale).sp
    val englishBodyNormal = (16 * scaleFactors.textScale).sp
    val englishBodyTranslation = (15 * scaleFactors.textScale).sp
    val reference = (13 * scaleFactors.textScale).sp

    // Arabic sizes
    val arabicHonorific = (16 * scaleFactors.textScale).sp
    val arabicSupplication = (18 * scaleFactors.textScale).sp
    val arabicVerse = (20 * scaleFactors.textScale).sp
    val arabicOther = (16 * scaleFactors.textScale).sp

    // Line heights
    val displayLargeLineHeight = (64 * scaleFactors.lineHeightScale).sp
    val displayMediumLineHeight = (52 * scaleFactors.lineHeightScale).sp
    val displaySmallLineHeight = (44 * scaleFactors.lineHeightScale).sp
    val headlineLargeLineHeight = (40 * scaleFactors.lineHeightScale).sp
    val headlineMediumLineHeight = (36 * scaleFactors.lineHeightScale).sp
    val headlineSmallLineHeight = (32 * scaleFactors.lineHeightScale).sp
    val titleLargeLineHeight = (28 * scaleFactors.lineHeightScale).sp
    val titleMediumLineHeight = (24 * scaleFactors.lineHeightScale).sp
    val titleSmallLineHeight = (20 * scaleFactors.lineHeightScale).sp
    val bodyLargeLineHeight = (24 * scaleFactors.lineHeightScale).sp
    val bodyMediumLineHeight = (20 * scaleFactors.lineHeightScale).sp
    val bodySmallLineHeight = (16 * scaleFactors.lineHeightScale).sp
    val labelLargeLineHeight = (20 * scaleFactors.lineHeightScale).sp
    val labelMediumLineHeight = (16 * scaleFactors.lineHeightScale).sp
    val labelSmallLineHeight = (16 * scaleFactors.lineHeightScale).sp

    val sunnahTitleLineHeight = (28 * scaleFactors.lineHeightScale).sp
    val topicHeadingLineHeight = (26 * scaleFactors.lineHeightScale).sp
    val englishBodyNormalLineHeight = (24 * scaleFactors.lineHeightScale).sp
    val englishBodyTranslationLineHeight = (23 * scaleFactors.lineHeightScale).sp
    val referenceLineHeight = (19 * scaleFactors.lineHeightScale).sp
    val arabicHonorificLineHeight = (24 * scaleFactors.lineHeightScale).sp
    val arabicSupplicationLineHeight = (26 * scaleFactors.lineHeightScale).sp
    val arabicVerseLineHeight = (28 * scaleFactors.lineHeightScale).sp
    val arabicOtherLineHeight = (24 * scaleFactors.lineHeightScale).sp
}

// CENTRAL TYPOGRAPHY FACTORY - Single place to create all TextStyles
object TypographyFactory {

    fun createTextStyle(
        variant: TypographyConfig.TypographyVariant,
        sizes: DynamicSizes
    ): TextStyle {
        val (fontSize, lineHeight) = when (variant) {
            TypographyConfig.TypographyVariant.DisplayLarge -> sizes.displayLarge to sizes.displayLargeLineHeight
            TypographyConfig.TypographyVariant.DisplayMedium -> sizes.displayMedium to sizes.displayMediumLineHeight
            TypographyConfig.TypographyVariant.DisplaySmall -> sizes.displaySmall to sizes.displaySmallLineHeight
            TypographyConfig.TypographyVariant.HeadlineLarge -> sizes.headlineLarge to sizes.headlineLargeLineHeight
            TypographyConfig.TypographyVariant.HeadlineMedium -> sizes.headlineMedium to sizes.headlineMediumLineHeight
            TypographyConfig.TypographyVariant.HeadlineSmall -> sizes.headlineSmall to sizes.headlineSmallLineHeight
            TypographyConfig.TypographyVariant.TitleLarge -> sizes.titleLarge to sizes.titleLargeLineHeight
            TypographyConfig.TypographyVariant.TitleMedium -> sizes.titleMedium to sizes.titleMediumLineHeight
            TypographyConfig.TypographyVariant.TitleSmall -> sizes.titleSmall to sizes.titleSmallLineHeight
            TypographyConfig.TypographyVariant.BodyLarge -> sizes.bodyLarge to sizes.bodyLargeLineHeight
            TypographyConfig.TypographyVariant.BodyMedium -> sizes.bodyMedium to sizes.bodyMediumLineHeight
            TypographyConfig.TypographyVariant.BodySmall -> sizes.bodySmall to sizes.bodySmallLineHeight
            TypographyConfig.TypographyVariant.LabelLarge -> sizes.labelLarge to sizes.labelLargeLineHeight
            TypographyConfig.TypographyVariant.LabelMedium -> sizes.labelMedium to sizes.labelMediumLineHeight
            TypographyConfig.TypographyVariant.LabelSmall -> sizes.labelSmall to sizes.labelSmallLineHeight
            TypographyConfig.TypographyVariant.SunnahTitle -> sizes.sunnahTitle to sizes.sunnahTitleLineHeight
            TypographyConfig.TypographyVariant.TopicHeading -> sizes.topicHeading to sizes.topicHeadingLineHeight
            TypographyConfig.TypographyVariant.EnglishBodyNormal -> sizes.englishBodyNormal to sizes.englishBodyNormalLineHeight
            TypographyConfig.TypographyVariant.EnglishBodyTranslation -> sizes.englishBodyTranslation to sizes.englishBodyTranslationLineHeight
            TypographyConfig.TypographyVariant.Reference -> sizes.reference to sizes.referenceLineHeight
            TypographyConfig.TypographyVariant.ArabicHonorific -> sizes.arabicHonorific to sizes.arabicHonorificLineHeight
            TypographyConfig.TypographyVariant.ArabicSupplication -> sizes.arabicSupplication to sizes.arabicSupplicationLineHeight
            TypographyConfig.TypographyVariant.ArabicVerse -> sizes.arabicVerse to sizes.arabicVerseLineHeight
            TypographyConfig.TypographyVariant.ArabicOther -> sizes.arabicOther to sizes.arabicOtherLineHeight
        }

        return TextStyle(
            fontFamily = variant.fontFamily,
            fontWeight = variant.fontWeight,
            fontStyle = variant.fontStyle,
            fontSize = fontSize,
            lineHeight = lineHeight,
            letterSpacing = (variant.letterSpacingFactor).sp
        )
    }

    fun createMaterialTypography(sizes: DynamicSizes): Typography {
        return Typography(
            displayLarge = createTextStyle(TypographyConfig.TypographyVariant.DisplayLarge, sizes),
            displayMedium = createTextStyle(TypographyConfig.TypographyVariant.DisplayMedium, sizes),
            displaySmall = createTextStyle(TypographyConfig.TypographyVariant.DisplaySmall, sizes),
            headlineLarge = createTextStyle(TypographyConfig.TypographyVariant.HeadlineLarge, sizes),
            headlineMedium = createTextStyle(TypographyConfig.TypographyVariant.HeadlineMedium, sizes),
            headlineSmall = createTextStyle(TypographyConfig.TypographyVariant.HeadlineSmall, sizes),
            titleLarge = createTextStyle(TypographyConfig.TypographyVariant.TitleLarge, sizes),
            titleMedium = createTextStyle(TypographyConfig.TypographyVariant.TitleMedium, sizes),
            titleSmall = createTextStyle(TypographyConfig.TypographyVariant.TitleSmall, sizes),
            bodyLarge = createTextStyle(TypographyConfig.TypographyVariant.BodyLarge, sizes),
            bodyMedium = createTextStyle(TypographyConfig.TypographyVariant.BodyMedium, sizes),
            bodySmall = createTextStyle(TypographyConfig.TypographyVariant.BodySmall, sizes),
            labelLarge = createTextStyle(TypographyConfig.TypographyVariant.LabelLarge, sizes),
            labelMedium = createTextStyle(TypographyConfig.TypographyVariant.LabelMedium, sizes),
            labelSmall = createTextStyle(TypographyConfig.TypographyVariant.LabelSmall, sizes)
        )
    }
}

// Centralized typography access
class AppTypography internal constructor(private val sizes: DynamicSizes) {

    // Material Design styles - use MaterialTheme.typography instead
    // These are kept for backward compatibility during migration

    // Content styles - your app-specific typography
    val sunnahTitle: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.SunnahTitle, sizes)
    val topicHeading: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.TopicHeading, sizes)
    val englishBodyNormal: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.EnglishBodyNormal, sizes)
    val englishBodyTranslation: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.EnglishBodyTranslation, sizes)
    val reference: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.Reference, sizes)

    // Arabic styles
    val arabicHonorific: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.ArabicHonorific, sizes)
    val arabicSupplication: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.ArabicSupplication, sizes)
    val arabicVerse: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.ArabicVerse, sizes)
    val arabicOther: TextStyle = TypographyFactory.createTextStyle(TypographyConfig.TypographyVariant.ArabicOther, sizes)
}

// CompositionLocal for app typography
val LocalAppTypography = compositionLocalOf<AppTypography> {
    error("No AppTypography provided")
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
                LocalCategoryGradients provides DefaultCategoryGradientProvider,
                LocalAppTypography provides appTypography
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

