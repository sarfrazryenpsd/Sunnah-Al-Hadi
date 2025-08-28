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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.common.ScreenHeaderSection
import com.ryen.sunnah_alhadi.presentation.common.SunnahGridCardContainer
import com.ryen.sunnah_alhadi.presentation.components.DisclaimerDialog
import com.ryen.sunnah_alhadi.presentation.components.SunnahPager
import com.ryen.sunnah_alhadi.presentation.components.cards.GlowingCard
import com.ryen.sunnah_alhadi.presentation.components.cards.HomeSunnahCard
import com.ryen.sunnah_alhadi.presentation.components.cards.SpecialArabicCard
import com.ryen.sunnah_alhadi.presentation.components.cards.TopicCard
import com.ryen.sunnah_alhadi.presentation.navigation.Home
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSotdRequested: (SotdOverlayRequest) -> Unit = {},
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

    // ✅ NEW: Listen to SOTD overlay requests
    LaunchedEffect(Unit) {
        viewModel.sotdOverlayRequest.collect { request ->
            onSotdRequested(request)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.AutoShowSotdCheck)
    }

    val onEventCallback = remember { viewModel::onEvent }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Initializing",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            else -> {
                HomeScreenContent(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToAllTopics = onNavigateToAllTopics,
                    modifier = modifier
                )

                if (uiState.isPagerVisible) {
                    SunnahPager(
                        sunnahs = uiState.recentSotd,
                        initialPage = uiState.selectedSunnahIndex,
                        onDismiss = { viewModel.onEvent(HomeEvent.ClosePager) },
                        onPageChanged = { index ->
                            onEventCallback(HomeEvent.PagerPageChanged(index))
                        },
                        onBookmarkClick = { sunnahId ->
                            onEventCallback(HomeEvent.ToggleBookmark(sunnahId))
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAllTopics: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    Box(modifier = modifier.fillMaxSize()) {
        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 48.dp)
        ) {
            item {
                CustomTopBar(
                    actionContents = {
                        ActionItems(
                            onOrbClick = { onEvent(HomeEvent.ToggleSotd) },
                            onInfoClick = { onEvent(HomeEvent.ToggleDisclaimer) }
                        )
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            // Greeting Section
            item {
                ScreenHeaderSection(
                    userName = uiState.username,
                    screen = Home,
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
                    ){
                        SpecialArabicCard()
                    }
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
                                        .clip(RoundedCornerShape(24.dp))
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

            if (uiState.recentSotd.isNotEmpty()) {
                // Recent SOTDs Section
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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    SunnahGridCardContainer(
                        sunnahs = uiState.recentSotd,
                        onSunnahClick = { idx -> onEvent(HomeEvent.SunnahCardClicked(idx)) },
                        screenSize = screenSize,
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

@Composable
private fun ActionItems(
    onOrbClick: () -> Unit,
    onInfoClick: () -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sotd_dark))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onOrbClick() }
        )
        Icon(
            painter = painterResource(id = R.drawable.interface_info0),
            contentDescription = "Info",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onInfoClick() }
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@PreviewFontScale
@PreviewScreenSizes
@Preview(showBackground = true)
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


