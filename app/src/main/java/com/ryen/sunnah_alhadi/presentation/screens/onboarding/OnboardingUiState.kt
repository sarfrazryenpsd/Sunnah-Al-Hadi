package com.ryen.sunnah_alhadi.presentation.screens.onboarding

import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.USERNAME,
    val username: String = "",
    val usernameError: String? = null,
    val isUsernameValid: Boolean = false,
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicThemeEnabled: Boolean = true,
    val isNotificationEnabled: Boolean = true,
    val selectedNotificationTime: NotificationTime = NotificationTime.MORNING,
    val isLoading: Boolean = false
)

enum class OnboardingStep {
    USERNAME, THEME, NOTIFICATION, WELCOME
}
