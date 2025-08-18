package com.ryen.sunnah_alhadi.presentation.screens.browse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.components.SunnahPager
import com.ryen.sunnah_alhadi.presentation.screens.topic.SkeletonCard
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.presentation.util.buildMetaInfoIconsForSunnah
import com.ryen.sunnah_alhadi.ui.theme.DynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize


// BrowseScreen Main Composable
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenSize = LocalScreenSize.current
    val dimensions = LocalDynamicDimensions.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Section - Persistent Search Bar
        BrowseSearchBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChanged = { viewModel.onEvent(BrowseUiEvent.SearchQueryChanged(it)) },
            onClearSearch = { viewModel.onEvent(BrowseUiEvent.ClearSearch) },
            modifier = Modifier.fillMaxWidth()
        )

        // Tab System - Floating Navigation Bar
        AnimatedBrowseTabBar(
            currentTab = uiState.currentTab,
            onTabChanged = { viewModel.onEvent(BrowseUiEvent.TabChanged(it)) },
            allSunnahsCount = uiState.allSunnahs.size,
            savedSunnahsCount = uiState.bookmarkedSunnahs.size,
            modifier = Modifier.fillMaxWidth()
        )

        // Filter Chips Row
        BrowseFilterChips(
            selectedFilters = uiState.selectedFilters,
            onFilterToggled = { viewModel.onEvent(BrowseUiEvent.FilterToggled(it)) },
            onClearAllFilters = { viewModel.onEvent(BrowseUiEvent.ClearAllFilters) },
            modifier = Modifier.fillMaxWidth()
        )

        // Content Section
        BrowseContent(
            uiState = uiState,
            screenSize = screenSize,
            dimensions = dimensions,
            onSunnahCardClicked = { sunnah ->
                viewModel.onEvent(BrowseUiEvent.SunnahCardClicked(sunnah))
            },
            onRetryLoading = { viewModel.onEvent(BrowseUiEvent.RetryLoading) },
            modifier = Modifier.fillMaxSize()
        )
    }
    if (uiState.isPagerVisible) {
        SunnahPager(
            sunnahs = uiState.filteredSunnahs, // Use filtered list based on search/tab
            initialPage = uiState.selectedSunnahIndex,
            onDismiss = { viewModel.onEvent(BrowseUiEvent.ClosePager) },
            onPageChanged = { index ->
                viewModel.onEvent(BrowseUiEvent.PagerPageChanged(index))
            }
        )
    }
}

// Search Bar Component
@Composable
private fun BrowseSearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search Sunnahs...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )

            AnimatedVisibility(
                visible = searchQuery.isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                IconButton(
                    onClick = onClearSearch,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BrowseSearchBarPreview() {
    BrowseSearchBar(
        searchQuery = "Sunnah",
        onSearchQueryChanged = {},
        onClearSearch = {},
        modifier = Modifier.fillMaxWidth()
    )
}


// Tab Bar Component
@Composable
private fun BrowseTabBar(
    currentTab: BrowseTab,
    onTabChanged: (BrowseTab) -> Unit,
    allSunnahsCount: Int,
    savedSunnahsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BrowseTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val tabTitle = when (tab) {
                    BrowseTab.ALL_SUNNAH -> "All Sunnah"
                    BrowseTab.SAVED -> "Saved"
                }
                val tabCount = when (tab) {
                    BrowseTab.ALL_SUNNAH -> allSunnahsCount
                    BrowseTab.SAVED -> savedSunnahsCount
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabChanged(tab) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 2.dp else 0.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tabTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )

                        Text(
                            text = tabCount.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedBrowseTabBar(
    currentTab: BrowseTab,
    onTabChanged: (BrowseTab) -> Unit,
    allSunnahsCount: Int,
    savedSunnahsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BrowseTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val tabTitle = when (tab) {
                    BrowseTab.ALL_SUNNAH -> "All Sunnah"
                    BrowseTab.SAVED -> "Saved"
                }

                // Enhanced tab animation with spring physics
                val animatedElevation by animateDpAsState(
                    targetValue = if (isSelected) 4.dp else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tab_elevation"
                )

                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.02f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tab_scale"
                )
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onTabChanged(tab)
                            // Add haptic feedback here if available
                        }
                        .weight(1f)
                        .scale(animatedScale),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        disabledContainerColor = Color.Transparent,
                        contentColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Animated text color transition
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            animationSpec = tween(300),
                            label = "text_color"
                        )

                        Text(
                            text = tabTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun BrowseTabBarPreview() {
    BrowseTabBar(
        currentTab = BrowseTab.ALL_SUNNAH,
        onTabChanged = {},
        allSunnahsCount = 10,
        savedSunnahsCount = 5,
        modifier = Modifier.fillMaxWidth()
    )
}


// Filter Chips Component
@Composable
private fun BrowseFilterChips(
    selectedFilters: Set<FilterType>,
    onFilterToggled: (FilterType) -> Unit,
    onClearAllFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(
                visible = selectedFilters.isNotEmpty(),
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                TextButton(
                    onClick = onClearAllFilters,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = FilterType.entries,
                key = { it.name }
            ) { filter ->
                BrowseFilterChip(
                    filter = filter,
                    isSelected = selectedFilters.contains(filter),
                    onClick = { onFilterToggled(filter) }
                )
            }
        }
    }
}

@Preview
@Composable
fun BrowseFilterChipsPreview() {
    BrowseFilterChips(
        selectedFilters = setOf(FilterType.HAS_VERSES, FilterType.HAS_SUPPLICATIONS),
        onFilterToggled = {},
        onClearAllFilters = {},
        modifier = Modifier.fillMaxWidth()
    )
}

// Individual Filter Chip Component
@Composable
private fun BrowseFilterChip(
    filter: FilterType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) 120.dp else 48.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_width"
    )

    Card(
        modifier = modifier
            .width(animatedWidth)
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filter.icon,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.size(20.dp)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BrowseFilterChipPreview() {
    BrowseFilterChip(
        filter = FilterType.HAS_VERSES,
        isSelected = true,
        onClick = {},
        modifier = Modifier
    )
}

// Content Section Component
@Composable
private fun BrowseContent(
    uiState: BrowseUiState,
    screenSize: ScreenSize,
    dimensions: DynamicDimensions,
    onSunnahCardClicked: (Sunnah) -> Unit,
    onRetryLoading: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
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

        uiState.error != null -> {
            BrowseErrorState(
                error = uiState.error,
                onRetry = onRetryLoading,
                modifier = modifier
            )
        }

        uiState.filteredSunnahs.isEmpty() -> {
            BrowseEmptyState(
                currentTab = uiState.currentTab,
                searchQuery = uiState.searchQuery,
                selectedFilters = uiState.selectedFilters,
                modifier = modifier
            )
        }

        else -> {
            BrowseSunnahGrid(
                sunnahs = uiState.filteredSunnahs,
                searchQuery = uiState.searchQuery,
                onSunnahCardClicked = onSunnahCardClicked,
                modifier = modifier
            )
        }
    }
}


// Error State Component
@Composable
private fun BrowseErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@Preview
@Composable
fun BrowseErrorStatePreview() {
    BrowseErrorState(
        error = "Network error",
        onRetry = {},
        modifier = Modifier.fillMaxSize()
    )
}


// Empty State Component
@Composable
private fun BrowseEmptyState(
    currentTab: BrowseTab,
    searchQuery: String,
    selectedFilters: Set<FilterType>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val (title, subtitle) = when {
            searchQuery.isNotEmpty() -> {
                "No Sunnahs found" to "Try searching with different terms"
            }

            selectedFilters.isNotEmpty() -> {
                "No results with current filters" to "Try removing some filters"
            }

            currentTab == BrowseTab.SAVED -> {
                "No saved Sunnahs yet" to "Bookmark your favorite Sunnahs to see them here"
            }

            else -> {
                "No Sunnahs available" to "Please check back later"
            }
        }

        Text(
            text = "🕌",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun BrowseEmptyStatePreview() {
    BrowseEmptyState(
        currentTab = BrowseTab.ALL_SUNNAH,
        searchQuery = "",
        selectedFilters = emptySet(),
        modifier = Modifier.fillMaxSize()
    )
}


// Sunnah Grid Component (Placeholder - will be implemented in Phase 3)
@Composable
fun BrowseSunnahGrid(
    sunnahs: List<Sunnah>,
    searchQuery: String,
    onSunnahCardClicked: (Sunnah) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = sunnahs,
            key = { it.id }
        ) { sunnah ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { -it / 2 }
                ) + fadeOut(animationSpec = tween(300))
            ) {
                EnhancedSunnahCompactCard(
                    sunnah = sunnah,
                    searchQuery = searchQuery,
                    onClick = { onSunnahCardClicked(sunnah) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    searchQuery: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    highlightColor: Color = MaterialTheme.colorScheme.primaryContainer,
    highlightTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    if (searchQuery.isEmpty() || searchQuery.length < 2) {
        Text(
            text = text,
            style = style,
            modifier = modifier
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        val cleanQuery = searchQuery.trim()
        val lowerText = text.lowercase()
        val lowerQuery = cleanQuery.lowercase()

        var currentIndex = 0
        var matchIndex = lowerText.indexOf(lowerQuery, currentIndex)

        while (matchIndex != -1 && matchIndex < text.length) {
            // Add text before match
            if (matchIndex > currentIndex) {
                append(text.substring(currentIndex, matchIndex))
            }

            // Add highlighted match
            val matchEnd = (matchIndex + cleanQuery.length).coerceAtMost(text.length)
            withStyle(
                style = SpanStyle(
                    background = highlightColor,
                    color = highlightTextColor
                )
            ) {
                append(text.substring(matchIndex, matchEnd))
            }

            currentIndex = matchEnd
            matchIndex = lowerText.indexOf(lowerQuery, currentIndex)
        }

        // Add remaining text
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier
    )
}

// Enhanced SunnahCompactCard with Search Highlighting
@Composable
fun EnhancedSunnahCompactCard(
    modifier: Modifier = Modifier,
    sunnah: Sunnah,
    searchQuery: String = "",
    onClick: () -> Unit
) {
    val categoryGradientBrush = CategoryUtils.categoryGradient(sunnah.categoryId)
    val metaIcons = buildMetaInfoIconsForSunnah(sunnah, 20.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        brush = categoryGradientBrush
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 8.dp)
            ) {
                // Title with highlighting
                HighlightedText(
                    text = sunnah.title,
                    searchQuery = searchQuery,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Icons Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    items(metaIcons) { icon ->
                        icon()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BrowseSunnahGridPreview() {
    BrowseSunnahGrid(
        sunnahs = listOf(
            Sunnah("1", 1, "Sunnah Title 1", emptyList()),
            Sunnah("2", 2, "Sunnah Title 2", emptyList())
        ),
        searchQuery = "",
        onSunnahCardClicked = {},
        modifier = Modifier.fillMaxSize()
    )
}

