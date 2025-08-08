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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.components.OptimizedTopicsGrid
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
        modifier = modifier
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
        modifier = modifier.fillMaxSize()
    ) {
        // Top app bar with Islamic design elements
        AllTopicsAppBar(
            onNavigateBack = onNavigateBack,
            onRefresh = onRefresh
        )

        // Main content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            // Islamic pattern background
            IslamicPatternBackground(
                modifier = Modifier.fillMaxSize()
            )

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
                        isRefreshing = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllTopicsAppBar(
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Topics",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Explore All Topics",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
    )
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
                painter = painterResource(id = R.drawable.ec_warning),
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
            painter = painterResource(id = R.drawable.ec_warning),
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
            color = Color.Black,
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
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        refreshing = isRefreshing
    }

    // Simple implementation - can be enhanced with actual pull-to-refresh library
    Box {
        content()

        if (refreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}