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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.common.SunnahCompactCardContainer
import com.ryen.sunnah_alhadi.presentation.components.DisclaimerDialog
import com.ryen.sunnah_alhadi.presentation.components.HomeGreetingSection
import com.ryen.sunnah_alhadi.presentation.components.cards.GlowingCard
import com.ryen.sunnah_alhadi.presentation.components.cards.HomeSunnahCard
import com.ryen.sunnah_alhadi.presentation.components.cards.SpecialArabicCard
import com.ryen.sunnah_alhadi.presentation.components.cards.SunnahCompactCard
import com.ryen.sunnah_alhadi.presentation.components.cards.TopicCard
import com.ryen.sunnah_alhadi.presentation.screens.allTopics.AllTopicsUiEvent
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography
import org.checkerframework.checker.units.qual.s

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSotdRequested: () -> Unit = {},
    onNavigateToAllTopics: () -> Unit = {},
    onNavigateToTopic: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is HomeEvent.NavigateToTopic -> {
                    onNavigateToTopic(event.categoryId)
                }

                else -> Unit
            }
        }
    }

    LaunchedEffect(uiState.showSotd) {
        if (uiState.showSotd) {
            onSotdRequested()
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToAllTopics = onNavigateToAllTopics,
        modifier = modifier
    )

}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAllTopics: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            item {
                CustomTopBar(
                    onOrbClick = {},
                    onInfoClick = {}
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            // Greeting Section
            item {
                HomeGreetingSection(
                    userName = uiState.username,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Special Arabic Card
            item {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                    GlowingCard(
                        glowingColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth(),
                        cornersRadius = 16.dp,
                        glowingRadius = 24.dp,
                        xShifting = 0.dp,
                        yShifting = 0.dp,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Featured Categories Section
            if (uiState.featuredCategories.isNotEmpty()) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Featured Topics",
                                style = MaterialTheme.appTypography.featuredTopics,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "See All",
                                style = MaterialTheme.appTypography.seeAll,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    onNavigateToAllTopics()
                                }
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            item {
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            items(
                                items = uiState.featuredCategories,
                                key = { it.id }
                            ) { category ->
                                TopicCard(
                                    category = category,
                                    numberOfSunnah = uiState.sunnahCount[category.id] ?: 0,
                                    topicSImage = CategoryUtils.categoryImageMap[category.id]
                                        ?: R.drawable.ec_warning,
                                    modifier = Modifier
                                        .clickable {
                                            onEvent(HomeEvent.NavigateToTopic(category.id))
                                        }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }

            // Home Sunnah Section
            item {
                HomeSunnahCard(
                    sunnah = uiState.homeSunnah,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Recent",
                        style = MaterialTheme.appTypography.featuredTopics,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sunnah Of The Day",
                        style = MaterialTheme.appTypography.notificationSubtitle,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Recent SOTDs Section
            if (uiState.recentSotd.isNotEmpty()) {
                item {
                    /*Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                                it.subtype.equals(
                                                    ArabicSubtype.VERSE.name,
                                                    ignoreCase = true
                                                )
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
                                                it.subtype.equals(
                                                    ArabicSubtype.SUPPLICATION.name,
                                                    ignoreCase = true
                                                )
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
                    }*/

                    SunnahCompactCardContainer(
                        sunnahs = uiState.recentSotd,
                        onSunnahClick = { onEvent(HomeEvent.OpenSunnah(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
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
        LocalScreenSize provides ScreenSize.COMPACT
    ) {
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
                        ), Category(
                            id = 2,
                            topic = "Walking"
                        ), Category(
                            id = 3,
                            topic = "Walking"
                        ), Category(
                            id = 4,
                            topic = "Walking"
                        ), Category(
                            id = 5,
                            topic = "Walking"
                        )
                    )

                ),
                onEvent = {},
                onNavigateToAllTopics = {},
            )
        }
    }
}