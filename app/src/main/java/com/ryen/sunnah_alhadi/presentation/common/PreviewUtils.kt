package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.ryen.sunnah_alhadi.ui.theme.DynamicLineHeightConfig
import com.ryen.sunnah_alhadi.ui.theme.DynamicTextConfig
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicLineHeightConfig
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicTextConfig
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.getScaleFactors
import com.ryen.sunnah_alhadi.ui.theme.toDynamicDimensions

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Preview(name = "Compact", widthDp = 360, heightDp = 640, showBackground = true)
@Preview(name = "Medium", widthDp = 600, heightDp = 900, showBackground = true)
@Preview(name = "Expanded", widthDp = 1024, heightDp = 1200, showBackground = true)
annotation class SunnahPreview


@Composable
fun PreviewWrapper(
    widthDp: Int = 360,
    content: @Composable () -> Unit
) {
    val screenSize = when {
        widthDp < 480 -> ScreenSize.COMPACT
        widthDp < 800 -> ScreenSize.MEDIUM
        else -> ScreenSize.EXPANDED
    }

    val scaleFactors = remember(screenSize) { screenSize.getScaleFactors() }
    val textConfig = remember(screenSize, scaleFactors) {
        DynamicTextConfig(screenSize, scaleFactors)
    }
    val lineHeightConfig = remember(scaleFactors) {
        DynamicLineHeightConfig(scaleFactors)
    }
    val dimensions = remember(screenSize) {
        screenSize.toDynamicDimensions()
    }

    CompositionLocalProvider(
        LocalDynamicTextConfig provides textConfig,
        LocalDynamicLineHeightConfig provides lineHeightConfig,
        LocalDynamicDimensions provides dimensions
    ) {
        content()
    }
}

