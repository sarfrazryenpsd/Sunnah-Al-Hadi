package com.ryen.sunnah_alhadi.presentation.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.components.DisclaimerDialog
import com.ryen.sunnah_alhadi.presentation.components.HomeGreetingSection
import com.ryen.sunnah_alhadi.presentation.components.cards.HomeSunnahCard
import com.ryen.sunnah_alhadi.presentation.components.cards.SpecialArabicCard
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCompactCard
import com.ryen.sunnah_alhadi.presentation.components.cards.TopicCard
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSotdRequested: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.showSotd) {
        if (uiState.showSotd) {
            onSotdRequested()
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )

}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Greeting Section
            item {
                HomeGreetingSection(
                    userName = uiState.username,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Special Arabic Card
            item {
                SpecialArabicCard(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Featured Categories Section
            if (uiState.featuredCategories.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Featured Categories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            TextButton(
                                onClick = { onEvent(HomeEvent.NavigateToAllTopics) }
                            ) {
                                Text("View All")
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            items(
                                items = uiState.featuredCategories,
                                key = { it.id }
                            ) { category ->
                                TopicCard(
                                    categoryName = category.topic,
                                    numberOfSunnah = uiState.sunnahCount[category.id] ?: 0,
                                    topicSImage = R.drawable.interface_darklight,
                                    modifier = Modifier
                                        .clickable {
                                            onEvent(HomeEvent.NavigateToTopic(category.id))
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // Home Sunnah Section
            item {
                HomeSunnahCard(
                    sunnah = uiState.homeSunnah,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Recent SOTDs Section
            if (uiState.recentSotd.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Recently Viewed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(
                                items = uiState.recentSotd,
                                key = { it.id }
                            ) { sunnah ->
                                SunnahCompactCard(
                                    title = sunnah.title,
                                    extraIcons = buildList {
                                        // Add extra content icon if available
                                        if (sunnah.extra != null) {
                                            add {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Has extra content",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        // Add verses icon if contains verses
                                        if (sunnah.body.any {
                                                it.subtype.equals(ArabicSubtype.VERSE.name, ignoreCase = true)
                                            }) {
                                            add {
                                                Icon(
                                                    painter = painterResource(R.drawable.ec_verse),
                                                    contentDescription = "Contains verses",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }

                                        if (sunnah.body.any {
                                                it.subtype.equals(ArabicSubtype.SUPPLICATION.name, ignoreCase = true)
                                            }) {
                                            add {
                                                Icon(
                                                    painter = painterResource(R.drawable.ec_supplication),
                                                    contentDescription = "Contains verses",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }

                                        // Add reference icon if has reference
                                        if (sunnah.references != null) {
                                            add {
                                                Icon(
                                                    painter = painterResource(R.drawable.ec_reference),
                                                    contentDescription = "Has reference",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                        }
                                    },
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier
                                        .width(200.dp)
                                        .clickable {
                                            onEvent(HomeEvent.OpenSunnah(sunnah.id))
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // Loading state
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Error state
            uiState.error?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "Something went wrong",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Bottom spacing for navigation
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        /*// SOTD Overlay
        if (uiState.showSotd && uiState.sotd != null) {
            SotdOverlay(
                sunnah = uiState.sotd,
                onDismiss = { onEvent(HomeEvent.DismissSotd) },
                onReadMore = { onEvent(HomeEvent.OpenSunnah(uiState.sotd.id)) }
            )
        }*/

        // Disclaimer Dialog
        if (uiState.showDisclaimer) {
            DisclaimerDialog(
                onDismiss = { onEvent(HomeEvent.ToggleDisclaimer) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@PreviewFontScale
@PreviewScreenSizes
@Composable
private fun HomeScreenContentPrev() {
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.MEDIUM
    ){
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
        ) {
            HomeScreenContent(
                uiState = HomeUiState(
                    username = "Sarfraz",
                    featuredCategories = listOf(
                        Category(
                            id = 1,
                            topic = "Walking"
                        ),Category(
                            id = 2,
                            topic = "Walking"
                        ),Category(
                            id = 3,
                            topic = "Walking"
                        ),Category(
                            id = 4,
                            topic = "Walking"
                        ),Category(
                            id = 5,
                            topic = "Walking"
                        )
                    )

                ),
                onEvent = {}
            )
        }
    }
}