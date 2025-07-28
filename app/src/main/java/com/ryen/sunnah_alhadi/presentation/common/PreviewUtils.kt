package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Preview(name = "Compact", widthDp = 360, heightDp = 640, showBackground = true)
@Preview(name = "Medium", widthDp = 600, heightDp = 900, showBackground = true)
@Preview(name = "Expanded", widthDp = 1024, heightDp = 1200, showBackground = true)
annotation class SunnahPreview


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PreviewWrapperWithFullTheme(
    widthDp: Int = 360,
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val screenSize = when {
        widthDp < 480 -> ScreenSize.COMPACT
        widthDp < 800 -> ScreenSize.MEDIUM
        else -> ScreenSize.EXPANDED
    }

    // Mock WindowSizeClass for preview
    val mockWindowSizeClass = androidx.compose.material3.windowsizeclass.WindowSizeClass.calculateFromSize(
        size = androidx.compose.ui.unit.DpSize(
            width = widthDp.dp,
            height = 800.dp // Default height for preview
        )
    )

    SunnahAlHadiTheme(
        themeMode = if (useDarkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        windowSizeClass = mockWindowSizeClass
    ) {
        CompositionLocalProvider(
            LocalScreenSize provides screenSize
        ) {
            content()
        }
    }
}

