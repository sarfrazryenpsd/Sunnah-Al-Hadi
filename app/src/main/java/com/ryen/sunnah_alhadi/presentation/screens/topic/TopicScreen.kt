@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.topic

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.common.ScreenHeaderSection
import com.ryen.sunnah_alhadi.presentation.common.SunnahGridCardContainer
import com.ryen.sunnah_alhadi.presentation.components.SunnahPager
import com.ryen.sunnah_alhadi.presentation.navigation.Topic
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

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val screenSize = LocalScreenSize.current
    val dimensions = LocalDynamicDimensions.current

    val onEventCallback = remember { viewModel::onEvent }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
                },
                onBookmarkClick = { sunnahId ->
                    onEventCallback(TopicUiEvent.ToggleBookmark(sunnahId))
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize().padding(top = 48.dp)
    ) {
        // Header with category name and gradient background
        CustomTopBar(
            onBackClick = onNavigateBack,
            isTopLevel = false
        )

        ScreenHeaderSection(
            screen = Topic(""),
            topic = uiState.category?.topic ?: "Loading",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                SunnahGridCardContainer(
                    sunnahs = uiState.sunnahs,
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
fun SkeletonCard() {
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
