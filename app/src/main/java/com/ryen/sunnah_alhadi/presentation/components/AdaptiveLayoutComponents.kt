@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.components.cards.AnimatedTopicCard
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCountBadge
import com.ryen.sunnah_alhadi.presentation.screens.allTopics.TopicWithCount
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun AdaptiveTopicsGrid(
    topics: List<TopicWithCount>,
    onTopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDynamicDimensions.current
    val screenSize = LocalScreenSize.current

    // Determine grid configuration based on screen size
    val gridConfig = remember(screenSize) {
        when (screenSize) {
            ScreenSize.COMPACT -> GridConfig(
                columns = 1,
                spacing = dimensions.cardSpacing,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            )

            ScreenSize.MEDIUM -> GridConfig(
                columns = 2,
                spacing = dimensions.cardSpacing,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            )

            ScreenSize.EXPANDED -> GridConfig(
                columns = 3, // Can be 2-3 based on available width
                spacing = dimensions.cardSpacing,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }

    // Lazy grid with parallax scrolling
    val gridState = rememberLazyStaggeredGridState()
    val listState = rememberLazyListState() // Added for AnimatedTopicCard
    val waveLoadingStates = rememberWaveLoadingState(
        itemCount = topics.size,
        animationDelayMs = 80L
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(gridConfig.columns),
        state = gridState,
        contentPadding = gridConfig.contentPadding,
        horizontalArrangement = Arrangement.spacedBy(gridConfig.spacing),
        verticalItemSpacing = gridConfig.spacing,
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = topics,
            key = { _, topic -> topic.category.id }
        ) { index, topicWithCount ->
            AnimatedTopicCard(
                topicWithCount = topicWithCount,
                onClick = onTopicClick,
                scrollState = listState, // Pass listState here
                itemIndex = index,
                isVisible = if (index < waveLoadingStates.size) waveLoadingStates[index] else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem() // Smooth reordering animation
            )
        }
    }
}

// Grid configuration data class
private data class GridConfig(
    val columns: Int,
    val spacing: Dp,
    val contentPadding: PaddingValues
)

// Alternative implementation for regular LazyColumn on compact screens
@Composable
fun CompactTopicsList(
    topics: List<TopicWithCount>,
    onTopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDynamicDimensions.current
    val listState = rememberLazyListState()
    val waveLoadingStates = rememberWaveLoadingState(
        itemCount = topics.size,
        animationDelayMs = 100L
    )

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(dimensions.cardSpacing),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = topics,
            key = { _: Int, topic: TopicWithCount -> topic.category.id }
        ) { index, topicWithCount ->
            AnimatedTopicCard(
                topicWithCount = topicWithCount,
                onClick = onTopicClick,
                scrollState = listState,
                itemIndex = index,
                isVisible = if (index < waveLoadingStates.size) waveLoadingStates[index] else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            )
        }
    }
}

// Responsive grid that chooses between implementations
@Composable
fun ResponsiveTopicsLayout(
    topics: List<TopicWithCount>,
    onTopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current

    when (screenSize) {
        ScreenSize.COMPACT -> {
            // Use single column layout for compact screens
            CompactTopicsList(
                topics = topics,
                onTopicClick = onTopicClick,
                modifier = modifier
            )
        }

        ScreenSize.MEDIUM, ScreenSize.EXPANDED -> {
            // Use adaptive grid for larger screens
            AdaptiveTopicsGrid(
                topics = topics,
                onTopicClick = onTopicClick,
                modifier = modifier
            )
        }
    }
}

// Scroll performance optimization
@Composable
fun OptimizedTopicsGrid(
    topics: List<TopicWithCount>,
    onTopicClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    val dimensions = LocalDynamicDimensions.current
    val screenSize = LocalScreenSize.current


    val columns = when (screenSize) {
        ScreenSize.COMPACT -> 1
        ScreenSize.MEDIUM -> 2
        ScreenSize.EXPANDED -> 1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = gridState,
        contentPadding = PaddingValues(
            horizontal = when (screenSize) {
                ScreenSize.COMPACT -> 16.dp
                ScreenSize.MEDIUM -> 20.dp
                ScreenSize.EXPANDED -> 24.dp
            },
            vertical = 12.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(dimensions.cardSpacing),
        verticalArrangement = Arrangement.spacedBy(dimensions.cardSpacing),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = topics,
            key = { _: Int, topic: TopicWithCount -> topic.category.id }
        ) { index, topicWithCount ->
            SimpleTopicCard(
                topicWithCount = topicWithCount,
                onClick = onTopicClick,
            )
        }
    }
}

// Simplified card for performance optimization
@Composable
fun SimpleTopicCard(
    topicWithCount: TopicWithCount,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Basic version without heavy animations for off-screen items
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalDynamicDimensions.current.topicCardHeight)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick(topicWithCount.category.id) },
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CategoryUtils.categoryGradient(topicWithCount.category.id))
        ) {
            SunnahCountBadge(
                count = topicWithCount.sunnahCount,
                categoryId = topicWithCount.category.id,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
            // Simplified content without animations
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()

            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom, modifier = Modifier.padding(
                        start = LocalDynamicDimensions.current.cardPaddingS,
                        bottom = LocalDynamicDimensions.current.cardPaddingS
                    ).weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.sunnah_and_manner_of),
                        style = MaterialTheme.appTypography.sunnahSubtitle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.offset(y = (6).dp)
                    )
                    BasicText(
                        text = topicWithCount.category.topic,
                        style = MaterialTheme.appTypography.topicMax.copy(
                            color = MaterialTheme.colorScheme.primary,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both // removes extra padding at top/bottom
                            )

                        ),
                        maxLines = 2,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = MaterialTheme.appTypography.topicMin.fontSize,
                            maxFontSize = MaterialTheme.appTypography.topicMax.fontSize
                        )
                    )
                }

                Image(
                    painter = rememberAsyncImagePainter(model = topicWithCount.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

private val sampleTopics = listOf(
    TopicWithCount(Category(1, "Salah"), 10, R.drawable.sleeping_14),
    TopicWithCount(Category(2, "Sawm"), 5, R.drawable.funerals_27),
    TopicWithCount(Category(3, "Zakat"), 8, R.drawable.visiting_sick_25),
    TopicWithCount(Category(4, "Hajj"), 3, R.drawable.ring_21),
    TopicWithCount(Category(5, "Siyam"), 7, R.drawable.treating_relatives_08),
    TopicWithCount(Category(6, "Treating Relatives with Kindness"), 9, R.drawable.graveyards_29),
    TopicWithCount(Category(7, "Dhikr"), 6, R.drawable.burials_28),
    TopicWithCount(Category(8, "Du'a"), 4, R.drawable.shrouding_26),
    TopicWithCount(Category(9, "Quran"), 2, R.drawable.travelling_24),
    TopicWithCount(Category(10, "Hadith"), 1, R.drawable.naming_23),
    TopicWithCount(Category(11, "Sunnah"), 11, R.drawable.aqeeqah_22),
    TopicWithCount(Category(12, "Fiqh"), 12, R.drawable.imamah_20),
    TopicWithCount(Category(13, "Aqeedah"), 13, R.drawable.oil_combing_16),
    TopicWithCount(Category(14, "Tafsir"), 14, R.drawable.clothing_19),
    TopicWithCount(Category(15, "Seerah"), 15, R.drawable.nails_18),
    TopicWithCount(Category(16, "Hadeeth"), 16, R.drawable.nails_18),
    TopicWithCount(Category(17, "Iman"), 17, R.drawable.miswak_17),
    TopicWithCount(Category(18, "Ihsan"), 18, R.drawable.hairstyles_15),
    TopicWithCount(Category(19, "Shahadah"), 19, R.drawable.kohl_13),
)

@Preview(showBackground = true, name = "AdaptiveTopicsGrid - Compact")
@Composable
fun AdaptiveTopicsGridCompactPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {

        // This preview will show how it looks on a compact screen
        // It uses the actual logic inside AdaptiveTopicsGrid for COMPACT
        AdaptiveTopicsGrid(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700, name = "AdaptiveTopicsGrid - Medium")
@Composable
fun AdaptiveTopicsGridMediumPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        // Simulate Medium screen for preview
        AdaptiveTopicsGrid(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1200, name = "AdaptiveTopicsGrid - Expanded")
@Composable
fun AdaptiveTopicsGridExpandedPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        // Simulate Expanded screen for preview
        AdaptiveTopicsGrid(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, name = "CompactTopicsList Preview")
@Composable
fun CompactTopicsListPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        CompactTopicsList(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, name = "ResponsiveTopicsLayout - Compact")
@Composable
fun ResponsiveTopicsLayoutCompactPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        // To truly test ResponsiveTopicsLayout, you might need to run on different devices/emulators
        // or use the Layout Inspector's screen size simulation.
        // This preview will default to the current device/emulator's configuration,
        // or you can force a specific LocalScreenSize if needed for isolated preview.
        ResponsiveTopicsLayout(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700, name = "ResponsiveTopicsLayout - Medium")
@Composable
fun ResponsiveTopicsLayoutMediumPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        ResponsiveTopicsLayout(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}


@Preview(showBackground = true, widthDp = 1200, name = "ResponsiveTopicsLayout - Expanded")
@Composable
fun ResponsiveTopicsLayoutExpandedPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        ResponsiveTopicsLayout(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}


@Preview(showBackground = true, name = "OptimizedTopicsGrid - Compact")
@Composable
fun OptimizedTopicsGridCompactPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(360.dp, 640.dp)
        )
    ) {
        OptimizedTopicsGrid(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700, name = "OptimizedTopicsGrid - Medium")
@Composable
fun OptimizedTopicsGridMediumPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        OptimizedTopicsGrid(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1200, name = "OptimizedTopicsGrid - Expanded")
@Composable
fun OptimizedTopicsGridExpandedPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(800.dp, 480.dp)
        )
    ) {
        OptimizedTopicsGrid(
            topics = sampleTopics,
            onTopicClick = {}
        )
    }
}

@Preview(showBackground = true, name = "SimpleTopicCard Preview")
@Composable
fun SimpleTopicCardPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(360.dp, 640.dp)
        )
    ) {
        SimpleTopicCard(
            topicWithCount = sampleTopics.first(),
            onClick = {}
        )
    }
}
