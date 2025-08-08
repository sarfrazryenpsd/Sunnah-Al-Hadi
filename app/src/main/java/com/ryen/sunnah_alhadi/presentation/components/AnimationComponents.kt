package com.ryen.sunnah_alhadi.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Floating animation modifier for 3D images
@Composable
fun Modifier.floatingAnimation(
    animationDuration: Int = 3000,
    maxOffsetY: Float = 8f
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    // Gentle floating animation with sine wave pattern
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = maxOffsetY,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )

    return this.offset(y = offsetY.dp)
}

// Wave loading animation controller
@Composable
fun rememberWaveLoadingState(
    itemCount: Int,
    animationDelayMs: Long = 100L
): List<Boolean> {
    // Track visibility state for each item
    val visibilityStates = remember { mutableStateListOf<Boolean>().apply {
        repeat(itemCount) { add(false) }
    }}

    // Trigger wave animation on composition
    LaunchedEffect(itemCount) {
        visibilityStates.clear()
        repeat(itemCount) { visibilityStates.add(false) }

        // Staggered appearance animation
        repeat(itemCount) { index ->
            delay(animationDelayMs)
            if (index < visibilityStates.size) {
                visibilityStates[index] = true
            }
        }
    }

    return visibilityStates
}

// Parallax scroll modifier for lazy column items
@Composable
fun Modifier.parallaxEffect(
    scrollState: LazyListState,
    itemIndex: Int,
    parallaxFactor: Float = 0.5f
): Modifier {
    // Calculate parallax offset based on scroll position and item index
    val parallaxOffset = remember(scrollState.firstVisibleItemIndex, scrollState.firstVisibleItemScrollOffset) {
        val currentItem = scrollState.firstVisibleItemIndex
        val scrollOffset = scrollState.firstVisibleItemScrollOffset

        // Apply parallax only to visible and near-visible items
        when {
            itemIndex < currentItem - 1 -> 0f // Items far above
            itemIndex > currentItem + 10 -> 0f // Items far below
            else -> {
                val relativePosition = (itemIndex - currentItem) * 1000 - scrollOffset
                -relativePosition * parallaxFactor
            }
        }
    }

    return this.graphicsLayer {
        translationY = parallaxOffset
    }
}

// Scale animation for card appearance
@Composable
fun Modifier.scaleInAnimation(
    isVisible: Boolean,
    animationDuration: Int = 300,
    delayMs: Int = 0
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = delayMs,
            easing = FastOutSlowInEasing
        ),
        label = "scale_animation"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = delayMs,
            easing = LinearEasing
        ),
        label = "alpha_animation"
    )

    return this
        .scale(scale)
        .alpha(alpha)
}