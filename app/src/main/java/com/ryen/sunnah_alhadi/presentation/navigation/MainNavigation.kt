@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.ryen.sunnah_alhadi.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND



@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavigation() {
    val localNavSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope> =
        compositionLocalOf {
            throw IllegalStateException(
                "Unexpected access to LocalNavSharedTransitionScope. You must provide a " +
                        "SharedTransitionScope from a call to SharedTransitionLayout() or " +
                        "SharedTransitionScope()"
            )
        }

    // Determine screen size class
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    // State for navigation rail expansion and toolbar expansion
    var isNavRailExpanded by rememberSaveable { mutableStateOf(false) }
    var isToolbarExpanded by rememberSaveable { mutableStateOf(true) }

    // Navigation back stack for all destinations
    val backStack = rememberNavBackStack(Home)

    // Current top-level destination (derived from back stack)
    val currentNavKey = backStack.lastOrNull()

    /**
     * A [NavEntryDecorator] that applies shared element transitions dynamically:
     * - For compact screens: No sharedElement wrapper (handled by composables).
     * - For medium/large screens: Wraps entire content in sharedElement for two-pane layout.
     */
    val sharedEntryInSceneNavEntryDecorator = navEntryDecorator<NavKey> { entry ->
        with(localNavSharedTransitionScope.current) {
            if (isCompact) {
                entry.Content()
            } else {
                Box(
                    Modifier.sharedElement(
                        rememberSharedContentState(entry.contentKey),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    ),
                ) {
                    entry.Content()
                }
            }
        }
    }

    val twoPaneStrategy = remember { TwoPaneSceneStrategy<Any>() }

    // Define top-level destinations for the toolbar/navigation rail
    val topLevelDestinations = listOf(Home, Browse, Preferences)

    // Material 3 Expressive vibrant colors for the toolbar/rail
    val toolbarColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()

    // Shared NavDisplay composable to avoid duplication
    @Composable
    fun NavigationContent(modifier: Modifier = Modifier) {
        SharedTransitionLayout {
            CompositionLocalProvider(localNavSharedTransitionScope provides this) {
                NavDisplay(
                    modifier = modifier,
                    backStack = backStack,
                    onBack = { keysToRemove -> repeat(keysToRemove) { backStack.removeLastOrNull() } },
                    entryDecorators = listOf(
                        sharedEntryInSceneNavEntryDecorator,
                        rememberSceneSetupNavEntryDecorator(),
                        rememberSavedStateNavEntryDecorator()
                    ),
                    sceneStrategy = twoPaneStrategy,
                    entryProvider = entryProvider {
                        entry<Home>(
                            metadata = TwoPaneScene.twoPane()
                        ) {
                            ContentRed("Welcome to Nav3") {
                                Button(onClick = { backStack.addTopicRoute(1) }) {
                                    Text("View the first topic")
                                }
                            }
                        }
                        entry<Topic>(
                            metadata = TwoPaneScene.twoPane()
                        ) { topic ->

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
                        entry<Saved> {
                            ContentGreen("Saved Topics")
                        }
                        entry<Preferences> {
                            ContentGreen("Preferences (single pane only)")
                        }
                    }
                )
            }
        }
    }

    if (isCompact) {
        // Compact screen: Use HorizontalFloatingToolbar in content slot
        Scaffold { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavigationContent(Modifier.fillMaxSize())
                HorizontalFloatingToolbar(
                    expanded = isToolbarExpanded,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
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
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Localized description",
                                    )
                                }
                            },
                        ){
                        topLevelDestinations.forEach { destination ->
                            val selected = currentNavKey == destination
                            clickableItem(
                                onClick = {
                                    // Clear back stack and add the selected destination
                                    backStack.clear()
                                    backStack.add(destination)
                                    isToolbarExpanded = true // Expand toolbar on selection
                                },
                                icon = {
                                    Icon(
                                        imageVector = when (destination) {
                                            is Home -> Icons.Filled.Home
                                            is Browse -> Icons.AutoMirrored.Filled.List
                                            is Preferences -> Icons.Filled.Person
                                            else -> Icons.Filled.Info
                                        },
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
        }
    } else {
        // Medium/large screen: Use NavigationRail
        Row(Modifier.fillMaxSize()) {
            NavigationRail(
                modifier = Modifier
                    .width(if (isNavRailExpanded) 240.dp else 80.dp)
                    .fillMaxHeight(),
                header = {
                    FilledIconButton(
                        onClick = { isNavRailExpanded = !isNavRailExpanded },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isNavRailExpanded) Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
                            contentDescription = "Toggle Navigation Rail"
                        )
                    }
                }
            ) {
                topLevelDestinations.forEach { destination ->
                    val selected = currentNavKey == destination
                    NavigationRailItem(
                        selected = selected,
                        onClick = {
                            // Clear back stack and add the selected destination
                            backStack.clear()
                            backStack.add(destination)
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    is Home -> Icons.Filled.Home
                                    is Browse -> Icons.AutoMirrored.Filled.List
                                    is Preferences -> Icons.Filled.Person
                                    else -> Icons.Filled.Info
                                },
                                contentDescription = destination.toString()
                            )
                        },
                        label = if (isNavRailExpanded) {
                            { Text(destination.toString()) }
                        } else {
                            {}
                        }
                    )
                }
            }
            NavigationContent(Modifier.fillMaxSize())
        }
    }
}

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