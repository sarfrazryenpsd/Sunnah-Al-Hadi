@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.allTopics

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ryen.sunnah_alhadi.presentation.components.cards.OptimizedTopicsGrid
import com.ryen.sunnah_alhadi.presentation.navigation.AllTopic
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun AllTopicsScreen(
    modifier: Modifier = Modifier,
    onNavigateToTopic: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AllTopicsViewModel = hiltViewModel()
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
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
private fun AllTopicsContent(
    uiState: AllTopicsUiState,
    onTopicClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp)
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
                    Box(modifier = Modifier.fillMaxSize())
                    // Added fillMaxSize for better preview
                    {
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
