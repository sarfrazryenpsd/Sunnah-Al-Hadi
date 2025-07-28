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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.common.PreviewWrapper
import com.ryen.sunnah_alhadi.presentation.common.SunnahPreview
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions
import com.ryen.sunnah_alhadi.ui.theme.appTypography


@Composable
fun TopicCard(
    categoryName: String,
    numberOfSunnah: Int,
    @DrawableRes topicSImage: Int,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalDynamicDimensions.current

    Box(
        modifier = modifier
            .width(dimensions.topicCardWidth)
            .height(dimensions.topicCardHeight)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Yellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 8.dp, start = 8.dp)
                    .background(Color(0xFFB0D6FF), CircleShape)
            ) {
                Text(
                    text = "$numberOfSunnah",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(dimensions.cardPadding)
                        .weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.sunnah_and_manner_of),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.9
                        )
                    )
                    BasicText(
                        text = categoryName,
                        style = MaterialTheme.appTypography.topicHeading.copy(
                            lineHeight = MaterialTheme.appTypography.topicHeading.lineHeight * 0.7
                        ),
                        maxLines = 2,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = MaterialTheme.appTypography.topicHeading.fontSize * .6,
                            maxFontSize = MaterialTheme.appTypography.topicHeading.fontSize
                        )
                    )
                }

                Image(
                    painter = rememberAsyncImagePainter(model = topicSImage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(dimensions.imageSize)

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
                        categoryName = topic.name,
                        numberOfSunnah = topic.count,
                        topicSImage = topic.imageRes
                    )
                }
            }
        }
    }
}


@SunnahPreview
@Composable
fun TopicScreenPreview() {
    PreviewWrapper(widthDp = 360) {
        TopicScreen(topics = previewDummyTopics(), userName = "Sarfraz")
    }
    PreviewWrapper(widthDp = 600) {
        TopicScreen(topics = previewDummyTopics(), userName = "Sarfraz")
    }
    PreviewWrapper(widthDp = 840) {
        TopicScreen(topics = previewDummyTopics(), userName = "Sarfraz")
    }
}


fun previewDummyTopics() = listOf(
    TopicUiModel("Applying Oil and Combing Hair", 12, R.drawable.ic_launcher_background),
    TopicUiModel("Treating Relatives With Kindness", 8, R.drawable.ic_launcher_background),
    TopicUiModel("Waking Up and Sleeping", 10, R.drawable.ic_launcher_background),
    TopicUiModel("Helping", 6, R.drawable.ic_launcher_background)
)





data class TopicUiModel(
    val name: String,
    val count: Int,
    @DrawableRes val imageRes: Int
)

