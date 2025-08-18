package com.ryen.sunnah_alhadi.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val LocalDynamicDimensions = staticCompositionLocalOf<DynamicDimensions> {
    error("No DynamicDimensions provided")
}

data class DynamicDimensions(
    // Base spacing (8dp grid system)
    val spacingXs: Dp,      // 4dp
    val spacingS: Dp,       // 8dp
    val spacingM: Dp,       // 16dp
    val spacingL: Dp,       // 24dp
    val spacingXl: Dp,      // 32dp
    val spacingXxl: Dp,     // 40dp

    // Screen layout
    val screenPadding: Dp,
    val sectionSpacing: Dp,

    // Cards and containers
    val cardPaddingS: Dp,
    val cardPaddingM: Dp,
    val cardPaddingL: Dp,
    val cardSpacing: Dp,
    val cardRadiusS: Dp,
    val cardRadiusM: Dp,
    val cardRadiusL: Dp,

    // Component sizes
    val iconS: Dp,
    val iconM: Dp,
    val iconL: Dp,
    val buttonHeight: Dp,
    val touchTarget: Dp,

    // Specific card dimensions
    val featuredCardWidth: Dp,
    val featuredCardHeight: Dp,
    val topicCardHeight: Dp,
    val listItemHeight: Dp,
    val compactItemHeight: Dp,

    // Search and navigation
    val searchBarHeight: Dp,
    val tabHeight: Dp,
    val bottomNavHeight: Dp,

    // Modal and overlays
    val modalPadding: Dp,
    val modalRadius: Dp,
    val modalMaxWidth: Dp
)

fun ScreenSize.toDynamicDimensions(): DynamicDimensions = when (this) {
    ScreenSize.COMPACT -> DynamicDimensions(
        // Base spacing
        spacingXs = 4.dp,
        spacingS = 8.dp,
        spacingM = 16.dp,
        spacingL = 24.dp,
        spacingXl = 32.dp,
        spacingXxl = 40.dp,

        // Screen layout
        screenPadding = 16.dp,
        sectionSpacing = 32.dp,

        // Cards
        cardPaddingS = 12.dp,
        cardPaddingM = 16.dp,
        cardPaddingL = 20.dp,
        cardSpacing = 8.dp,
        cardRadiusS = 12.dp,
        cardRadiusM = 16.dp,
        cardRadiusL = 20.dp,

        // Components
        iconS = 16.dp,
        iconM = 24.dp,
        iconL = 32.dp,
        buttonHeight = 48.dp,
        touchTarget = 48.dp,

        // Specific cards
        featuredCardWidth = 280.dp,
        featuredCardHeight = 140.dp,
        topicCardHeight = 160.dp,
        listItemHeight = 64.dp,
        compactItemHeight = 48.dp,

        // Navigation
        searchBarHeight = 48.dp,
        tabHeight = 40.dp,
        bottomNavHeight = 56.dp,

        // Modals
        modalPadding = 24.dp,
        modalRadius = 24.dp,
        modalMaxWidth = 320.dp
    )

    ScreenSize.MEDIUM -> DynamicDimensions(
        // Base spacing
        spacingXs = 4.dp,
        spacingS = 8.dp,
        spacingM = 16.dp,
        spacingL = 24.dp,
        spacingXl = 32.dp,
        spacingXxl = 40.dp,

        // Screen layout
        screenPadding = 24.dp,
        sectionSpacing = 40.dp,

        // Cards
        cardPaddingS = 16.dp,
        cardPaddingM = 20.dp,
        cardPaddingL = 24.dp,
        cardSpacing = 12.dp,
        cardRadiusS = 12.dp,
        cardRadiusM = 16.dp,
        cardRadiusL = 20.dp,

        // Components
        iconS = 20.dp,
        iconM = 28.dp,
        iconL = 36.dp,
        buttonHeight = 52.dp,
        touchTarget = 48.dp,

        // Specific cards
        featuredCardWidth = 320.dp,
        featuredCardHeight = 160.dp,
        topicCardHeight = 180.dp,
        listItemHeight = 72.dp,
        compactItemHeight = 56.dp,

        // Navigation
        searchBarHeight = 52.dp,
        tabHeight = 44.dp,
        bottomNavHeight = 60.dp,

        // Modals
        modalPadding = 32.dp,
        modalRadius = 28.dp,
        modalMaxWidth = 400.dp
    )

    ScreenSize.EXPANDED -> DynamicDimensions(
        // Base spacing (slightly increased for larger screens)
        spacingXs = 4.dp,
        spacingS = 8.dp,
        spacingM = 16.dp,
        spacingL = 28.dp,      // Slightly larger
        spacingXl = 36.dp,     // Slightly larger
        spacingXxl = 48.dp,    // Slightly larger

        // Screen layout (optimized for two-pane)
        screenPadding = 32.dp,
        sectionSpacing = 48.dp,

        // Cards
        cardPaddingS = 16.dp,
        cardPaddingM = 24.dp,
        cardPaddingL = 32.dp,
        cardSpacing = 16.dp,
        cardRadiusS = 12.dp,
        cardRadiusM = 16.dp,
        cardRadiusL = 20.dp,

        // Components
        iconS = 20.dp,
        iconM = 28.dp,
        iconL = 36.dp,
        buttonHeight = 52.dp,
        touchTarget = 48.dp,

        // Specific cards (optimized for two-pane layout)
        featuredCardWidth = 300.dp,  // Smaller to fit two-pane
        featuredCardHeight = 160.dp,
        topicCardHeight = 180.dp,
        listItemHeight = 72.dp,
        compactItemHeight = 56.dp,

        // Navigation
        searchBarHeight = 52.dp,
        tabHeight = 44.dp,
        bottomNavHeight = 60.dp,

        // Modals
        modalPadding = 32.dp,
        modalRadius = 28.dp,
        modalMaxWidth = 480.dp
    )
}

/*// Home Screen specific dimensions
class HomeScreenDimensions(private val base: DynamicDimensions) {
    val headerTopMargin = base.spacingL
    val greetingTopMargin = base.spacingXl
    val arabicCardTopMargin = base.spacingL
    val arabicCardHeight = 120.dp
    val featuredSectionTopMargin = base.spacingXl
    val featuredCardSpacing = base.spacingS
    val bottomContentTopMargin = base.spacingL
}

// Browse Screen specific dimensions
class BrowseScreenDimensions(private val base: DynamicDimensions) {
    val titleTopMargin = base.spacingXxl
    val searchBarTopMargin = base.spacingXl
    val tabsTopMargin = base.spacingL
    val filtersTopMargin = base.spacingM
    val listTopMargin = base.spacingM
}

// Detail Screen specific dimensions
class DetailScreenDimensions(private val base: DynamicDimensions) {
    val backButtonMargin = base.spacingM
    val titleTopMargin = base.spacingXl
    val contentTopMargin = base.spacingXl
    val modalElevation = 8.dp
}

// Settings Screen specific dimensions
class SettingsScreenDimensions(private val base: DynamicDimensions) {
    val titleTopMargin = base.spacingXl
    val sectionTopMargin = base.spacingXxl
    val itemSpacing = base.spacingS
    val themePreviewSize = 48.dp
}*/
