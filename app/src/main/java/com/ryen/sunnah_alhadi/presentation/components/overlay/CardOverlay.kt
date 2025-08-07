@file:OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.ryen.sunnah_alhadi.presentation.components.overlay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.presentation.NotificationPermissionHandler
import com.ryen.sunnah_alhadi.presentation.components.cards.OnboardingCard
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingEvent
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingStep
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingUiState
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingViewModel
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun CardOverlay(
    showOverlay: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    overlayContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val motion = MaterialTheme.motionScheme
    Box(modifier = modifier.fillMaxSize()) {
        // Main content underneath
        content()

        // Overlay with backdrop blur
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(animationSpec = motion.fastEffectsSpec()),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = EaseInOut
                )
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Prevent click-through */ }
            ) {
                overlayContent()
            }
        }
    }
}

@Composable
fun OnboardingOverlayContent(
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val uiState by onboardingViewModel.uiState.collectAsState()


    // Handle permission requests
    NotificationPermissionHandler(
        showPermissionDialog = uiState.showPermissionDialog,
        hasNotificationPermission = uiState.hasNotificationPermission,
        onPermissionResult = { granted ->
            onboardingViewModel.handlePermissionResult(granted)
        },
        onDismissDialog = {
            onboardingViewModel.onEvent(OnboardingEvent.DismissPermissionDialog)
        }
    )

    // Centered onboarding card (80% screen size)
    OnboardingCardContainer(
        uiState = uiState,
        onEvent = onboardingViewModel::onEvent,
        onDismiss = onDismiss,
        onComplete = onComplete,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .wrapContentHeight()
    )
}

@Composable
private fun OnboardingCardContainer(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine navigation capabilities
    val canGoToPrevious = uiState.currentStep != OnboardingStep.USERNAME
    val canProceedToNext = when (uiState.currentStep) {
        OnboardingStep.USERNAME -> uiState.isUsernameValid
        OnboardingStep.THEME,
        OnboardingStep.NOTIFICATION -> true

        OnboardingStep.WELCOME -> false
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // ✅ Header with proper dismiss handling
            OnboardingHeader(
                currentStep = uiState.currentStep,
                onDismiss = {
                    onEvent(OnboardingEvent.DismissOnboarding)
                    onDismiss()
                } // ✅ Use parent's onDismiss directly
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic card content with smooth transitions
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(300, easing = EaseInOut)
                    ) togetherWith fadeOut(
                        animationSpec = tween(200, easing = EaseInOut)
                    )
                },
                label = "OnboardingStepTransition"
            ) { step ->
                OnboardingCard(
                    step = step,
                    uiState = uiState,
                    onEvent = onEvent,
                    canProceedToNext = canProceedToNext,
                    canGoToPrevious = canGoToPrevious,
                    onComplete = onComplete // ✅ Pass completion handler
                )
            }
        }
    }
}

@Composable
private fun OnboardingHeader(
    currentStep: OnboardingStep,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step indicator
        OnboardingStepIndicator(currentStep = currentStep)

        // Dismiss button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close onboarding",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OnboardingStepIndicator(
    currentStep: OnboardingStep,
    modifier: Modifier = Modifier
) {
    val steps = OnboardingStep.entries
    val currentIndex = steps.indexOf(currentStep)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, _ ->
            StepDot(
                isActive = index <= currentIndex,
                isCurrent = index == currentIndex
            )
        }
    }
}

@Composable
private fun StepDot(
    isActive: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    val targetSize by animateDpAsState(
        targetValue = if (isCurrent) 12.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "StepDotSize"
    )

    val targetColor by animateColorAsState(
        targetValue = when {
            isCurrent -> MaterialTheme.colorScheme.primary
            isActive -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300, easing = EaseInOut),
        label = "StepDotColor"
    )

    Box(
        modifier = modifier
            .size(targetSize)
            .background(
                color = targetColor,
                shape = CircleShape
            )
    )
}


@Preview
@Composable
private fun OBPrevStepDots() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        StepDot(
            isActive = true,
            isCurrent = true
        )

    }
}

@Preview
@Composable
private fun OBPrevStepIndicator() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingStepIndicator(
            currentStep = OnboardingStep.USERNAME
        )

    }
}

@Preview
@Composable
private fun OBPrevHeader() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingHeader(
            currentStep = OnboardingStep.USERNAME,
            onDismiss = {}
        )

    }
}

@Preview
@Composable
private fun OBPrevCardContainer() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingCardContainer(
            uiState = OnboardingUiState(),
            onEvent = {},
            onDismiss = {},
            onComplete = {}
        )

    }
}


