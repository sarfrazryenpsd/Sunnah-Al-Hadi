package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Utility object for handling dynamic icon sizing across different screen sizes
 * Optimized for the Sunnah App to provide consistent visual hierarchy
 */
object IconUtils {

    /**
     * Icon size categories for different use cases
     */
    enum class IconSizeCategory {
        SMALL,      // For inline text, small buttons
        MEDIUM,     // For standard list items, cards
        LARGE,      // For headers, featured content
        EXTRA_LARGE // For hero sections, empty states
    }

    /**
     * Gets the appropriate icon size based on window size class and icon category
     *
     * @param windowSizeClass Current window size class
     * @param category Icon size category
     * @return Dp size for the icon
     */
    fun getIconSize(
        windowSizeClass: WindowSizeClass,
        category: IconSizeCategory = IconSizeCategory.MEDIUM
    ): Dp {
        return when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                when (category) {
                    IconSizeCategory.SMALL -> 16.dp
                    IconSizeCategory.MEDIUM -> 24.dp
                    IconSizeCategory.LARGE -> 32.dp
                    IconSizeCategory.EXTRA_LARGE -> 48.dp
                }
            }
            WindowWidthSizeClass.Medium -> {
                when (category) {
                    IconSizeCategory.SMALL -> 20.dp
                    IconSizeCategory.MEDIUM -> 28.dp
                    IconSizeCategory.LARGE -> 40.dp
                    IconSizeCategory.EXTRA_LARGE -> 56.dp
                }
            }
            WindowWidthSizeClass.Expanded -> {
                when (category) {
                    IconSizeCategory.SMALL -> 24.dp
                    IconSizeCategory.MEDIUM -> 32.dp
                    IconSizeCategory.LARGE -> 48.dp
                    IconSizeCategory.EXTRA_LARGE -> 64.dp
                }
            }
            else -> {
                // Fallback to compact sizes
                when (category) {
                    IconSizeCategory.SMALL -> 16.dp
                    IconSizeCategory.MEDIUM -> 24.dp
                    IconSizeCategory.LARGE -> 32.dp
                    IconSizeCategory.EXTRA_LARGE -> 48.dp
                }
            }
        }
    }

    /**
     * Gets icon size based on density-independent pixel values for custom sizing
     * Useful when you need specific sizes not covered by categories
     */
    fun getCustomIconSize(
        windowSizeClass: WindowSizeClass,
        compactSize: Dp,
        mediumSize: Dp = compactSize * 1.17f, // ~17% increase
        expandedSize: Dp = compactSize * 1.33f  // ~33% increase
    ): Dp {
        return when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> compactSize
            WindowWidthSizeClass.Medium -> mediumSize
            WindowWidthSizeClass.Expanded -> expandedSize
            else -> compactSize
        }
    }
}

/**
 * Composable function to load and display icons with dynamic sizing
 *
 * @param iconRes Resource ID of the icon
 * @param windowSizeClass Current window size class
 * @param category Size category for the icon
 * @param contentDescription Accessibility description
 * @return Painter for the icon
 */
@Composable
fun rememberDynamicIcon(
    iconRes: Int,
    windowSizeClass: WindowSizeClass,
    category: IconUtils.IconSizeCategory = IconUtils.IconSizeCategory.MEDIUM,
    contentDescription: String? = null
): Painter {
    return painterResource(id = iconRes)
}
