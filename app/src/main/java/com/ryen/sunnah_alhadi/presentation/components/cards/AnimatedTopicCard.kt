@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Category // Assuming Category model location
import com.ryen.sunnah_alhadi.presentation.components.floatingAnimation
import com.ryen.sunnah_alhadi.presentation.components.parallaxEffect
import com.ryen.sunnah_alhadi.presentation.components.scaleInAnimation
import com.ryen.sunnah_alhadi.presentation.screens.allTopics.TopicWithCount
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils.darken
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedTopicCard(
    topicWithCount: TopicWithCount,
    onClick: (Int) -> Unit,
    scrollState: LazyListState,
    itemIndex: Int,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalDynamicDimensions.current
    val haptics = LocalHapticFeedback.current

    // Animation states for interaction feedback
    var isPressed by remember { mutableStateOf(false) }
    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "pressed_scale"
    )

    // Card background gradient based on category
    val cardGradient = CategoryUtils.categoryGradient(topicWithCount.category.id)

    Card(
        modifier = modifier
            .fillMaxWidth() // Use full width instead of fixed width
            .height(dimensions.topicCardHeight)
            .parallaxEffect(
                scrollState = scrollState,
                itemIndex = itemIndex,
                parallaxFactor = 0.15f // Subtle parallax effect
            )
            .scaleInAnimation(
                isVisible = isVisible,
                animationDuration = 400,
                delayMs = itemIndex * 50 // Staggered animation delay
            )
            .scale(pressedScale)
            .pointerInput(topicWithCount.category.id) {
                detectTapGestures(onPress = {
                    isPressed = true
                    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    tryAwaitRelease()
                    isPressed = false
                }, onTap = {
                    onClick(topicWithCount.category.id)
                })
            }, shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp, pressedElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient) // Apply category-specific gradient
        ) {
            // Islamic pattern overlay (subtle)
            IslamicPatternOverlay(
                modifier = Modifier.fillMaxSize(), alpha = 0.1f
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Sunnah count badge with enhanced styling
                SunnahCountBadge(
                    count = topicWithCount.sunnahCount,
                    categoryId = topicWithCount.category.id,
                    modifier = Modifier.padding(top = 12.dp, start = 12.dp)
                )

                // Main content row
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text content column
                    TopicTextContent(
                        categoryName = topicWithCount.category.topic, modifier = Modifier.weight(1f)
                        //.padding(dimensions.cardPadding)
                    )

                    // Animated 3D illustration
                    Animated3DImage(
                        imageRes = topicWithCount.imageRes,
                        contentDescription = topicWithCount.category.topic,
                        modifier = Modifier
                            .fillMaxHeight()
                            //.size(dimensions.imageSize)
                            .floatingAnimation(
                                animationDuration = 3000 + (itemIndex * 200), // Varied timing
                                maxOffsetY = 6f
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun SunnahCountBadge(
    categoryId: Int, count: Int, modifier: Modifier = Modifier
) {
    val bgColor = CategoryUtils.categoryGradientColors(categoryId).first().darken(0.3f)
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .size(36.dp)
            .background(
                color = bgColor, shape = CircleShape
            )
    ) {
        Text(
            text = "$count", style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold, color = Color(0xFF1565C0)
            ), color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun TopicTextContent(
    categoryName: String, modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // "Sunnah and Manner of" label
        Text(
            text = stringResource(id = R.string.sunnah_and_manner_of),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.75,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        )

        // Main topic heading with enhanced styling
        /*BasicText(
            text = categoryName,
            style = MaterialTheme.appTypography.topicHeading.copy(
                lineHeight = MaterialTheme.appTypography.topicHeading.lineHeight * 0.8,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = Offset(1f, 1f),
                    blurRadius = 2f
                )
            ),
            maxLines = 2,
            autoSize = TextAutoSize.StepBased( // TextAutoSize is part of BasicText style in newer versions
                minFontSize = MaterialTheme.appTypography.topicHeading.fontSize * 0.6,
                maxFontSize = MaterialTheme.appTypography.topicHeading.fontSize
            )
        )*/
    }
}

@Composable
private fun Animated3DImage(
    @DrawableRes imageRes: Int, contentDescription: String, modifier: Modifier = Modifier
) {
    // Enhanced image with subtle glow effect
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        // Subtle glow background
        Box(
            modifier = Modifier
                //.size(LocalDynamicDimensions.current.imageSize * 1.2f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f), Color.Transparent
                        )
                    ), shape = CircleShape
                )
        )

        // Main 3D image
        Image(
            painter = rememberAsyncImagePainter(model = imageRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxHeight()
            //.size(LocalDynamicDimensions.current.imageSize)
        )
    }
}

@Composable
private fun IslamicPatternOverlay(
    modifier: Modifier = Modifier, alpha: Float = 0.1f
) {
    // Subtle Islamic geometric pattern overlay
    Canvas(modifier = modifier.alpha(alpha)) {
        val pattern = createIslamicGeometricPattern()
        drawPath(
            path = pattern, color = Color.White, style = Stroke(width = 1.dp.toPx())
        )
    }
}

// Helper function to create Islamic geometric pattern
private fun createIslamicGeometricPattern(): Path {
    return Path().apply {
        // Simple geometric pattern - can be enhanced with more complex Islamic patterns
        // This creates a basic star pattern that repeats
        val centerX = 50f
        val centerY = 50f
        val radius = 30f

        // Create 8-pointed star pattern
        for (i in 0 until 8) {
            val angle = i * 45f * (PI / 180f).toFloat()
            val x = centerX + cos(angle) * radius
            val y = centerY + sin(angle) * radius

            if (i == 0) moveTo(x, y)
            else lineTo(x, y)
        }
        close()
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimatedTopicCardPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        AnimatedTopicCard(
            topicWithCount = TopicWithCount(
                category = Category(id = 1, topic = "Prayer (Salah)"),
                sunnahCount = 25,
                imageRes = R.drawable.ic_launcher_background // Replace with an actual drawable
            ), onClick = {}, scrollState = rememberLazyListState(), itemIndex = 0, isVisible = true
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun SunnahCountBadgePreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        SunnahCountBadge(count = 10, categoryId = 1, modifier = Modifier)
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicTextContentPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        TopicTextContent(categoryName = "Fasting (Sawm)")
    }
}

@Preview(showBackground = true)
@Composable
private fun Animated3DImagePreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        Animated3DImage(
            imageRes = R.drawable.ic_launcher_background, // Replace with an actual drawable
            contentDescription = "Sample Image"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IslamicPatternOverlayPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        Box(modifier = Modifier.size(100.dp)) { // Added a Box for better preview
            IslamicPatternOverlay(modifier = Modifier.fillMaxSize())
        }
    }
}

