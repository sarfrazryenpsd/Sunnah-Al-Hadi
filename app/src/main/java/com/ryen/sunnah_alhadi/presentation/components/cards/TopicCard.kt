package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.util.DynamicContentTypography
import com.ryen.sunnah_alhadi.ui.theme.DynamicLineHeightConfig
import com.ryen.sunnah_alhadi.ui.theme.DynamicTextConfig
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicLineHeightConfig
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicTextConfig
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.TypographyScales
import com.ryen.sunnah_alhadi.ui.theme.amiri

@Composable
fun TopicTexts(
    categoryName: String,
){
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.sunnah_and_manner_of),
            maxLines = 1,
            fontFamily = amiri,
            fontSize = 12.sp,
            lineHeight = 0.5.sp

        )
        Text(
            text = categoryName,
            style = DynamicContentTypography.topicHeading()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopicTextsPreview() {
    val dummyTextConfig = DynamicTextConfig(
        screenSize = ScreenSize.COMPACT,
        scaleFactors = TypographyScales.COMPACT
    )
    val dummyLineHeightConfig = DynamicLineHeightConfig(TypographyScales.COMPACT)

    CompositionLocalProvider(
        LocalDynamicTextConfig provides dummyTextConfig,
        LocalDynamicLineHeightConfig provides dummyLineHeightConfig
    ) {
        TopicTexts(
            categoryName = "Walking"
        )
    }
}
