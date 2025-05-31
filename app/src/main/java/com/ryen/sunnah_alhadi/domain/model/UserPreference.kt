package com.ryen.sunnah_alhadi.domain.model

import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

data class UserPreferences(
    val username: String = "",
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicThemeEnabled: Boolean = true,
    val isDailyReminderEnabled: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
)
