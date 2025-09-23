@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.ryen.sunnah_alhadi.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.navEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.presentation.common.LoadingIndicator
import com.ryen.sunnah_alhadi.presentation.components.overlay.CardOverlay
import com.ryen.sunnah_alhadi.presentation.components.overlay.OnboardingOverlayContent
import com.ryen.sunnah_alhadi.presentation.components.overlay.SotdCardContainer
import com.ryen.sunnah_alhadi.presentation.screens.allTopics.AllTopicsScreen
import com.ryen.sunnah_alhadi.presentation.screens.browse.BrowseScreen
import com.ryen.sunnah_alhadi.presentation.screens.home.HomeScreen
import com.ryen.sunnah_alhadi.presentation.screens.home.SotdOverlayRequest
import com.ryen.sunnah_alhadi.presentation.screens.preferences.PreferencesScreen
import com.ryen.sunnah_alhadi.presentation.screens.topic.TopicScreen
import com.ryen.sunnah_alhadi.presentation.util.PagerVisibilityState
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavigation(
    showOnboarding: Boolean,
    isFromNotification: Boolean = false,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val topLevelDestinations = remember { listOf(Home, Browse, Preferences) }
    val backStack = rememberNavBackStack(Home)

    // ✅ Sequential overlay state management
    var onboardingCompleted by rememberSaveable { mutableStateOf(false) }
    var currentOverlay by rememberSaveable { mutableStateOf<OverlayType?>(null) }
    var isInitializing by remember { mutableStateOf(true) }
    val twoPaneStrategy = remember { TwoPaneSceneStrategy<NavKey>() }

    // ✅ Handle initial loading
    LaunchedEffect(showOnboarding) {
        isInitializing = false
    }

    // ✅ Initialize overlay state
    LaunchedEffect(showOnboarding) {
        currentOverlay = if (showOnboarding) {
            OverlayType.ONBOARDING
        } else {
            null
        }
    }

    // ✅ Handle post-onboarding flow
    LaunchedEffect(onboardingCompleted, isFromNotification) {
        if (!isInitializing && onboardingCompleted && isFromNotification) {
            delay(300) // Smooth transition
            currentOverlay = OverlayType.SOTD_FROM_NOTIFICATION
        }
    }

    // ✅ SOTD overlay request handler
    val handleSotdRequest = remember(currentOverlay) {
        { request: SotdOverlayRequest ->
            when (request) {
                is SotdOverlayRequest.AutoShow -> {
                    // Only auto-show if no other overlay is active
                    if (currentOverlay == null) {
                        currentOverlay = OverlayType.SOTD_AUTO_SHOW
                    }
                }

                is SotdOverlayRequest.FromNotification -> {
                    currentOverlay = OverlayType.SOTD_FROM_NOTIFICATION
                }

                is SotdOverlayRequest.Manual -> {
                    currentOverlay = OverlayType.SOTD_MANUAL
                }
            }
        }
    }

    if (isInitializing) {
        LoadingIndicator()
    }

    // ✅ Overlay management
    CardOverlay(
        showOverlay = currentOverlay != null,
        onDismiss = { currentOverlay == null },
        overlayContent = {
            when (currentOverlay) {
                OverlayType.ONBOARDING -> {
                    OnboardingOverlayContent(
                        onDismiss = {
                            currentOverlay = null
                        },
                        onComplete = {
                            onboardingCompleted = true
                            currentOverlay = null
                        }
                    )
                }

                OverlayType.SOTD_FROM_NOTIFICATION,
                OverlayType.SOTD_AUTO_SHOW,
                OverlayType.SOTD_MANUAL -> {
                    SotdCardContainer(
                        isFromNotification = currentOverlay == OverlayType.SOTD_FROM_NOTIFICATION,
                        onDismiss = {
                            currentOverlay = null
                        }
                    )
                }

                null -> { /* No overlay */
                }
            }
        }
    ) {
        // Main app content
        if (isCompact) {
            CompactScreenLayout(
                backStack = backStack,
                windowSizeClass = windowSizeClass,
                topLevelDestinations = topLevelDestinations,
                twoPaneStrategy = twoPaneStrategy,
                onSotdRequested = handleSotdRequest
            )
        } else {
            ExpandedScreenLayout(
                backStack = backStack,
                windowSizeClass = windowSizeClass,
                topLevelDestinations = topLevelDestinations,
                twoPaneStrategy = twoPaneStrategy,
                onSotdRequested = handleSotdRequest
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CompactScreenLayout(
    backStack: SnapshotStateList<NavKey>,
    windowSizeClass: WindowSizeClass,
    twoPaneStrategy: TwoPaneSceneStrategy<NavKey>,
    topLevelDestinations: List<NavKey>,
    onSotdRequested: (SotdOverlayRequest) -> Unit
) {
    val baseBottomBarVisible = backStack.lastOrNull()?.let { it in topLevelDestinations } ?: false
    val isPagerVisible by PagerVisibilityState.isPagerVisible.collectAsStateWithLifecycle()
    val bottomBarAnimation = MaterialTheme.motionScheme

    // Bottom bar is visible if we're on a top level destination AND pager is not visible
    val isBottomBarVisible = baseBottomBarVisible && !isPagerVisible

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main navigation content
        NavigationContent(
            backStack = backStack,
            onSotdRequested = onSotdRequested,
            windowSizeClass = windowSizeClass,
            twoPaneStrategy = twoPaneStrategy,
            topLevelDestinations = topLevelDestinations,
            modifier = Modifier.fillMaxSize()
        )
        // Custom bottom toolbar
        AnimatedVisibility(
            visible = isBottomBarVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = bottomBarAnimation.slowEffectsSpec()
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 100)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            CustomBottomBar(
                topLevelDestinations = topLevelDestinations,
                backStack = backStack,
                onItemSelected = { destination ->
                    backStack.clear()
                    backStack.add(destination)
                }
            )
        }

    }

}

@Composable
private fun ExpandedScreenLayout(
    backStack: SnapshotStateList<NavKey>,
    windowSizeClass: WindowSizeClass,
    twoPaneStrategy: TwoPaneSceneStrategy<NavKey>,
    topLevelDestinations: List<NavKey>,
    onSotdRequested: (SotdOverlayRequest) -> Unit
) {

    Row(modifier = Modifier.fillMaxSize()) {
        SideNavigationRail(
            topLevelDestinations = topLevelDestinations,
            currentDestination = backStack.lastOrNull(),
            onDestinationSelected = { destination ->
                backStack.clear()
                backStack.add(destination)
            }
        )

        NavigationContent(
            backStack = backStack,
            windowSizeClass = windowSizeClass,
            onSotdRequested = onSotdRequested,
            twoPaneStrategy = twoPaneStrategy,
            topLevelDestinations = topLevelDestinations,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavigationContent(
    backStack: SnapshotStateList<NavKey>,
    windowSizeClass: WindowSizeClass,
    onSotdRequested: (SotdOverlayRequest) -> Unit,
    twoPaneStrategy: TwoPaneSceneStrategy<NavKey>,
    topLevelDestinations: List<NavKey>,
    modifier: Modifier = Modifier
) {
    val navAnimation = MaterialTheme.motionScheme
    SharedTransitionLayout {
        // Shared transition decorator for smooth animations
        val sharedEntryDecorator = navEntryDecorator<NavKey> { entry ->
            val isCompact = !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

            if (isCompact) {
                entry.Content()
            } else {
                Box(
                    Modifier.sharedElement(
                        rememberSharedContentState(entry.contentKey),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    )
                ) {
                    entry.Content()
                }
            }
        }

        NavDisplay(
            modifier = modifier,
            backStack = backStack,
            onBack = { keysToRemove -> repeat(keysToRemove) { backStack.removeLastOrNull() } },
            entryDecorators = listOf(
                sharedEntryDecorator,
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator()
            ),
            sceneStrategy = twoPaneStrategy,
            transitionSpec = {
                if(backStack.lastOrNull() !in topLevelDestinations) {
                    slideInHorizontally(animationSpec = navAnimation.defaultSpatialSpec(), initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(animationSpec = navAnimation.defaultSpatialSpec(),targetOffsetX = { -it })
                } else {
                    // no animation → just return Enter + Exit with 0 duration
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                }
            },
            popTransitionSpec = {
                slideInHorizontally(animationSpec = navAnimation.defaultSpatialSpec(),initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(animationSpec = navAnimation.defaultSpatialSpec(),targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(animationSpec = navAnimation.defaultSpatialSpec(),initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(animationSpec = navAnimation.defaultSpatialSpec(),targetOffsetX = { it })
            },
            entryProvider = createEntryProvider(backStack, onSotdRequested)
        )
    }
}

@Composable
fun CustomBottomBar(
    topLevelDestinations: List<NavKey>,
    backStack: SnapshotStateList<NavKey>,
    onItemSelected: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDestination = backStack.lastOrNull()

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .zIndex(1f)
            .width(210.dp)
            .height(70.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(32),
            shadowElevation = 18.dp
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(70.dp)
                    .width(210.dp)
            ) {
                topLevelDestinations.forEach { destination ->
                    val selected = currentDestination == destination
                    val selectedIconColor =
                        if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    val bgColor =
                        if (selected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32))
                            .weight(1f)
                            .height(80.dp)
                            .background(bgColor)
                            .clickable { onItemSelected(destination) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(getDestinationIcon(destination)),
                            contentDescription = destination.toString(),
                            tint = selectedIconColor,
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SideNavigationRail(
    topLevelDestinations: List<NavKey>,
    currentDestination: NavKey?,
    onDestinationSelected: (NavKey) -> Unit
) {

    NavigationRail(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .width(80.dp)
            .fillMaxHeight(),
        header = {

        }
    ) {
        topLevelDestinations.forEach { destination ->
            val selected = currentDestination == destination
            NavigationRailItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        painter = painterResource(getDestinationIcon(destination)),
                        contentDescription = destination.toString(),
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        }
    }
}


// Helper function to get destination drawables (for CustomBottomBar)
private fun getDestinationIcon(destination: NavKey): Int {
    return when (destination) {
        is Home -> R.drawable.interface_home
        is Browse -> R.drawable.interface_browse
        is Preferences -> R.drawable.interface_preferences
        else -> R.drawable.interface_home // fallback
    }
}

// Entry provider factory function
private fun createEntryProvider(
    backStack: SnapshotStateList<NavKey>,
    onSotdRequested: (SotdOverlayRequest) -> Unit
) =
    entryProvider<NavKey> {
        entry<Home>(
            metadata = TwoPaneScene.twoPane()
        ) {
            HomeScreen(
                onSotdRequested = onSotdRequested,
                onNavigateToAllTopics = {
                    while (backStack.last() !is Home) backStack.removeLastOrNull()
                    if (AllTopic !in backStack) backStack.add(AllTopic)
                },
                onNavigateToTopic = { topicId ->
                    backStack.clearDuplicateTopic(topicId)
                    backStack.addTopicRoute(topicId)
                },
                modifier = Modifier.fillMaxSize()
            )

        }

        entry<Topic>(
            metadata = TwoPaneScene.twoPane()
        ) { topic ->
            TopicScreen(
                categoryId = topic.categoryId.toInt(),
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }

        entry<AllTopic>(
            metadata = TwoPaneScene.twoPane()
        ) {
            AllTopicsScreen(
                onNavigateToTopic = { topicId ->
                    backStack.clearDuplicateTopic(topicId)
                    backStack.addTopicRoute(topicId)
                },
                onNavigateBack = { backStack.removeLastOrNull() },
                modifier = Modifier.fillMaxSize(),
            )
        }

        entry<Browse> {
            BrowseScreen()
        }

        entry<Preferences> {
            PreferencesScreen()
        }
    }

// Extension function for adding topic routes
private fun SnapshotStateList<NavKey>.addTopicRoute(topicId: Int) {
    val topicRoute = Topic("$topicId")
    if (this.lastOrNull() == topicRoute) {
        // If the exact same topic is already the last one, do nothing.
        return
    }
    // If the topic exists elsewhere in the stack, remove its old instance first to bring it to the top.
    if (this.contains(topicRoute)) {
        this.remove(topicRoute)
    }
    this.add(topicRoute) // Add the new (or now moved) topic to the end of the stack.
}

private fun SnapshotStateList<NavKey>.clearDuplicateTopic(topicId: Int) {
    val newTopicKey = Topic("$topicId") // Create the key for the new topic
    val currentLastKey = this.lastOrNull()

    if (currentLastKey is Topic && currentLastKey != newTopicKey) {
        // If the current detail view is already a TopicScreen
        this.removeLastOrNull() // Remove the current TopicScreen from the stack
    }
}

enum class OverlayType {
    ONBOARDING,
    SOTD_FROM_NOTIFICATION,
    SOTD_AUTO_SHOW,
    SOTD_MANUAL
}
