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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.common.states.PreviewWrapper
import com.ryen.sunnah_alhadi.presentation.common.states.SunnahPreview
import com.ryen.sunnah_alhadi.presentation.util.DynamicAppTypography
import com.ryen.sunnah_alhadi.presentation.util.DynamicContentTypography
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicDimensions


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
            .clip(RoundedCornerShape(25))
            .background(Color.Yellow)
    ) {
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color(0xFFB0D6FF), CircleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$numberOfSunnah",
                        style = DynamicAppTypography.labelSmall()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.sunnah_and_manner_of),
                    style = DynamicAppTypography.labelSmall().copy(
                        fontSize = DynamicAppTypography.labelSmall().fontSize * 0.9
                    )
                )
                BasicText(
                    text = categoryName,
                    style = DynamicContentTypography.topicHeading().copy(
                        lineHeight = DynamicContentTypography.topicHeading().lineHeight * 0.7
                    ),
                    maxLines = 2,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = DynamicContentTypography.topicHeading().fontSize * .6,
                        maxFontSize = DynamicContentTypography.topicHeading().fontSize
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



@Composable
fun TopicScreen(topics: List<TopicUiModel>) {
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
                text = "Explore Topics",
                style = DynamicContentTypography.topicHeading(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
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
        TopicScreen(topics = previewDummyTopics())
    }
    PreviewWrapper(widthDp = 600) {
        TopicScreen(topics = previewDummyTopics())
    }
    PreviewWrapper(widthDp = 840) {
        TopicScreen(topics = previewDummyTopics())
    }
}


fun previewDummyTopics() = listOf(
    TopicUiModel("Visiting Graveyard", 12, R.drawable.ic_launcher_background),
    TopicUiModel("Speaking", 8, R.drawable.ic_launcher_background),
    TopicUiModel("Greeting", 10, R.drawable.ic_launcher_background),
    TopicUiModel("Helping", 6, R.drawable.ic_launcher_background)
)



@Preview(showBackground = true)
@Composable
fun TopicCardPreview() {
    PreviewWrapper(widthDp = 360) {
        TopicCard(
            categoryName = "Walking",
            numberOfSunnah = 12,
            topicSImage = R.drawable.ic_launcher_background
        )
    }
}





data class TopicUiModel(
    val name: String,
    val count: Int,
    @DrawableRes val imageRes: Int
)

