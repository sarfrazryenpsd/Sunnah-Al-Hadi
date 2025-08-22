package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.navigation.AllTopic
import com.ryen.sunnah_alhadi.presentation.navigation.Browse
import com.ryen.sunnah_alhadi.presentation.navigation.Home
import com.ryen.sunnah_alhadi.presentation.navigation.Preferences
import com.ryen.sunnah_alhadi.presentation.navigation.Topic
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun ScreenHeaderSection(
    modifier: Modifier = Modifier,
    userName: String = "",
    topic: String = "",
    screen: NavKey,
) {
    val screenHeader = when (screen) {
        is Home -> {
            ScreenHeader(
                title = userName.ifBlank { "Friend" }.uppercase(),
                subtitle = stringResource(R.string.greeting),
                titleStyle = MaterialTheme.appTypography.displayName,
                subtitleStyle = MaterialTheme.appTypography.browseSubtitle
            )
        }
        is Browse -> {
            ScreenHeader(
                title = "SUNNAH",
                subtitle = "FIND GUIDANCE IN",
                titleStyle = MaterialTheme.appTypography.browseTitle,
                subtitleStyle = MaterialTheme.appTypography.browseSubtitle
            )
        }
        is AllTopic -> {
            ScreenHeader(
                title = "Topics",
                subtitle = "Explore All",
                titleStyle = MaterialTheme.appTypography.browseTitle,
                subtitleStyle = MaterialTheme.appTypography.topicsSubtitle
            )
        }
        is Topic -> {
            ScreenHeader(
                title = topic,
                subtitle = "Sunnah And Manner Of",
                titleStyle = MaterialTheme.appTypography.browseTitle.copy(
                    lineHeight = 48.sp
                ),
                subtitleStyle = MaterialTheme.appTypography.topicsSubtitle
            )
        }
        else -> {
            ScreenHeader(
                title = "Settings",
                subtitle = "",
                titleStyle = MaterialTheme.appTypography.settings,
                subtitleStyle = MaterialTheme.appTypography.topicsSubtitle
            )
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Static Arabic greeting
        if(screen !is Preferences){
            Text(
                text = screenHeader.subtitle,
                style = screenHeader.subtitleStyle,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start
            )
        }


        // Dynamic username from onboarding
        Text(
            text = screenHeader.title,
            style = screenHeader.titleStyle,
            //lineHeight = 32.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start
        )
    }
}

private data class ScreenHeader(
    val title: String,
    val subtitle: String,
    val titleStyle: TextStyle,
    val subtitleStyle: TextStyle
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun HomeGreetingPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {

        ScreenHeaderSection(
            screen = Home,
            userName = "Sarfraz",
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun HomeGreetingPrev2() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {

        ScreenHeaderSection(
            screen = Topic("123"),
            topic = "Treating Relatives With Kindness",
            userName = "Sarfraz",
        )
    }
}