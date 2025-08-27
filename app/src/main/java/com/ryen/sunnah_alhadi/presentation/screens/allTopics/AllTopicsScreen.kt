@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.allTopics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.common.ScreenHeaderSection
import com.ryen.sunnah_alhadi.presentation.components.OptimizedTopicsGrid
import com.ryen.sunnah_alhadi.presentation.navigation.AllTopic
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AllTopicsScreen(
    onNavigateToTopic: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AllTopicsViewModel = hiltViewModel(),
) {
    // Collect UI state
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AllTopicsUiEvent.TopicClicked -> {
                    onNavigateToTopic(event.categoryId)
                }
                else -> Unit
            }
        }
    }

    AllTopicsContent(
        uiState = uiState,
        onTopicClick = { categoryId ->
            viewModel.onEvent(AllTopicsUiEvent.TopicClicked(categoryId))
        },
        onRetryClick = {
            viewModel.onEvent(AllTopicsUiEvent.RetryLoading)
        },
        onRefresh = {
            viewModel.onEvent(AllTopicsUiEvent.RefreshTopics)
        },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun AllTopicsContent(
    uiState: AllTopicsUiState,
    onTopicClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(top = 48.dp)
    ) {
        // Top app bar with Islamic design elements
        CustomTopBar(
            onBackClick = onNavigateBack,
            isTopLevel = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Spacer(modifier = Modifier.height(24.dp))

        ScreenHeaderSection(
            screen = AllTopic,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Main content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {

            when {
                uiState.isLoading -> {
                    LoadingContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetryClick = onRetryClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.topics.isNotEmpty() -> {
                    // Main topics grid with pull-to-refresh
                    PullToRefreshLayout(
                        onRefresh = onRefresh,
                        isRefreshing = false // This should ideally reflect actual refresh state
                    ) {
                        OptimizedTopicsGrid(
                            topics = uiState.topics,
                            onTopicClick = onTopicClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> {
                    EmptyContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp)
    ) {
        // Animated loading indicator with Islamic styling
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )

            // Islamic star pattern in center
            Icon(
                painter = painterResource(id = R.drawable.ec_warning), // Assuming this is an appropriate icon
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oops! Something went wrong.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Retry")
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ec_warning), // Assuming this is an appropriate icon
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No topics found",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Try adjusting your filters to find more topics.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun IslamicPatternBackground(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.alpha(0.05f)
    ) {
        val pattern = createComplexIslamicPattern(size)
        drawPath(
            path = pattern,
            color = Color.Black, // Or use MaterialTheme.colorScheme.onBackground for theme awareness
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

// Helper function for complex Islamic geometric pattern
private fun createComplexIslamicPattern(canvasSize: Size): Path {
    return Path().apply {
        val width = canvasSize.width
        val height = canvasSize.height
        val gridSize = 100f

        // Create repeating geometric pattern
        for (x in 0 until (width / gridSize).toInt()) {
            for (y in 0 until (height / gridSize).toInt()) {
                val centerX = x * gridSize + gridSize / 2
                val centerY = y * gridSize + gridSize / 2

                // Create 8-pointed star at each grid point
                addIslamicStar(centerX, centerY, gridSize * 0.3f)
            }
        }
    }
}

private fun Path.addIslamicStar(centerX: Float, centerY: Float, radius: Float) {
    val points = 8
    for (i in 0 until points * 2) {
        val angle = i * PI.toFloat() / points
        val r = if (i % 2 == 0) radius else radius * 0.5f
        val x = centerX + cos(angle) * r
        val y = centerY + sin(angle) * r

        if (i == 0) moveTo(x, y)
        else lineTo(x, y)
    }
    close()
}

// Pull-to-refresh implementation
@Composable
private fun PullToRefreshLayout(
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    content: @Composable () -> Unit
) {
    var refreshing by remember { mutableStateOf(false) } // This state is local to PullToRefreshLayout

    LaunchedEffect(isRefreshing) {
        refreshing = isRefreshing
    }

    // Simple implementation - can be enhanced with actual pull-to-refresh library
    Box(modifier = Modifier.fillMaxSize()) { // Added fillMaxSize for better preview
        content()

        if (refreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}

// Previews

private val sampleTopics = List(30) { index ->
    TopicWithCount(
        category = Category(
            id = index,
            topic = "Topic ${index + 1}",
            // Replace with an actual drawable resource if available, otherwise, this will cause an error if imageRes is Int
            // For network URLs, ensure Coil or Glide is set up for previews or use placeholders.

        ),
        imageRes = android.R.drawable.sym_def_app_icon, // Placeholder
        sunnahCount = (index + 1) * 5
    )
}

@Preview(showBackground = true, name = "AllTopicsContent - Loading")
@Composable
private fun AllTopicsContentLoadingPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        AllTopicsContent(
            uiState = AllTopicsUiState(isLoading = true),
            onTopicClick = {},
            onRetryClick = {},
            onRefresh = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "AllTopicsContent - Error")
@Composable
private fun AllTopicsContentErrorPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        AllTopicsContent(
            uiState = AllTopicsUiState(error = "Failed to load topics. Please check your connection."),
            onTopicClick = {},
            onRetryClick = {},
            onRefresh = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "AllTopicsContent - With Data")
@PreviewScreenSizes
@Composable
private fun AllTopicsContentWithDataPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(360.dp, 640.dp)
        )
    ) {
        AllTopicsContent(
            uiState = AllTopicsUiState(topics = sampleTopics, isLoading = false),
            onTopicClick = {},
            onRetryClick = {},
            onRefresh = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "AllTopicsContent - Empty")
@Composable
private fun AllTopicsContentEmptyPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        AllTopicsContent(
            uiState = AllTopicsUiState(topics = emptyList()),
            onTopicClick = {},
            onRetryClick = {},
            onRefresh = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingContentPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        LoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        ErrorContent(
            error = "A network error occurred.",
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyContentPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        EmptyContent()
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 200)
@Composable
private fun IslamicPatternBackgroundPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) { // Ensure background has size
            IslamicPatternBackground(modifier = Modifier.fillMaxSize())
            Text("Content on top", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true, name = "PullToRefreshLayout - Not Refreshing")
@Composable
private fun PullToRefreshLayoutNotRefreshingPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        PullToRefreshLayout(
            onRefresh = {},
            isRefreshing = false
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Pull down to refresh content here.")
            }
        }
    }
}

@Preview(showBackground = true, name = "PullToRefreshLayout - Refreshing")
@Composable
private fun PullToRefreshLayoutRefreshingPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        PullToRefreshLayout(
            onRefresh = {},
            isRefreshing = true
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Content is currently refreshing...")
            }
        }
    }
}

// Dummy AllTopicsUiState for preview if not defined in the ViewModel or a shared location
// If AllTopicsUiState is defined elsewhere and imported, this can be removed.
// data class AllTopicsUiState(
// val isLoading: Boolean = false,
// val topics: List<TopicWithCount> = emptyList(),
// val error: String? = null
// )
// Assuming Category and TopicWithCount are defined in domain.model and imported.
// If not, their definitions would be needed here for the sampleTopics to compile.
/*
data class Category(
    val id: Int,
    val name: String,
    val description: String, // Added based on typical use
    val imageRes: Int // Or String for URL
)

data class TopicWithCount(
    val category: Category,
    val hadithCount: Int
)
*/
