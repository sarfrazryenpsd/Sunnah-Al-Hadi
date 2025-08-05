@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.ryen.sunnah_alhadi.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.navEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.components.overlay.CardOverlay
import com.ryen.sunnah_alhadi.presentation.components.overlay.OnboardingOverlayContent
import com.ryen.sunnah_alhadi.presentation.components.overlay.SotdCardContainer
import com.ryen.sunnah_alhadi.presentation.screens.home.HomeScreen
import com.ryen.sunnah_alhadi.presentation.screens.preferences.PreferencesScreen
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavigation(
    showOnboarding: Boolean,
    shouldShowSotd: Boolean = false,
    sotdId: String? = null // ✅ Now receives sotdId
) {
    // Screen size detection
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    // Navigation state
    val backStack = rememberNavBackStack(Home)
    val topLevelDestinations = listOf(Home, Browse, Preferences)

    // ✅ Sequential overlay state management
    var showOnboardingState by remember { mutableStateOf(showOnboarding) }
    var showSotdState by remember { mutableStateOf(false) }
    var currentSotdId by remember { mutableStateOf<String?>(null) }

    // ✅ Handle notification launch immediately if from notification
    LaunchedEffect(shouldShowSotd, sotdId) {
        if (shouldShowSotd && sotdId != null) {
            currentSotdId = sotdId
            if (!showOnboardingState) {
                // Show SOTD immediately if onboarding not needed
                showSotdState = true
            }
        }
    }

    // ✅ Show SOTD after onboarding completes (if needed)
    LaunchedEffect(showOnboardingState) {
        if (!showOnboardingState && shouldShowSotd) {
            delay(300) // Smooth transition
            showSotdState = true
        }
    }

    // ✅ Onboarding overlay (highest priority)
    CardOverlay(
        showOverlay = showOnboardingState,
        onDismiss = { showOnboardingState = false },
        overlayContent = {
            OnboardingOverlayContent(
                onDismiss = { showOnboardingState = false }
            )
        }
    ) {
        // ✅ SOTD overlay (shows after onboarding or immediately)
        CardOverlay(
            showOverlay = showSotdState,
            onDismiss = {
                showSotdState = false
                currentSotdId = null
            },
            overlayContent = {
                SotdCardContainer(
                    sotdId = currentSotdId,
                    onDismiss = {
                        showSotdState = false
                        currentSotdId = null
                    }
                )
            }
        ) {
            // Main app content
            if (isCompact) {
                CompactScreenLayout(
                    backStack = backStack,
                    topLevelDestinations = topLevelDestinations,
                    shouldShowSotd = shouldShowSotd,
                    onSotdRequested = {
                        showSotdState = true
                    }
                )
            } else {
                ExpandedScreenLayout(
                    backStack = backStack,
                    topLevelDestinations = topLevelDestinations,
                    shouldShowSotd = shouldShowSotd,
                    onSotdRequested = {
                        showSotdState = true
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CompactScreenLayout(
    backStack: SnapshotStateList<NavKey>,
    topLevelDestinations: List<NavKey>,
    shouldShowSotd: Boolean,
    onSotdRequested: () -> Unit
) {
    // State for toolbar expansion
    var isToolbarExpanded by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            AppTopBar(
                isTopLevel = backStack.lastOrNull() in topLevelDestinations,
                onNavigateBack = { backStack.removeLastOrNull() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main navigation content
            NavigationContent(
                backStack = backStack,
                shouldShowSotd = shouldShowSotd,
                onSotdRequested = onSotdRequested,
                modifier = Modifier.fillMaxSize()
            )

            // Bottom floating toolbar
            BottomFloatingToolbar(
                expanded = isToolbarExpanded,
                topLevelDestinations = topLevelDestinations,
                backStack = backStack,
                onItemSelected = { isToolbarExpanded = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun ExpandedScreenLayout(
    backStack: SnapshotStateList<NavKey>,
    topLevelDestinations: List<NavKey>,
    shouldShowSotd: Boolean,
    onSotdRequested: () -> Unit
) {
    // State for navigation rail expansion
    var isNavRailExpanded by rememberSaveable { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        SideNavigationRail(
            expanded = isNavRailExpanded,
            topLevelDestinations = topLevelDestinations,
            currentDestination = backStack.lastOrNull(),
            onToggleExpansion = { isNavRailExpanded = !isNavRailExpanded },
            onDestinationSelected = { destination ->
                backStack.clear()
                backStack.add(destination)
            }
        )

        NavigationContent(
            backStack = backStack,
            shouldShowSotd = shouldShowSotd,
            onSotdRequested = onSotdRequested,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun NavigationContent(
    backStack: SnapshotStateList<NavKey>,
    shouldShowSotd: Boolean,
    onSotdRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val twoPaneStrategy = remember { TwoPaneSceneStrategy<NavKey>() }

    SharedTransitionLayout {
        // Shared transition decorator for smooth animations
        val sharedEntryDecorator = navEntryDecorator<NavKey> { entry ->
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
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
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryProvider = createEntryProvider(backStack, shouldShowSotd, onSotdRequested)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomFloatingToolbar(
    expanded: Boolean,
    topLevelDestinations: List<NavKey>,
    backStack: SnapshotStateList<NavKey>,
    onItemSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val toolbarColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()

    HorizontalFloatingToolbar(
        expanded = expanded,
        modifier = modifier,
        colors = toolbarColors,
        content = {
            AppBarRow(
                overflowIndicator = { menuState ->
                    IconButton(
                        onClick = {
                            if (menuState.isExpanded) {
                                menuState.dismiss()
                            } else {
                                menuState.show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                }
            ) {
                topLevelDestinations.forEach { destination ->
                    clickableItem(
                        onClick = {
                            backStack.clear()
                            backStack.add(destination)
                            onItemSelected()
                        },
                        icon = {
                            Icon(
                                imageVector = getDestinationIcon(destination),
                                contentDescription = destination.toString()
                            )
                        },
                        label = destination.toString()
                    )
                }
            }
        }
    )
}

@Composable
private fun SideNavigationRail(
    expanded: Boolean,
    topLevelDestinations: List<NavKey>,
    currentDestination: NavKey?,
    onToggleExpansion: () -> Unit,
    onDestinationSelected: (NavKey) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .width(if (expanded) 240.dp else 80.dp)
            .fillMaxHeight(),
        header = {
            FilledIconButton(
                onClick = onToggleExpansion,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
                    contentDescription = "Toggle Navigation Rail"
                )
            }
        }
    ) {
        topLevelDestinations.forEach { destination ->
            val selected = currentDestination == destination
            NavigationRailItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = getDestinationIcon(destination),
                        contentDescription = destination.toString()
                    )
                },
                label = if (expanded) {
                    { Text(destination.toString()) }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun AppTopBar(
    isTopLevel: Boolean,
    onNavigateBack: () -> Unit
) {
    CustomTopBar(
        isTopLevel = isTopLevel,
        onNavigateBack = onNavigateBack,
        onOrbClick = { },
        onInfoClick = { }
    )
}

// Helper function to get destination icons
private fun getDestinationIcon(destination: NavKey): ImageVector {
    return when (destination) {
        is Home -> Icons.Filled.Home
        is Browse -> Icons.AutoMirrored.Filled.List
        is Preferences -> Icons.Filled.Person
        else -> Icons.Filled.Info
    }
}

// Entry provider factory function
private fun createEntryProvider(backStack: SnapshotStateList<NavKey>, shouldShowSotd: Boolean, onSotdRequested: () -> Unit) = entryProvider<NavKey> {
    entry<Home>(
        metadata = TwoPaneScene.twoPane()
    ) {
        HomeScreen(
            shouldShowSotd = shouldShowSotd,
            onSotdRequested = onSotdRequested,
            modifier = Modifier.fillMaxSize()
        )

        //ContentGreen("HomeScreen")
    }

    entry<Topic>(
        metadata = TwoPaneScene.twoPane()
    ) { topic ->
        ContentBase(
            "Topic: ${topic.categoryId}",
            Modifier.background(Color.Blue.copy(alpha = 0.1f))
        ) {
            Text("This is topic ${topic.categoryId}")
            Button(onClick = { backStack.removeLastOrNull() }) {
                Text("Go Back")
            }
        }
    }

    entry<AllTopic>(
        metadata = TwoPaneScene.twoPane()
    ) {
        ContentBase(
            "All Topics",
            Modifier.background(Color.Cyan)
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val list = (0..75).map { it.toString() }
                items(list) { item ->
                    Text(
                        text = "Topic $item",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { backStack.addTopicRoute(item.toInt()) }
                    )
                }
            }
        }
    }

    entry<Browse> {
        ContentBase(
            "Browse",
            Modifier.background(Color.Yellow)
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val list = (0..75).map { it.toString() }
                items(list) { item ->
                    Text(
                        text = "Browse Item $item",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { backStack.addTopicRoute(item.toInt()) }
                    )
                }
            }
        }
    }

    entry<Preferences> {
        PreferencesScreen()
    }
}

// Extension function for adding topic routes
private fun SnapshotStateList<NavKey>.addTopicRoute(topicId: Int) {
    val topicRoute = Topic("$topicId")
    if (!contains(topicRoute)) {
        add(topicRoute)
    }
}

/**
 * Placeholder composable for red-themed content.
 */
@Composable
fun ContentRed(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

/**
 * Placeholder composable for base content with customizable background.
 */
@Composable
fun ContentBase(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

/**
 * Placeholder composable for green-themed content.
 */
@Composable
fun ContentGreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}