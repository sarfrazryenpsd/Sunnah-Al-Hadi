@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components.cards

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingEvent
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingStep
import com.ryen.sunnah_alhadi.presentation.screens.onboarding.OnboardingUiState
import com.ryen.sunnah_alhadi.presentation.util.getUsernameCharacterCount
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

@Composable
fun OnboardingCard(
    step: OnboardingStep,
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    canProceedToNext: Boolean,
    onComplete: () -> Unit,
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
                onComplete = {
                    onEvent(OnboardingEvent.CompleteOnboarding)
                    onComplete()
                }
            )
        }
    }
}

@Composable
fun UsernameCard(
    username: String,
    usernameError: String?,
    onUsernameChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.sunnahlogo),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )

        Text(
            text = "Welcome to Sunnah Al-Hadi",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary
        )


        Text(
            text = "Let's personalize your experience.\nWhat is your good name?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Your Name", color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)) },
            placeholder = { Text("Enter your name", color = MaterialTheme.colorScheme.tertiary) },
            supportingText = {
                usernameError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                } ?: Text(
                    text = "Enter your preferred name for the app",
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            trailingIcon = {
                Text(
                    text = getUsernameCharacterCount(username),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            isError = usernameError != null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                focusedTextColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
fun UsernameCardPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ) {
        UsernameCard(
            username = "",
            usernameError = null,
            onUsernameChange = {}
        )
    }
}


@Composable
private fun ThemeCard(
    selectedTheme: ThemeMode,
    isDynamicThemeEnabled: Boolean,
    onThemeSelect: (ThemeMode) -> Unit,
    onDynamicThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        OnboardingCardHeader(
            title = "Choose Your Theme",
            subtitle = "Customize the app's appearance to your preference"
        )

        // Theme selection radio buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeOption(
                title = "System Default",
                subtitle = "Follow device theme settings",
                isSelected = selectedTheme == ThemeMode.SYSTEM,
                onClick = { onThemeSelect(ThemeMode.SYSTEM) },
                icon = R.drawable.interface_system
            )

            ThemeOption(
                title = "Light Mode",
                subtitle = "Always use light theme",
                isSelected = selectedTheme == ThemeMode.LIGHT,
                onClick = { onThemeSelect(ThemeMode.LIGHT) },
                icon = R.drawable.interface_light
            )

            ThemeOption(
                title = "Dark Mode",
                subtitle = "Always use dark theme",
                isSelected = selectedTheme == ThemeMode.DARK,
                onClick = { onThemeSelect(ThemeMode.DARK) },
                icon = R.drawable.interface_dark
            )
        }

        // Dynamic theme toggle (only on Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDynamicThemeToggle(!isDynamicThemeEnabled) }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dynamic Colors",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Use colors from your wallpaper",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                    )
                }

                Switch(
                    checked = isDynamicThemeEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onCheckedChange = onDynamicThemeToggle
                )
            }
        }
    }
}

@Composable
private fun OnboardingCardHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ThemeOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.tertiary,
                unselectedColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
            ),
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
            },
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                }
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun NotificationCard(
    isNotificationEnabled: Boolean,
    selectedTime: NotificationTime,
    onNotificationToggle: (Boolean) -> Unit,
    onTimeSelect: (NotificationTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        OnboardingCardHeader(
            title = "Daily Reminders",
            subtitle = "Get reminded to read a daily Sunnah"
        )

        // Notification toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNotificationToggle(!isNotificationEnabled) }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Enable Daily Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Receive gentle reminders to stay connected with the Sunnah",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                )
            }

            Switch(
                checked = isNotificationEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                    checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onCheckedChange = onNotificationToggle
            )
        }

        // Time selection (only visible when notifications enabled)
        AnimatedVisibility(
            visible = isNotificationEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Text(
                    text = "Preferred Time",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                NotificationTime.entries.forEach { time ->
                    NotificationTimeOption(
                        time = time,
                        isSelected = selectedTime == time,
                        onClick = { onTimeSelect(time) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationTimeOption(
    time: NotificationTime,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (timeText, descriptionText) = when (time) {
        NotificationTime.MORNING -> "Morning (8:00 AM)" to "Start your day with a Sunnah"
        NotificationTime.EVENING -> "Evening (6:00 PM)" to "Wind down with reflection"
        NotificationTime.NIGHT -> "Night (9:00 PM)" to "End your day peacefully"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.tertiary,
                unselectedColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                }
            )
            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun WelcomeCard(
    username: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome illustration or icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ec_benefit),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        // Welcome message
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Welcome, ${username}!",
                maxLines = 2,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = "You're all set to begin your journey with the beautiful Sunnahs of Prophet Muhammad ﷺ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        // Features preview
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WelcomeFeatureItem(
                icon = R.drawable.ec_explanation,
                title = "470+ Sunnahs & Manners",
                description = "Explore a comprehensive collection"
            )

            WelcomeFeatureItem(
                icon = R.drawable.interface_bookmarked,
                title = "Personal Bookmarks",
                description = "Save your favorite Sunnahs"
            )

            WelcomeFeatureItem(
                icon = R.drawable.interface_clock,
                title = "Daily Reminders",
                description = "Stay connected every day"
            )
        }
    }
}

@Composable
private fun WelcomeFeatureItem(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
            )
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
                onClick = onPrevious,
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.interface_left),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Back", color = MaterialTheme.colorScheme.tertiary)
            }
        }

        if (currentStep == OnboardingStep.WELCOME) {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ),
                modifier = Modifier.fillMaxWidth(if (canGoToPrevious) 1f else 1f)
            ) {
                BasicText(
                    text = "Get Started",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    maxLines = 2,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = MaterialTheme.typography.bodyMedium.fontSize * .5,
                        maxFontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.interface_right),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Button(
                onClick = onNext,
                enabled = canProceedToNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth(if (canGoToPrevious) 0.6f else 1f)
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.interface_right),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@PreviewLightDark
@Preview(showBackground = true)
@Composable
fun OnboardingNavigationButtonPrev1() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingNavigationButtons(
            currentStep = OnboardingStep.WELCOME,
            canProceedToNext = false,
            canGoToPrevious = true,
            onNext = {},
            onPrevious = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingNavigationButtonPrev2() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingNavigationButtons(
            currentStep = OnboardingStep.USERNAME,
            canProceedToNext = true,
            canGoToPrevious = false,
            onNext = {},
            onPrevious = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingNavigationButtonPrev3() {
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

@Preview(showBackground = true)
@Composable
fun OnboardingNavigationButtonPrev4() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingNavigationButtons(
            currentStep = OnboardingStep.NOTIFICATION,
            canProceedToNext = true,
            canGoToPrevious = true,
            onNext = {},
            onPrevious = {},
            onComplete = {}
        )
    }
}

@Preview(widthDp = 240)
@PreviewLightDark
@Composable
fun OnboardingCardsPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
    ) {
        OnboardingCard(
            step = OnboardingStep.WELCOME,
            uiState = OnboardingUiState(username = "Sarfraz"),
            onEvent = {},
            canProceedToNext = true,
            canGoToPrevious = true,
            onComplete = {}
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