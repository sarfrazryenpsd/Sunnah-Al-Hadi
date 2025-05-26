package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R


private val amiri = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold),
    Font(R.font.amiri_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.amiri_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

private val cinzel_decorative = FontFamily(
    Font(R.font.cinzel_decorative_regular, FontWeight.Normal),
    Font(R.font.cinzel_decorative_bold, FontWeight.Bold),
    Font(R.font.cinzel_decorative_black, FontWeight.Black)
)

private val cormorant_garamond = FontFamily(
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

private val figtree = FontFamily(
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

private val lateef = FontFamily(
    Font(R.font.lateef_bold, FontWeight.Bold),
    Font(R.font.lateef_extra_bold, FontWeight.ExtraBold),
    Font(R.font.lateef_extra_light, FontWeight.ExtraLight),
    Font(R.font.lateef_light, FontWeight.Light),
    Font(R.font.lateef_medium, FontWeight.Medium),
    Font(R.font.lateef_regular, FontWeight.Normal),
    Font(R.font.lateef_semi_bold, FontWeight.SemiBold),
)

private val mirza = FontFamily(
    Font(R.font.mirza_regular, FontWeight.Normal),
    Font(R.font.mirza_bold, FontWeight.Bold),
    Font(R.font.mirza_semi_bold, FontWeight.SemiBold),
    Font(R.font.mirza_medium, FontWeight.Medium),
)

private val notoNaskhArabic = FontFamily(
    Font(R.font.noto_naskh_arabic_regular, FontWeight.Normal),
    Font(R.font.noto_naskh_arabic_medium, FontWeight.Medium),
    Font(R.font.noto_naskh_arabic_bold, FontWeight.Bold),
    Font(R.font.noto_naskh_arabic_semi_bold, FontWeight.SemiBold),
)

private val notoSerifJP = FontFamily(
    Font(R.font.noto_serif_jp_regular, FontWeight.Normal),
    Font(R.font.noto_serif_jp_bold, FontWeight.Bold),
    Font(R.font.noto_serif_jp_medium, FontWeight.Medium),
    Font(R.font.noto_serif_jp_semi_bold, FontWeight.SemiBold),
    Font(R.font.noto_serif_jp_light, FontWeight.Light),
    Font(R.font.noto_serif_jp_extra_light, FontWeight.ExtraLight),
    Font(R.font.noto_serif_jp_extra_bold, FontWeight.ExtraBold),
    Font(R.font.noto_serif_jp_black, FontWeight.Black),
)

// Set of Material typography styles to start with
// Material 3 Typography using Outfit as the app UI font
object TextSizeConfig {
    // Display sizes
    const val DISPLAY_LARGE = 57
    const val DISPLAY_MEDIUM = 45
    const val DISPLAY_SMALL = 36

    // Headline sizes
    const val HEADLINE_LARGE = 32
    const val HEADLINE_MEDIUM = 28
    const val HEADLINE_SMALL = 24

    // Title sizes
    const val TITLE_LARGE = 22
    const val TITLE_MEDIUM = 16
    const val TITLE_SMALL = 14

    // Body sizes
    const val BODY_LARGE = 16
    const val BODY_MEDIUM = 14
    const val BODY_SMALL = 12

    // Label sizes
    const val LABEL_LARGE = 14
    const val LABEL_MEDIUM = 12
    const val LABEL_SMALL = 11

    // Content specific sizes
    const val SUNNAH_TITLE = 24
    const val TOPIC_HEADING = 16
    const val ENGLISH_BODY_NORMAL = 14
    const val ENGLISH_BODY_TRANSLATION = 18
    const val REFERENCE = 10

    // Arabic specific sizes
    const val ARABIC_HONORIFIC = 16
    const val ARABIC_SUPPLICATION = 18
    const val ARABIC_VERSE = 24
    const val ARABIC_OTHER = 16
}

object LineHeightConfig {
    const val DISPLAY_LARGE = 64
    const val DISPLAY_MEDIUM = 52
    const val DISPLAY_SMALL = 44

    const val HEADLINE_LARGE = 40
    const val HEADLINE_MEDIUM = 36
    const val HEADLINE_SMALL = 32

    const val TITLE_LARGE = 28
    const val TITLE_MEDIUM = 24
    const val TITLE_SMALL = 20

    const val BODY_LARGE = 24
    const val BODY_MEDIUM = 20
    const val BODY_SMALL = 16

    const val LABEL_LARGE = 20
    const val LABEL_MEDIUM = 16
    const val LABEL_SMALL = 16

    // Content specific
    const val SUNNAH_TITLE = 32
    const val TOPIC_HEADING = 28
    const val ENGLISH_BODY_NORMAL = 24
    const val ENGLISH_BODY_TRANSLATION = 26
    const val REFERENCE = 20

    // Arabic specific
    const val ARABIC_HONORIFIC = 28
    const val ARABIC_SUPPLICATION = 36
    const val ARABIC_VERSE = 36
    const val ARABIC_OTHER = 32
}

// Improved Typography with configuration
val AppTypography = Typography(
    // Large title text
    displayLarge = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.DISPLAY_LARGE.sp,
        lineHeight = LineHeightConfig.DISPLAY_LARGE.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.DISPLAY_MEDIUM.sp,
        lineHeight = LineHeightConfig.DISPLAY_MEDIUM.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.DISPLAY_SMALL.sp,
        lineHeight = LineHeightConfig.DISPLAY_SMALL.sp,
        letterSpacing = 0.sp
    ),

    // Section headers
    headlineLarge = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = TextSizeConfig.HEADLINE_LARGE.sp,
        lineHeight = LineHeightConfig.HEADLINE_LARGE.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = TextSizeConfig.HEADLINE_MEDIUM.sp,
        lineHeight = LineHeightConfig.HEADLINE_MEDIUM.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = TextSizeConfig.HEADLINE_SMALL.sp,
        lineHeight = LineHeightConfig.HEADLINE_SMALL.sp,
        letterSpacing = 0.sp
    ),

    // Subsection and card headers
    titleLarge = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.TITLE_LARGE.sp,
        lineHeight = LineHeightConfig.TITLE_LARGE.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.TITLE_MEDIUM.sp,
        lineHeight = LineHeightConfig.TITLE_MEDIUM.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.TITLE_SMALL.sp,
        lineHeight = LineHeightConfig.TITLE_SMALL.sp,
        letterSpacing = 0.1.sp
    ),

    // Body text
    bodyLarge = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Normal,
        fontSize = TextSizeConfig.BODY_LARGE.sp,
        lineHeight = LineHeightConfig.BODY_LARGE.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Normal,
        fontSize = TextSizeConfig.BODY_MEDIUM.sp,
        lineHeight = LineHeightConfig.BODY_MEDIUM.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Normal,
        fontSize = TextSizeConfig.BODY_SMALL.sp,
        lineHeight = LineHeightConfig.BODY_SMALL.sp,
        letterSpacing = 0.4.sp
    ),

    // For buttons and interactive elements
    labelLarge = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.LABEL_LARGE.sp,
        lineHeight = LineHeightConfig.LABEL_LARGE.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.LABEL_MEDIUM.sp,
        lineHeight = LineHeightConfig.LABEL_MEDIUM.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = figtree,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.LABEL_SMALL.sp,
        lineHeight = LineHeightConfig.LABEL_SMALL.sp,
        letterSpacing = 0.5.sp
    )
)

// Improved ContentTypography with configuration
object ContentTypography {
    val sunnahTitle = TextStyle(
        fontFamily = amiri,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.SUNNAH_TITLE.sp,
        lineHeight = LineHeightConfig.SUNNAH_TITLE.sp,
        letterSpacing = 0.sp
    )

    val topicHeading = TextStyle(
        fontFamily = cinzel_decorative,
        fontWeight = FontWeight.Black,
        fontSize = TextSizeConfig.TOPIC_HEADING.sp,
        lineHeight = LineHeightConfig.TOPIC_HEADING.sp,
        letterSpacing = 0.5.sp
    )

    val englishBodyNormal = TextStyle(
        fontFamily = notoSerifJP,
        fontWeight = FontWeight.Normal,
        fontSize = TextSizeConfig.ENGLISH_BODY_NORMAL.sp,
        lineHeight = LineHeightConfig.ENGLISH_BODY_NORMAL.sp,
        letterSpacing = 0.15.sp
    )

    val englishBodyTranslation = TextStyle(
        fontFamily = cormorant_garamond,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        fontSize = TextSizeConfig.ENGLISH_BODY_TRANSLATION.sp,
        lineHeight = LineHeightConfig.ENGLISH_BODY_TRANSLATION.sp,
        letterSpacing = 0.15.sp
    )

    val reference = TextStyle(
        fontFamily = notoSerifJP,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.REFERENCE.sp,
        lineHeight = LineHeightConfig.REFERENCE.sp,
        letterSpacing = 0.25.sp
    )
}

// Improved ArabicTypography with configuration
object ArabicTypography {
    val honorific = TextStyle(
        fontFamily = mirza,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.ARABIC_HONORIFIC.sp,
        lineHeight = LineHeightConfig.ARABIC_HONORIFIC.sp,
        letterSpacing = 0.sp
    )

    val supplication = TextStyle(
        fontFamily = lateef,
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeConfig.ARABIC_SUPPLICATION.sp,
        lineHeight = LineHeightConfig.ARABIC_SUPPLICATION.sp,
        letterSpacing = 0.sp
    )

    val quranicVerse = TextStyle(
        fontFamily = lateef,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.ARABIC_VERSE.sp,
        lineHeight = LineHeightConfig.ARABIC_VERSE.sp,
        letterSpacing = 0.sp
    )

    val other = TextStyle(
        fontFamily = notoNaskhArabic,
        fontWeight = FontWeight.Bold,
        fontSize = TextSizeConfig.ARABIC_OTHER.sp,
        lineHeight = LineHeightConfig.ARABIC_OTHER.sp,
        letterSpacing = 0.sp
    )
}

