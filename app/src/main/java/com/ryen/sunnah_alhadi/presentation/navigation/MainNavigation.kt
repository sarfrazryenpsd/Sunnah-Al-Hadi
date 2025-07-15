package com.ryen.sunnah_alhadi.presentation.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Home)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

    val motionScheme = MaterialTheme.motionScheme

    NavDisplay(
        backStack = backStack,
        onBack = { keysToRemove -> repeat(keysToRemove) { backStack.removeLastOrNull() } },
        sceneStrategy = listDetailStrategy,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            ContentTransform(
                fadeIn(motionScheme.defaultEffectsSpec()),
                fadeOut(motionScheme.defaultEffectsSpec()),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                fadeIn(motionScheme.defaultEffectsSpec()),
                scaleOut(
                    targetScale = 0.7f,
                ),
            )
        },
        entryProvider = entryProvider {
            entry<Home>(
                metadata = ListDetailSceneStrategy.listPane()
            ){
                //Home Composable
            }
            entry<AllTopic>(
                metadata = ListDetailSceneStrategy.detailPane()
            ){
                //AllTopic Composable
            }
            entry<Browse>(
                metadata = ListDetailSceneStrategy.listPane()
            ){
                //Browse Composable
            }
            entry<Saved>(
                metadata = ListDetailSceneStrategy.listPane()
            ){
                //Saved Composable
            }
            entry<Preferences>(
                metadata = ListDetailSceneStrategy.listPane()
            ){
                //Preferences Composable
            }
            entry<Topic>(
                metadata = ListDetailSceneStrategy.extraPane()
            ){ category ->
                //Topic Composable
            }
        }
    )
}