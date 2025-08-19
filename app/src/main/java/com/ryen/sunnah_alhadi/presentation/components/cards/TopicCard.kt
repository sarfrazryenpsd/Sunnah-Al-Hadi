package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.common.PreviewWrapperWithFullTheme
import com.ryen.sunnah_alhadi.presentation.common.SunnahPreview
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography


@Composable
fun TopicCard(
    category: Category,
    numberOfSunnah: Int,
    @DrawableRes topicSImage: Int,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalDynamicDimensions.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensions.featuredCardHeight)
            .width(dimensions.featuredCardWidth),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CategoryUtils.categoryGradient(category.id))
        ) {
            SunnahCountBadge(
                count = numberOfSunnah,
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
                    verticalArrangement = Arrangement.Bottom, modifier = Modifier
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
                        modifier = Modifier.offset(y = (6).dp)
                    )
                    BasicText(
                        text = category.topic,
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
                    painter = rememberAsyncImagePainter(model = topicSImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
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
                    TopicCard(
                        category = topic.category,
                        numberOfSunnah = topic.count,
                        topicSImage = topic.imageRes
                    )
                }
            }
        }
    }
}

@Composable
fun PreviewSizes(
    content: @Composable (Int) -> Unit
) {
    listOf(360, 600, 840).forEach { width ->
        content(width)
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

