@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.topic

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.SunnahPager
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCompactCard
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.presentation.util.buildMetaInfoIconsForSunnah
import com.ryen.sunnah_alhadi.ui.theme.DynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun TopicScreen(
    categoryId: Int,
    viewModel: TopicViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(categoryId) {
        viewModel.initialize(categoryId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val screenSize = LocalScreenSize.current
    val dimensions = LocalDynamicDimensions.current

    val onEventCallback = remember { viewModel::onEvent }

    Box(modifier = Modifier.fillMaxSize()) {
        TopicContent(
            uiState = uiState,
            dimensions = dimensions,
            screenSize = screenSize,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack
        )

        // Cinematic Pager Overlay
        if (uiState.isPagerVisible) {
            SunnahPager(
                sunnahs = uiState.sunnahs,
                initialPage = uiState.selectedSunnahIndex,
                onDismiss = { viewModel.onEvent(TopicUiEvent.ClosePager) },
                onPageChanged = { index ->
                    onEventCallback(TopicUiEvent.PagerPageChanged(index))
                }
            )
        }
    }
}

@Composable
private fun TopicContent(
    uiState: TopicUiState,
    dimensions: DynamicDimensions,
    screenSize: ScreenSize,
    onEvent: (TopicUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header with category name and gradient background
        TopicHeader(
            category = uiState.category,
            onNavigateBack = onNavigateBack
        )

        // Main content area
        when {
            uiState.isLoading -> {
                LoadingContent(
                    dimensions = dimensions,
                    screenSize = screenSize
                )
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error,
                    onRetry = { onEvent(TopicUiEvent.RetryLoading) }
                )
            }

            else -> {
                SunnahGrid(
                    sunnahs = uiState.sunnahs,
                    categoryId = uiState.category?.id ?: 0,
                    dimensions = dimensions,
                    screenSize = screenSize,
                    onSunnahClick = { index ->
                        onEvent(TopicUiEvent.SunnahCardClicked(index))
                    }
                )
            }
        }
    }
}

@Composable
private fun TopicHeader(
    category: Category?,
    onNavigateBack: () -> Unit
) {
    val gradient = if (category != null) {
        CategoryUtils.categoryGradient(category.id)
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = category?.topic
                    ?: "Loading...", // topic field was used in original, using name as a guess from Category
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SunnahGrid(
    sunnahs: List<Sunnah>,
    categoryId: Int,
    dimensions: DynamicDimensions,
    screenSize: ScreenSize,
    onSunnahClick: (Int) -> Unit
) {
    val columns = when (screenSize) {
        ScreenSize.COMPACT -> 1
        ScreenSize.MEDIUM -> 2
        ScreenSize.EXPANDED -> 1
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        verticalItemSpacing = dimensions.cardSpacing,
        horizontalArrangement = Arrangement.spacedBy(dimensions.cardSpacing),
        contentPadding = PaddingValues(dimensions.cardSpacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(sunnahs) { index, sunnah ->
            SunnahGridCard(
                sunnah = sunnah,
                categoryId = categoryId,
                onClick = { onSunnahClick(index) }
            )
        }
    }
}

@Composable
private fun SunnahGridCard(
    sunnah: Sunnah,
    categoryId: Int,
    onClick: () -> Unit
) {
    val gradient = CategoryUtils.categoryGradient(categoryId)
    val gradientColors = CategoryUtils.categoryGradientColors(categoryId)
    val metaIcons = buildMetaInfoIconsForSunnah(sunnah, boxSize = 20.dp)

    // Create background with low opacity gradient + scrim overlay
    val cardModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(
            brush = gradient,
            alpha = 0.1f
        )
        .background(
            color = Color.Black.copy(alpha = 0.05f) // Scrim overlay for readability
        )
        .clickable { onClick() }

    SunnahCompactCard(
        title = sunnah.title,
        extraIcons = metaIcons,
        modifier = cardModifier,
        borderColor = gradientColors.first()
    )
}

@Composable
private fun LoadingContent(
    dimensions: DynamicDimensions,
    screenSize: ScreenSize
) {
    val columns = when (screenSize) {
        ScreenSize.COMPACT -> 1
        ScreenSize.MEDIUM -> 2
        ScreenSize.EXPANDED -> 1
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        verticalItemSpacing = dimensions.cardSpacing,
        horizontalArrangement = Arrangement.spacedBy(dimensions.cardSpacing),
        contentPadding = PaddingValues(dimensions.cardSpacing),
        modifier = Modifier.fillMaxSize()
    ) {
        items(8) { // Show 8 skeleton cards
            SkeletonCard()
        }
    }
}

@Composable
private fun SkeletonCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height((80..120).random().dp) // Random height for staggered effect
            .clip(RoundedCornerShape(8.dp)),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    shimmerBrush() // Shimmer effect for loading
                )
        )
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Retry")
        }
    }
}

// --- Previews ---

// Assuming TopicUiState and TopicUiEvent are defined in this ViewModel or accessible
// For example:
// data class TopicUiState(
//     val isLoading: Boolean = false,
//     val sunnahs: List<Sunnah> = emptyList(),
//     val category: Category? = null,
//     val error: String? = null,
//     val isPagerVisible: Boolean = false,
//     val selectedSunnahIndex: Int = 0
// )
// sealed interface TopicUiEvent {
//     data object RetryLoading : TopicUiEvent
//     data class SunnahCardClicked(val index: Int) : TopicUiEvent
//     // ... other events
// }


private val sampleCategory = Category(id = 1, topic = "Sample Topic")
private val sampleSunnahs = List(5) {
    Sunnah(
        id = it.toString(),
        categoryId = it,
        title = "Sunnah Title $it",
        body = listOf()
    )
}
private val sampleDimensions = DynamicDimensions(
    topicCardWidth = 320.dp,
    topicCardHeight = 140.dp,
    sunnahCardWidth = 140.dp,
    sunnahCardHeight = 80.dp,
    compactCardWidth = 120.dp,
    compactCardHeight = 60.dp,
    cardPadding = 12.dp,
    cardSpacing = 8.dp,
    iconSize = 20.dp,
    imageSize = 56.dp
)

@Preview(name = "TopicContent - Loading", showBackground = true)
@Composable
private fun TopicContentLoadingPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.COMPACT
        ) {
            TopicContent(
                uiState = TopicUiState(isLoading = true, category = sampleCategory),
                dimensions = sampleDimensions,
                screenSize = ScreenSize.COMPACT,
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "TopicContent - Error", showBackground = true)
@Composable
private fun TopicContentErrorPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.COMPACT
        ) {
            TopicContent(
                uiState = TopicUiState(
                    error = "Failed to load content.",
                    category = sampleCategory
                ),
                dimensions = sampleDimensions,
                screenSize = ScreenSize.COMPACT,
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "TopicContent - Success", showBackground = true)
@Composable
private fun TopicContentSuccessPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.COMPACT
        ) {
            TopicContent(
                uiState = TopicUiState(sunnahs = sampleSunnahs, category = sampleCategory),
                dimensions = sampleDimensions,
                screenSize = ScreenSize.COMPACT,
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "TopicHeader - With Category", showBackground = true)
@Composable
private fun TopicHeaderWithCategoryPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        TopicHeader(category = sampleCategory, onNavigateBack = {})
    }
}

@Preview(name = "TopicHeader - Null Category", showBackground = true)
@Composable
private fun TopicHeaderNullCategoryPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        TopicHeader(category = null, onNavigateBack = {})
    }
}

@Preview(name = "SunnahGrid - Compact", showBackground = true)
@Composable
private fun SunnahGridCompactPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.COMPACT
        ) {
            SunnahGrid(
                sunnahs = sampleSunnahs,
                categoryId = 1,
                dimensions = sampleDimensions,
                screenSize = ScreenSize.COMPACT,
                onSunnahClick = {}
            )
        }
    }
}

@Preview(name = "SunnahGrid - Medium", showBackground = true, widthDp = 600)
@Composable
private fun SunnahGridMediumPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.MEDIUM
        ) {
            SunnahGrid(
                sunnahs = sampleSunnahs,
                categoryId = 1,
                dimensions = sampleDimensions,
                screenSize = ScreenSize.MEDIUM,
                onSunnahClick = {}
            )
        }
    }
}

@Preview(name = "SunnahGridCard", showBackground = true)
@Composable
private fun SunnahGridCardPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        // Ensure SunnahCompactCard is available and works with this data
        SunnahGridCard(sunnah = sampleSunnahs.first(), categoryId = 1, onClick = {})
    }
}

@Preview(name = "LoadingContent - Compact", showBackground = true)
@Composable
private fun LoadingContentCompactPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.COMPACT
        ) {
            LoadingContent(dimensions = sampleDimensions, screenSize = ScreenSize.COMPACT)
        }
    }
}

@Preview(name = "LoadingContent - Medium", showBackground = true, widthDp = 600)
@Composable
private fun LoadingContentMediumPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        CompositionLocalProvider(
            LocalDynamicDimensions provides sampleDimensions,
            LocalScreenSize provides ScreenSize.MEDIUM
        ) {
            LoadingContent(dimensions = sampleDimensions, screenSize = ScreenSize.MEDIUM)
        }
    }
}

@Preview(name = "SkeletonCard", showBackground = true)
@Composable
private fun SkeletonCardPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        SkeletonCard()
    }
}

@Preview(name = "ErrorContent", showBackground = true)
@Composable
private fun ErrorContentPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        ErrorContent(error = "This is a sample error message.", onRetry = {})
    }
}
