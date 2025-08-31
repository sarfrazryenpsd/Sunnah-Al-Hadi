@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.browse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.common.ScreenHeaderSection
import com.ryen.sunnah_alhadi.presentation.common.SunnahGridCardContainer
import com.ryen.sunnah_alhadi.presentation.components.SunnahPager
import com.ryen.sunnah_alhadi.presentation.components.cards.innerShadow
import com.ryen.sunnah_alhadi.presentation.navigation.Browse
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.ui.theme.DynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography


// BrowseScreen Main Composable
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenSize = LocalScreenSize.current
    val dimensions = LocalDynamicDimensions.current

    BrowseScreenContent(
        uiState = uiState,
        screenSize = screenSize,
        //dimensions = dimensions,
        onSunnahCardClickedByIndex = { index -> // New callback for index-based clicks
            viewModel.onEvent(BrowseUiEvent.SunnahCardClickedByIndex(index))
        },
        onTabChanged = { tab ->
            viewModel.onEvent(BrowseUiEvent.TabChanged(tab))
        },
        onSearchQueryChanged = { query ->
            viewModel.onEvent(BrowseUiEvent.SearchQueryChanged(query))
        },
        onClearSearch = { viewModel.onEvent(BrowseUiEvent.ClearSearch) },
        onRetryLoading = { viewModel.onEvent(BrowseUiEvent.RetryLoading) },
        onFilterToggled = { filter ->
            viewModel.onEvent(BrowseUiEvent.FilterToggled(filter))
        },
        modifier = modifier,
    )

    if (uiState.isPagerVisible) {
        SunnahPager(
            sunnahs = uiState.filteredSunnahs, // Use filtered list based on search/tab
            initialPage = uiState.selectedSunnahIndex,
            onDismiss = { viewModel.onEvent(BrowseUiEvent.ClosePager) },
            onPageChanged = { index ->
                viewModel.onEvent(BrowseUiEvent.PagerPageChanged(index))
            },
            onBookmarkClick = { sunnahId ->
                viewModel.onEvent(BrowseUiEvent.ToggleBookmark(sunnahId))
            }
        )
    }
}

@Composable
fun BrowseScreenContent(
    modifier: Modifier = Modifier,
    uiState: BrowseUiState,
    screenSize: ScreenSize,
    //dimensions: DynamicDimensions,
    onSunnahCardClickedByIndex: (Int) -> Unit = {}, // New parameter
    onTabChanged: (BrowseTab) -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onRetryLoading: () -> Unit = {},
    onFilterToggled: (FilterType) -> Unit = {},
) {
    val colorList = CategoryUtils.specialGradient()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        item{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = colorList,
                            start = Offset(500f, -500f),
                            end = Offset(-100f, 200f)
                        )
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                ) {
                    CustomTopBar(isBrowse = true)

                    Spacer(modifier = Modifier.height(24.dp))

                    ScreenHeaderSection(
                        screen = Browse,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                        BrowseSearchBar(
                            searchQuery = uiState.searchQuery,
                            onSearchQueryChanged = { onSearchQueryChanged(it) },
                            onClearSearch = onClearSearch
                        )
                    }
                }
            }
        }

        item {
            BrowseTabBar(
                currentTab = uiState.currentTab,
                onTabChanged = onTabChanged,
                modifier = modifier
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            BrowseFilterChips(
                selectedFilters = uiState.selectedFilters,
                onFilterToggled = { filter -> onFilterToggled(filter) },
                modifier = modifier
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            uiState.error != null -> {
                // Error state UI
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = uiState.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetryLoading) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.filteredSunnahs.isEmpty() -> {
                // Empty state UI
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Sunnah", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }

            else -> {
                // Use the new enhanced container
                item {
                    SunnahGridCardContainer(
                        sunnahs = uiState.filteredSunnahs,
                        onSunnahClick = onSunnahCardClickedByIndex, // Use index-based callback
                        screenSize = screenSize,
                        searchQuery = uiState.searchQuery, // Pass search query for highlighting
                    )
                }
            }
        }
    }
}

@Preview
@PreviewLightDark
@PreviewScreenSizes
@Composable
fun BrowseScreenContentPreview() {
    val dynamicDimensions = DynamicDimensions(
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
        featuredCardWidth = 350.dp,
        featuredCardHeight = 160.dp,
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
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.COMPACT,
        LocalDynamicDimensions provides dynamicDimensions
    ) {
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
        ) {
            BrowseScreenContent(
                uiState = BrowseUiState(
                    searchQuery = "Search query",
                    isLoading = false,
                    allSunnahs = listOf(
                        Sunnah("1", 1, "Sunnah Title 1", emptyList()),
                        Sunnah("2", 2, "Sunnah Title 2", emptyList())
                    ),
                    filteredSunnahs = listOf(
                        Sunnah("1", 1, "Sunnah Title 1", emptyList())
                    )
                ),
                screenSize = ScreenSize.COMPACT,
                //dimensions = dynamicDimensions,
                onTabChanged = {},
                onSearchQueryChanged = {},
                onClearSearch = {},
                onFilterToggled = {_ -> },
                onRetryLoading = {}
            )
        }
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
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.interface_search),
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        BasicTextField(
            value = searchQuery.take(25),
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = MaterialTheme.appTypography.searchPlaceHolder.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search by title",
                        style = MaterialTheme.appTypography.searchPlaceHolder,
                        color = MaterialTheme.colorScheme.primary
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
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear search",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

}

@Preview
@Composable
fun BrowseSearchBarPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ) {
        BrowseSearchBar(
            searchQuery = "Sunnah",
            onSearchQueryChanged = {},
            onClearSearch = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}


// Tab Bar Component
@Composable
private fun BrowseTabBar(
    currentTab: BrowseTab,
    onTabChanged: (BrowseTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(32))
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .innerShadow(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                blur = 8.dp,
                offsetX = 0.dp,
                offsetY = 0.dp,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            BrowseTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val tabColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    Color.Transparent
                }
                val tabTitle = when (tab) {
                    BrowseTab.ALL_SUNNAH -> "All Sunnah"
                    BrowseTab.SAVED -> "Saved"
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .weight(1f)
                        .background(tabColor)
                        .clickable { onTabChanged(tab) },

                    ) {
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        animationSpec = tween(300),
                        label = "text_color"
                    )
                    Text(
                        text = tabTitle,
                        style = MaterialTheme.appTypography.tabs,
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun BrowseTabBarPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ){
        BrowseTabBar(
            currentTab = BrowseTab.ALL_SUNNAH,
            onTabChanged = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}


// Filter Chips Component
@Composable
private fun BrowseFilterChips(
    selectedFilters: Set<FilterType>,
    onFilterToggled: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
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

@Preview
@Composable
fun BrowseFilterChipsPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ){
        BrowseFilterChips(
            selectedFilters = setOf(FilterType.HAS_VERSES, FilterType.HAS_SUPPLICATIONS),
            onFilterToggled = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Individual Filter Chip Component
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BrowseFilterChip(
    filter: FilterType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    Color.Transparent
                }
            )
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(filter.icon),
                contentDescription = filter.displayName,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.tertiary
                },
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
                        style = MaterialTheme.appTypography.topicSubtitleFilters,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.tertiary
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
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ){
        BrowseFilterChip(
            filter = FilterType.HAS_VERSES,
            isSelected = true,
            onClick = {},
            modifier = Modifier
        )
    }
}








