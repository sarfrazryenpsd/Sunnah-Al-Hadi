package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R


 val amiri = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold),
    Font(R.font.amiri_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.amiri_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

 val cinzel_decorative = FontFamily(
    Font(R.font.cinzel_decorative_regular, FontWeight.Normal),
    Font(R.font.cinzel_decorative_bold, FontWeight.Bold),
    Font(R.font.cinzel_decorative_black, FontWeight.Black)
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

enum class ScreenSize {
    COMPACT,    // Phones
    MEDIUM,     // Small tablets / Large phones
    EXPANDED    // Large tablets / Desktop
}

data class TypographyScaleFactors(
    val displayScale: Float,
    val headlineScale: Float,
    val titleScale: Float,
    val bodyScale: Float,
    val labelScale: Float,
    val contentScale: Float,
    val arabicScale: Float,
    val lineHeightScale: Float
)

// Set of Material typography styles to start with
// Material 3 Typography using Outfit as the app UI font
object BaseTextSizes {
    // Display sizes
    const val DISPLAY_LARGE = 57f
    const val DISPLAY_MEDIUM = 45f
    const val DISPLAY_SMALL = 36f

    // Headline sizes
    const val HEADLINE_LARGE = 32f
    const val HEADLINE_MEDIUM = 28f
    const val HEADLINE_SMALL = 24f

    // Title sizes
    const val TITLE_LARGE = 22f
    const val TITLE_MEDIUM = 16f
    const val TITLE_SMALL = 14f

    // Body sizes
    const val BODY_LARGE = 16f
    const val BODY_MEDIUM = 14f
    const val BODY_SMALL = 12f

    // Label sizes
    const val LABEL_LARGE = 14f
    const val LABEL_MEDIUM = 12f
    const val LABEL_SMALL = 11f

    // Content specific sizes
    const val SUNNAH_TITLE = 24f
    const val TOPIC_HEADING = 16f
    const val ENGLISH_BODY_NORMAL = 14f
    const val ENGLISH_BODY_TRANSLATION = 18f
    const val REFERENCE = 10f

    // Arabic specific sizes
    const val ARABIC_HONORIFIC = 16f
    const val ARABIC_SUPPLICATION = 24f
    const val ARABIC_VERSE = 24f
    const val ARABIC_OTHER = 16f
}

object BaseLineHeights {
    const val DISPLAY_LARGE = 64f
    const val DISPLAY_MEDIUM = 52f
    const val DISPLAY_SMALL = 44f

    const val HEADLINE_LARGE = 40f
    const val HEADLINE_MEDIUM = 36f
    const val HEADLINE_SMALL = 32f

    const val TITLE_LARGE = 28f
    const val TITLE_MEDIUM = 24f
    const val TITLE_SMALL = 20f

    const val BODY_LARGE = 24f
    const val BODY_MEDIUM = 20f
    const val BODY_SMALL = 16f

    const val LABEL_LARGE = 20f
    const val LABEL_MEDIUM = 16f
    const val LABEL_SMALL = 16f

    // Content specific
    const val SUNNAH_TITLE = 32f
    const val TOPIC_HEADING = 28f
    const val ENGLISH_BODY_NORMAL = 24f
    const val ENGLISH_BODY_TRANSLATION = 26f
    const val REFERENCE = 20f

    // Arabic specific
    const val ARABIC_HONORIFIC = 28f
    const val ARABIC_SUPPLICATION = 36f
    const val ARABIC_VERSE = 36f
    const val ARABIC_OTHER = 32f
}

// Scale factors for different screen sizes
object TypographyScales {
    val COMPACT = TypographyScaleFactors(
        displayScale = 1.0f,
        headlineScale = 1.0f,
        titleScale = 1.0f,
        bodyScale = 1.0f,
        labelScale = 1.0f,
        contentScale = 1.0f,
        arabicScale = 1.0f,
        lineHeightScale = 1.0f
    )

    val MEDIUM = TypographyScaleFactors(
        displayScale = 1.15f,
        headlineScale = 1.1f,
        titleScale = 1.05f,
        bodyScale = 1.1f,
        labelScale = 1.05f,
        contentScale = 1.1f,
        arabicScale = 1.15f, // Arabic text needs more scaling for readability
        lineHeightScale = 1.1f
    )

    val EXPANDED = TypographyScaleFactors(
        displayScale = 1.3f,
        headlineScale = 1.25f,
        titleScale = 1.15f,
        bodyScale = 1.2f,
        labelScale = 1.1f,
        contentScale = 1.25f,
        arabicScale = 1.3f,
        lineHeightScale = 1.15f
    )
}

// Utility function to determine screen size from WindowSizeClass
fun WindowSizeClass.toScreenSize(): ScreenSize {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> ScreenSize.COMPACT
        WindowWidthSizeClass.Medium -> ScreenSize.MEDIUM
        WindowWidthSizeClass.Expanded -> ScreenSize.EXPANDED
        else -> ScreenSize.COMPACT
    }
}

// Get appropriate scale factors based on screen size
fun ScreenSize.getScaleFactors(): TypographyScaleFactors {
    return when (this) {
        ScreenSize.COMPACT -> TypographyScales.COMPACT
        ScreenSize.MEDIUM -> TypographyScales.MEDIUM
        ScreenSize.EXPANDED -> TypographyScales.EXPANDED
    }
}

// Dynamic text size configuration
data class DynamicTextConfig(
    val screenSize: ScreenSize,
    val scaleFactors: TypographyScaleFactors
) {
    // Display sizes
    val displayLarge get() = (BaseTextSizes.DISPLAY_LARGE * scaleFactors.displayScale).sp
    val displayMedium get() = (BaseTextSizes.DISPLAY_MEDIUM * scaleFactors.displayScale).sp
    val displaySmall get() = (BaseTextSizes.DISPLAY_SMALL * scaleFactors.displayScale).sp

    // Headline sizes
    val headlineLarge get() = (BaseTextSizes.HEADLINE_LARGE * scaleFactors.headlineScale).sp
    val headlineMedium get() = (BaseTextSizes.HEADLINE_MEDIUM * scaleFactors.headlineScale).sp
    val headlineSmall get() = (BaseTextSizes.HEADLINE_SMALL * scaleFactors.headlineScale).sp

    // Title sizes
    val titleLarge get() = (BaseTextSizes.TITLE_LARGE * scaleFactors.titleScale).sp
    val titleMedium get() = (BaseTextSizes.TITLE_MEDIUM * scaleFactors.titleScale).sp
    val titleSmall get() = (BaseTextSizes.TITLE_SMALL * scaleFactors.titleScale).sp

    // Body sizes
    val bodyLarge get() = (BaseTextSizes.BODY_LARGE * scaleFactors.bodyScale).sp
    val bodyMedium get() = (BaseTextSizes.BODY_MEDIUM * scaleFactors.bodyScale).sp
    val bodySmall get() = (BaseTextSizes.BODY_SMALL * scaleFactors.bodyScale).sp

    // Label sizes
    val labelLarge get() = (BaseTextSizes.LABEL_LARGE * scaleFactors.labelScale).sp
    val labelMedium get() = (BaseTextSizes.LABEL_MEDIUM * scaleFactors.labelScale).sp
    val labelSmall get() = (BaseTextSizes.LABEL_SMALL * scaleFactors.labelScale).sp

    // Content specific sizes
    val sunnahTitle get() = (BaseTextSizes.SUNNAH_TITLE * scaleFactors.contentScale).sp
    val topicHeading get() = (BaseTextSizes.TOPIC_HEADING * scaleFactors.contentScale).sp
    val englishBodyNormal get() = (BaseTextSizes.ENGLISH_BODY_NORMAL * scaleFactors.contentScale).sp
    val englishBodyTranslation get() = (BaseTextSizes.ENGLISH_BODY_TRANSLATION * scaleFactors.contentScale).sp
    val reference get() = (BaseTextSizes.REFERENCE * scaleFactors.contentScale).sp

    // Arabic specific sizes
    val arabicHonorific get() = (BaseTextSizes.ARABIC_HONORIFIC * scaleFactors.arabicScale).sp
    val arabicSupplication get() = (BaseTextSizes.ARABIC_SUPPLICATION * scaleFactors.arabicScale).sp
    val arabicVerse get() = (BaseTextSizes.ARABIC_VERSE * scaleFactors.arabicScale).sp
    val arabicOther get() = (BaseTextSizes.ARABIC_OTHER * scaleFactors.arabicScale).sp
}

// Dynamic line height configuration
data class DynamicLineHeightConfig(
    val scaleFactors: TypographyScaleFactors
) {
    val displayLarge get() = (BaseLineHeights.DISPLAY_LARGE * scaleFactors.lineHeightScale).sp
    val displayMedium get() = (BaseLineHeights.DISPLAY_MEDIUM * scaleFactors.lineHeightScale).sp
    val displaySmall get() = (BaseLineHeights.DISPLAY_SMALL * scaleFactors.lineHeightScale).sp

    val headlineLarge get() = (BaseLineHeights.HEADLINE_LARGE * scaleFactors.lineHeightScale).sp
    val headlineMedium get() = (BaseLineHeights.HEADLINE_MEDIUM * scaleFactors.lineHeightScale).sp
    val headlineSmall get() = (BaseLineHeights.HEADLINE_SMALL * scaleFactors.lineHeightScale).sp

    val titleLarge get() = (BaseLineHeights.TITLE_LARGE * scaleFactors.lineHeightScale).sp
    val titleMedium get() = (BaseLineHeights.TITLE_MEDIUM * scaleFactors.lineHeightScale).sp
    val titleSmall get() = (BaseLineHeights.TITLE_SMALL * scaleFactors.lineHeightScale).sp

    val bodyLarge get() = (BaseLineHeights.BODY_LARGE * scaleFactors.lineHeightScale).sp
    val bodyMedium get() = (BaseLineHeights.BODY_MEDIUM * scaleFactors.lineHeightScale).sp
    val bodySmall get() = (BaseLineHeights.BODY_SMALL * scaleFactors.lineHeightScale).sp

    val labelLarge get() = (BaseLineHeights.LABEL_LARGE * scaleFactors.lineHeightScale).sp
    val labelMedium get() = (BaseLineHeights.LABEL_MEDIUM * scaleFactors.lineHeightScale).sp
    val labelSmall get() = (BaseLineHeights.LABEL_SMALL * scaleFactors.lineHeightScale).sp

    val sunnahTitle get() = (BaseLineHeights.SUNNAH_TITLE * scaleFactors.lineHeightScale).sp
    val topicHeading get() = (BaseLineHeights.TOPIC_HEADING * scaleFactors.lineHeightScale).sp
    val englishBodyNormal get() = (BaseLineHeights.ENGLISH_BODY_NORMAL * scaleFactors.lineHeightScale).sp
    val englishBodyTranslation get() = (BaseLineHeights.ENGLISH_BODY_TRANSLATION * scaleFactors.lineHeightScale).sp
    val reference get() = (BaseLineHeights.REFERENCE * scaleFactors.lineHeightScale).sp

    val arabicHonorific get() = (BaseLineHeights.ARABIC_HONORIFIC * scaleFactors.lineHeightScale).sp
    val arabicSupplication get() = (BaseLineHeights.ARABIC_SUPPLICATION * scaleFactors.lineHeightScale).sp
    val arabicVerse get() = (BaseLineHeights.ARABIC_VERSE * scaleFactors.lineHeightScale).sp
    val arabicOther get() = (BaseLineHeights.ARABIC_OTHER * scaleFactors.lineHeightScale).sp
}

// CompositionLocal for providing dynamic typography throughout the app
val LocalDynamicTextConfig = compositionLocalOf<DynamicTextConfig> {
    error("DynamicTextConfig not provided")
}

val LocalDynamicLineHeightConfig = compositionLocalOf<DynamicLineHeightConfig> {
    error("DynamicLineHeightConfig not provided")
}


