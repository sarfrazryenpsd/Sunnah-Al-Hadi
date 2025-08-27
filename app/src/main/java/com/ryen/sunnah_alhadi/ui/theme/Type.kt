package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R

enum class ScreenSize {
    COMPACT,    // Phones
    MEDIUM,     // Small tablets / Large phones
    EXPANDED    // Large tablets / Desktop (two-pane)
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

// =============================================================================
// TYPOGRAPHY SYSTEM - SIMPLIFIED FOR TWO FONTS
// =============================================================================

object TypographyConfig {

    // Font families - Only Figtree and Amiri
    val figtree = FontFamily(
        Font(R.font.figtree_regular, FontWeight.Normal),
        Font(R.font.figtree_medium, FontWeight.Medium),
        Font(R.font.figtree_semi_bold, FontWeight.SemiBold),
        Font(R.font.figtree_bold, FontWeight.Bold),
        Font(R.font.figtree_light, FontWeight.Light)
    )

    val amiri = FontFamily(
        Font(R.font.amiri_regular, FontWeight.Normal),
        Font(R.font.amiri_regular, FontWeight.Light),
        Font(R.font.amiri_bold, FontWeight.Bold)
    )

    // Typography roles - Simplified for production
    enum class TextRole {
        // UI Text (Figtree)
        DISPLAY,        // Page titles
        HEADLINE,       // Section headers
        TITLE,          // Card titles, button labels
        BODY,           // Regular content
        LABEL,          // Small labels, captions

        // Arabic Text (Amiri)
        ARABIC_LARGE,   // Main Arabic content
        ARABIC_MEDIUM,  // Secondary Arabic content
        ARABIC_SMALL    // References, citations
    }

    enum class TextVariant {
        LARGE,
        MEDIUM,
        SMALL
    }
}

// Simplified scaling - minimal for expanded screens since you use two-pane
data class ScaleFactors(
    val textScale: Float,
    val spacingScale: Float
) {
    companion object {
        fun from(screenSize: ScreenSize): ScaleFactors {
            return when (screenSize) {
                ScreenSize.COMPACT -> ScaleFactors(1.0f, 1.0f)
                ScreenSize.MEDIUM -> ScaleFactors(1.0f, 1.0f)
                ScreenSize.EXPANDED -> ScaleFactors(1.0f, 1.1f) // Minimal scaling for two-pane
            }
        }
    }
}

// Corrected typography sizes (halved from your current values)
class AppTypographySizes(private val scaleFactors: ScaleFactors) {

    // UI Typography (Figtree) - Fixed sp values based on Material Design
    val display = (32 * scaleFactors.textScale).sp      // Page titles (was 64)
    val headline = (24 * scaleFactors.textScale).sp     // Section headers (was 48)
    val titleLarge = (20 * scaleFactors.textScale).sp   // Main titles (was 40)
    val titleMedium = (18 * scaleFactors.textScale).sp  // Card titles (was 36)
    val titleSmall = (16 * scaleFactors.textScale).sp   // Button labels (was 32)
    val bodyLarge = (16 * scaleFactors.textScale).sp    // Main content (was 32)
    val bodyMedium = (14 * scaleFactors.textScale).sp   // Secondary content (was 28)
    val bodySmall = (12 * scaleFactors.textScale).sp    // Small content (was 24)
    val labelLarge = (14 * scaleFactors.textScale).sp   // Tabs (was 28)
    val labelMedium = (12 * scaleFactors.textScale).sp  // Small labels (was 24)
    val labelSmall = (10 * scaleFactors.textScale).sp   // Captions (was 20)
    val displayName = (28 * scaleFactors.textScale).sp    // SARFRAZ display name

    val searchPlaceholder = (14 * scaleFactors.textScale).sp  // Search placeholder

    val browseTitle = (44 * scaleFactors.textScale).sp        // Browse section titles
    val sunnahCardTitle = (20 * scaleFactors.textScale).sp       // Sunnah card titles
    val featuredTopics = (22 * scaleFactors.textScale).sp        // "Featured Topics" text
    val browseSubtitle = (22 * scaleFactors.textScale).sp        // Browse section subtitles

    val settings = (30 * scaleFactors.textScale).sp               // Settings text
    val seeAll = (14 * scaleFactors.textScale).sp               // "See All" links
    val notificationType = (16 * scaleFactors.textScale).sp     // Notification type headers
    val extraNotificationTitle = (16 * scaleFactors.textScale).sp // Extra & notification titles
    val sunnahDetail = (14 * scaleFactors.textScale).sp         // Sunnah detail text
    val reminderTime = (14 * scaleFactors.textScale).sp         // Reminder time text
    val tabs = (12 * scaleFactors.textScale).sp                 // Tab labels
    val filters = (14 * scaleFactors.textScale).sp // Topic subtitle & filters
    val sunnahSubtitle = (10 * scaleFactors.textScale).sp // Sunnah subtitles
    val notificationSubtitle = (12 * scaleFactors.textScale).sp // Notification subtitles

    // Arabic specific sizes
    val topicMax = (32 * scaleFactors.textScale).sp             // Topic max Arabic text
    val topicMin = (22 * scaleFactors.textScale).sp             // Topic min Arabic text
    val homeSunnahTitle = (28 * scaleFactors.textScale).sp      // Home Sunnah title
    val homeSalat = (16 * scaleFactors.textScale).sp            // Home Salat text
    val homeSunnahHeading = (14 * scaleFactors.textScale).sp    // Home Sunnah heading
    val homeSunnahDetail = (16 * scaleFactors.textScale).sp     // Home Sunnah detail
    val sunnahReference = (10 * scaleFactors.textScale).sp      // Sunnah reference text

    // Arabic Typography (Amiri)
    val arabicLarge = (20 * scaleFactors.textScale).sp  // Main Arabic text (was 40)
    val arabicMedium = (12 * scaleFactors.textScale).sp // Secondary Arabic (was 32)
    val arabicSmall = (12 * scaleFactors.textScale).sp  // References (was 24)

    // Line heights (1.4x font size for readability)
    val displayLineHeight = (45 * scaleFactors.textScale).sp
    val headlineLineHeight = (34 * scaleFactors.textScale).sp
    val titleLargeLineHeight = (28 * scaleFactors.textScale).sp
    val titleMediumLineHeight = (25 * scaleFactors.textScale).sp
    val titleSmallLineHeight = (22 * scaleFactors.textScale).sp
    val bodyLargeLineHeight = (22 * scaleFactors.textScale).sp
    val bodyMediumLineHeight = (20 * scaleFactors.textScale).sp
    val bodySmallLineHeight = (17 * scaleFactors.textScale).sp
    val labelLargeLineHeight = (20 * scaleFactors.textScale).sp
    val labelMediumLineHeight = (17 * scaleFactors.textScale).sp
    val labelSmallLineHeight = (14 * scaleFactors.textScale).sp
    val arabicLargeLineHeight = (28 * scaleFactors.textScale).sp
    val arabicMediumLineHeight = (22 * scaleFactors.textScale).sp
    val arabicSmallLineHeight = (17 * scaleFactors.textScale).sp
}

// Typography factory - Simplified
object TypographyFactory {

    fun createTextStyle(
        role: TypographyConfig.TextRole,
        sizes: AppTypographySizes,
        fontWeight: FontWeight = FontWeight.Normal,
        variant: TypographyConfig.TextVariant = TypographyConfig.TextVariant.MEDIUM  // New parameter, defaults to MEDIUM
    ): TextStyle {

        val (fontFamily, fontSize, lineHeight, letterSpacing) = when (role) {
            TypographyConfig.TextRole.DISPLAY -> listOf(
                TypographyConfig.figtree,
                sizes.display,
                sizes.displayLineHeight,
                (-0.5).sp
            )
            TypographyConfig.TextRole.HEADLINE -> listOf(
                TypographyConfig.figtree,
                sizes.headline,
                sizes.headlineLineHeight,
                0.sp
            )
            TypographyConfig.TextRole.TITLE -> when (variant) {  // Branch on variant
                TypographyConfig.TextVariant.LARGE -> listOf(TypographyConfig.figtree, sizes.titleLarge, sizes.titleLargeLineHeight, 0.sp)
                TypographyConfig.TextVariant.MEDIUM -> listOf(TypographyConfig.figtree, sizes.titleMedium, sizes.titleMediumLineHeight, 0.1.sp)
                TypographyConfig.TextVariant.SMALL -> listOf(TypographyConfig.figtree, sizes.titleSmall, sizes.titleSmallLineHeight, 0.1.sp)
            }
            TypographyConfig.TextRole.BODY -> when (variant) {
                TypographyConfig.TextVariant.LARGE -> listOf(TypographyConfig.figtree, sizes.bodyLarge, sizes.bodyLargeLineHeight, 0.15.sp)
                TypographyConfig.TextVariant.MEDIUM -> listOf(TypographyConfig.figtree, sizes.bodyMedium, sizes.bodyMediumLineHeight, 0.25.sp)
                TypographyConfig.TextVariant.SMALL -> listOf(TypographyConfig.figtree, sizes.bodySmall, sizes.bodySmallLineHeight, 0.4.sp)
            }
            TypographyConfig.TextRole.LABEL -> when (variant) {
                TypographyConfig.TextVariant.LARGE -> listOf(TypographyConfig.figtree, sizes.labelLarge, sizes.labelLargeLineHeight, 0.1.sp)
                TypographyConfig.TextVariant.MEDIUM -> listOf(TypographyConfig.figtree, sizes.labelMedium, sizes.labelMediumLineHeight, 0.5.sp)
                TypographyConfig.TextVariant.SMALL -> listOf(TypographyConfig.figtree, sizes.labelSmall, sizes.labelSmallLineHeight, 0.5.sp)
            }
            TypographyConfig.TextRole.ARABIC_LARGE -> listOf(
                TypographyConfig.amiri,
                sizes.arabicLarge,
                sizes.arabicLargeLineHeight,
                0.sp
            )
            TypographyConfig.TextRole.ARABIC_MEDIUM -> listOf(
                TypographyConfig.amiri,
                sizes.arabicMedium,
                sizes.arabicMediumLineHeight,
                0.sp
            )
            TypographyConfig.TextRole.ARABIC_SMALL -> listOf(
                TypographyConfig.amiri,
                sizes.arabicSmall,
                sizes.arabicSmallLineHeight,
                0.sp
            )
        }

        return TextStyle(
            fontFamily = fontFamily as FontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize as TextUnit,
            lineHeight = lineHeight as TextUnit,
            letterSpacing = letterSpacing as TextUnit
        )
    }

    fun createMaterialTypography(sizes: AppTypographySizes): Typography {
        return Typography(
            displayLarge = createTextStyle(TypographyConfig.TextRole.DISPLAY, sizes, FontWeight.Bold),
            displayMedium = createTextStyle(TypographyConfig.TextRole.DISPLAY, sizes, FontWeight.Bold),
            displaySmall = createTextStyle(TypographyConfig.TextRole.DISPLAY, sizes, FontWeight.Bold),
            headlineLarge = createTextStyle(TypographyConfig.TextRole.HEADLINE, sizes, FontWeight.SemiBold),
            headlineMedium = createTextStyle(TypographyConfig.TextRole.HEADLINE, sizes, FontWeight.SemiBold),
            headlineSmall = createTextStyle(TypographyConfig.TextRole.HEADLINE, sizes, FontWeight.SemiBold),
            titleLarge = createTextStyle(TypographyConfig.TextRole.TITLE, sizes, FontWeight.Medium, TypographyConfig.TextVariant.LARGE),  // Pass variant
            titleMedium = createTextStyle(TypographyConfig.TextRole.TITLE, sizes, FontWeight.Medium, TypographyConfig.TextVariant.MEDIUM),
            titleSmall = createTextStyle(TypographyConfig.TextRole.TITLE, sizes, FontWeight.Medium, TypographyConfig.TextVariant.SMALL),
            bodyLarge = createTextStyle(TypographyConfig.TextRole.BODY, sizes, FontWeight.Normal, TypographyConfig.TextVariant.LARGE),
            bodyMedium = createTextStyle(TypographyConfig.TextRole.BODY, sizes, FontWeight.Normal, TypographyConfig.TextVariant.MEDIUM),
            bodySmall = createTextStyle(TypographyConfig.TextRole.BODY, sizes, FontWeight.Normal, TypographyConfig.TextVariant.SMALL),
            labelLarge = createTextStyle(TypographyConfig.TextRole.LABEL, sizes, FontWeight.Medium, TypographyConfig.TextVariant.LARGE),
            labelMedium = createTextStyle(TypographyConfig.TextRole.LABEL, sizes, FontWeight.Medium, TypographyConfig.TextVariant.MEDIUM),
            labelSmall = createTextStyle(TypographyConfig.TextRole.LABEL, sizes, FontWeight.Medium, TypographyConfig.TextVariant.SMALL)
        )
    }
}

// =============================================================================
// SIMPLIFIED APP TYPOGRAPHY
// =============================================================================

class AppTypography internal constructor(private val sizes: AppTypographySizes) {

    // Add these specific typography styles to your AppTypography class

    // Figtree specific styles
    val displayName: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.TITLE, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.displayName)

    val sunnahCardTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.TITLE, sizes, FontWeight.SemiBold
    ).copy(fontSize = sizes.sunnahCardTitle)

    val featuredTopics: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.TITLE, sizes, FontWeight.SemiBold
    ).copy(fontSize = sizes.featuredTopics)

    val browseSubtitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Medium
    ).copy(fontSize = sizes.browseSubtitle)

    val browseTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.TITLE, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.browseTitle)

    val topicsSubtitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Medium
    ).copy(fontSize = sizes.tabs)

    val seeAll: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.seeAll)

    val searchPlaceHolder: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.Normal
    ).copy(fontSize = sizes.searchPlaceholder)

    val settings: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.settings)

    val sunnahCompactCardTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.SemiBold
    ).copy(fontSize = sizes.seeAll)

    val sunnahSubtitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.Normal
    ).copy(fontSize = sizes.sunnahSubtitle)

    val notificationType: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.SemiBold
    ).copy(fontSize = sizes.notificationType)

    val extraAndNotificationTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Medium
    ).copy(fontSize = sizes.extraNotificationTitle)

    val sunnahDetail: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Normal
    ).copy(fontSize = sizes.sunnahDetail)

    val reminderTime: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Light,
    ).copy(fontSize = sizes.reminderTime, fontStyle = FontStyle.Italic)

    val tabs: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.tabs)

    val topicSubtitleFilters: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.SemiBold
    ).copy(fontSize = sizes.filters)

    val notificationSubtitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.Light
    ).copy(fontSize = sizes.notificationSubtitle)

    // Amiri specific styles
    val topicMax: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_LARGE, sizes, FontWeight.Normal
    ).copy(fontSize = sizes.topicMax)

    val topicMin: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_MEDIUM, sizes, FontWeight.Normal
    ).copy(fontSize = sizes.topicMin)

    val homeSunnahTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_MEDIUM, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.homeSunnahTitle)

    val homeSalat: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_MEDIUM, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.homeSalat)

    val homeSunnahHeading: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_MEDIUM, sizes, FontWeight.Bold
    ).copy(fontSize = sizes.homeSunnahHeading)

    val homeSunnahDetail: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_SMALL, sizes, FontWeight.Normal
    ).copy(fontSize = sizes.homeSunnahDetail, fontFamily = TypographyConfig.amiri)

    val sunnahReference: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_SMALL, sizes, FontWeight.Light
    ).copy(fontSize = sizes.sunnahReference)


    // Page-level typography
    val pageTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.DISPLAY, sizes, FontWeight.Bold
    )

    // Section headers
    val sectionHeader: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.HEADLINE, sizes, FontWeight.SemiBold
    )

    // Card titles and important labels
    val cardTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.TITLE, sizes, FontWeight.SemiBold
    )

    // Button labels and navigation
    val buttonLabel: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.TITLE, sizes, FontWeight.Medium
    )

    // Main content body
    val bodyPrimary: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Normal
    )

    // Secondary content
    val bodySecondary: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.BODY, sizes, FontWeight.Normal
    )

    // Small labels and captions
    val caption: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.LABEL, sizes, FontWeight.Medium
    )

    // Arabic typography
    val arabicTitle: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_LARGE, sizes, FontWeight.Bold
    )

    val arabicBody: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_MEDIUM, sizes, FontWeight.Normal
    )

    val arabicReference: TextStyle = TypographyFactory.createTextStyle(
        TypographyConfig.TextRole.ARABIC_SMALL, sizes, FontWeight.Normal
    )
}



