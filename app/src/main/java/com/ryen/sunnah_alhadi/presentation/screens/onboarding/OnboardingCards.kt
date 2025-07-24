@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.onboarding

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.presentation.common.PreviewWrapper
import com.ryen.sunnah_alhadi.presentation.util.getUsernameCharacterCount
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

@Composable
fun OnboardingCard(
    step: OnboardingStep,
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    canProceedToNext: Boolean,
    canGoToPrevious: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                OnboardingStep.USERNAME -> UsernameCard(
                    username = uiState.username,
                    usernameError = uiState.usernameError,
                    onUsernameChange = { onEvent(OnboardingEvent.UpdateUsername(it)) }
                )
                OnboardingStep.THEME -> ThemeCard(
                    selectedTheme = uiState.selectedTheme,
                    isDynamicThemeEnabled = uiState.isDynamicThemeEnabled,
                    onThemeSelect = { onEvent(OnboardingEvent.SelectTheme(it)) },
                    onDynamicThemeToggle = { onEvent(OnboardingEvent.ToggleDynamicTheme(it)) }
                )
                OnboardingStep.NOTIFICATION -> NotificationCard(
                    isNotificationEnabled = uiState.isNotificationEnabled,
                    selectedTime = uiState.selectedNotificationTime,
                    onNotificationToggle = { onEvent(OnboardingEvent.ToggleNotification(it)) },
                    onTimeSelect = { onEvent(OnboardingEvent.SelectNotificationTime(it)) }
                )
                OnboardingStep.WELCOME -> WelcomeCard(
                    username = uiState.username
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation buttons
            OnboardingNavigationButtons(
                currentStep = step,
                canProceedToNext = canProceedToNext,
                canGoToPrevious = canGoToPrevious,
                onNext = { onEvent(OnboardingEvent.NextStep) },
                onPrevious = { onEvent(OnboardingEvent.PreviousStep) },
                onComplete = { onEvent(OnboardingEvent.CompleteOnboarding) }
            )
        }
    }
}

@Composable
private fun UsernameCard(
    username: String,
    usernameError: String?,
    onUsernameChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Welcome to Sunnah Al-Hadi",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Let's personalize your experience.\nWhat would you like to be called?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Your Name") },
            placeholder = { Text("Enter your name") },
            supportingText = {
                usernameError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                } ?: Text(
                    text = "Enter your preferred name for the app",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                Text(
                    text = getUsernameCharacterCount(username),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = usernameError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ThemeCard(
    selectedTheme: ThemeMode,
    isDynamicThemeEnabled: Boolean,
    onThemeSelect: (ThemeMode) -> Unit,
    onDynamicThemeToggle: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Choose Your Theme",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Select your preferred appearance for the app",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.entries.forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedTheme == theme,
                            onClick = { onThemeSelect(theme) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTheme == theme,
                        onClick = { onThemeSelect(theme) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (theme) {
                            ThemeMode.LIGHT -> "Light Mode"
                            ThemeMode.DARK -> "Dark Mode"
                            else -> "System Default"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Dynamic Theme toggle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dynamic Colors",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Use colors from your wallpaper",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isDynamicThemeEnabled,
                    onCheckedChange = onDynamicThemeToggle
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    isNotificationEnabled: Boolean,
    selectedTime: NotificationTime,
    onNotificationToggle: (Boolean) -> Unit,
    onTimeSelect: (NotificationTime) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Daily Reminders",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Get gentle reminders to read a Sunnah daily",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enable Daily Reminders",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isNotificationEnabled,
                onCheckedChange = onNotificationToggle
            )
        }

        // Time selection (only shown when notifications are enabled)
        AnimatedVisibility(visible = isNotificationEnabled) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Preferred Time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                NotificationTime.entries.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedTime == time,
                                onClick = { onTimeSelect(time) }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTime == time,
                            onClick = { onTimeSelect(time) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (time) {
                                NotificationTime.MORNING -> "Morning (8:00 AM)"
                                NotificationTime.EVENING -> "Evening (6:00 PM)"
                                NotificationTime.NIGHT -> "Night (9:00 PM)"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard(
    username: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Welcome, $username!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Your app is ready! Explore 500+ authentic Sunnahs with beautiful Arabic texts, English translations, and reliable references.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✨ Features you'll love:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )

                val features = listOf(
                    "Daily Sunnah of the Day",
                    "Search and bookmark your favorites",
                    "Beautiful Arabic calligraphy",
                    "Authentic references included",
                    "Works completely offline"
                )

                features.forEach { feature ->
                    Text(
                        text = "• $feature",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingNavigationButtons(
    currentStep: OnboardingStep,
    canProceedToNext: Boolean,
    canGoToPrevious: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (canGoToPrevious) Arrangement.SpaceBetween else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canGoToPrevious) {
            TextButton(
                onClick = onPrevious
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Back")
            }
        }

        if (currentStep == OnboardingStep.WELCOME) {
            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth(if (canGoToPrevious) 0.6f else 1f)
            ) {
                Text("Get Started")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Button(
                onClick = onNext,
                enabled = canProceedToNext,
                modifier = Modifier.fillMaxWidth(if (canGoToPrevious) 0.6f else 1f)
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingNavigationButtonPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingNavigationButtons(
            currentStep = OnboardingStep.THEME,
            canProceedToNext = true,
            canGoToPrevious = true,
            onNext = {},
            onPrevious = {},
            onComplete = {}
        )
    }
}

@Preview
@Composable
fun OnboardingCardsPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingCard(
            step = OnboardingStep.USERNAME,
            uiState = OnboardingUiState(),
            onEvent = {},
            canProceedToNext = true,
            canGoToPrevious = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationCardPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        NotificationCard(
            isNotificationEnabled = true,
            selectedTime = NotificationTime.MORNING,
            onNotificationToggle = { },
            onTimeSelect = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeCardPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        ThemeCard(
            selectedTheme = ThemeMode.LIGHT,
            isDynamicThemeEnabled = true,
            onThemeSelect = { },
            onDynamicThemeToggle = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun WelcomeCardPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        WelcomeCard(
            username = "Sarfraz"
        )
    }
}