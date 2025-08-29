package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.screens.allTopics.TopicWithCount
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils.darken
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography


@Composable
fun TopicCardFixedWidth(
    category: Category,
    numberOfSunnah: Int,
    @DrawableRes topicImage: Int,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalDynamicDimensions.current

    TopicCardContent(
        title = category.topic,
        sunnahCount = numberOfSunnah,
        categoryId = category.id,
        imageRes = topicImage,
        modifier = modifier.width(dimensions.featuredCardWidth),
        height = dimensions.featuredCardHeight
    )
}

@Composable
private fun TopicCardContent(
    title: String,
    sunnahCount: Int,
    categoryId: Int,
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
    height: Dp,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(24.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CategoryUtils.categoryGradient(categoryId))
        ) {
            SunnahCountBadge(
                count = sunnahCount,
                categoryId = categoryId,
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .padding(
                            start = LocalDynamicDimensions.current.cardPaddingS,
                            bottom = LocalDynamicDimensions.current.cardPaddingS
                        )
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.sunnah_and_manner_of),
                        style = MaterialTheme.appTypography.sunnahSubtitle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.offset(y = 6.dp)
                    )
                    BasicText(
                        text = title,
                        style = MaterialTheme.appTypography.topicMax.copy(
                            color = MaterialTheme.colorScheme.primary,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
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
                    painter = rememberAsyncImagePainter(model = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun SunnahCountBadge(
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
            TopicCardMaxWidth(
                topicWithCount = topicWithCount,
                onClick = onTopicClick,
            )
        }
    }
}

// Simplified card for performance optimization
@Composable
fun TopicCardMaxWidth(
    topicWithCount: TopicWithCount,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TopicCardContent(
        title = topicWithCount.category.topic,
        sunnahCount = topicWithCount.sunnahCount,
        categoryId = topicWithCount.category.id,
        imageRes = topicWithCount.imageRes,
        modifier = modifier.fillMaxWidth(),
        height = LocalDynamicDimensions.current.topicCardHeight,
        onClick = { onClick(topicWithCount.category.id) }
    )
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopicScreen(topics: List<TopicUiModel>, userName: String) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 24.dp)
        ) {
            Text(
                text = "Assalamualaikum",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Explore Topics",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(topics) { topic ->
                    TopicCardFixedWidth(
                        category = topic.category,
                        numberOfSunnah = topic.count,
                        topicImage = topic.imageRes
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@PreviewScreenSizes
@Composable
fun TopicScreenCleanPreview() {
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.COMPACT
    ) {
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
        ) {
            TopicScreen(
                topics = previewDummyTopics(),
                userName = "Sarfraz"
            )
        }
    }
}



fun previewDummyTopics() = listOf(
    TopicUiModel(Category(id = 1,topic = "Applying Oil and Combing Hair"), 12, R.drawable.oil_combing_16),
    TopicUiModel(Category(id = 2,topic = "Funerals"), 12, R.drawable.funerals_27),
    TopicUiModel(Category(id = 3,topic = "Sleeping"), 12, R.drawable.sleeping_14),
    TopicUiModel(Category(id = 4,topic = "Hospitality"), 12, R.drawable.hospitality_07),
    TopicUiModel(Category(id = 5,topic = "Miswak"), 12, R.drawable.miswak_17),
    TopicUiModel(Category(id = 6,topic = "Visiting Graveyards"), 12, R.drawable.entering_leaving_03),
    TopicUiModel(Category(id = 7,topic = "Applying Kohl"), 12, R.drawable.kohl_13),
)





data class TopicUiModel(
    val category: Category,
    val count: Int,
    @param:DrawableRes val imageRes: Int
)

