package com.ryen.sunnah_alhadi.presentation.screens.onboarding

import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

sealed class OnboardingEvent {
    data class UpdateUsername(val username: String) : OnboardingEvent()
    data class SelectTheme(val theme: ThemeMode) : OnboardingEvent()
    data class ToggleDynamicTheme(val enabled: Boolean) : OnboardingEvent()
    data class ToggleNotification(val enabled: Boolean) : OnboardingEvent()
    data class SelectNotificationTime(val time: NotificationTime) : OnboardingEvent()
    object NextStep : OnboardingEvent()
    object PreviousStep : OnboardingEvent()
    object DismissOnboarding : OnboardingEvent()
    object CompleteOnboarding : OnboardingEvent()
}