package com.ryen.sunnah_alhadi.domain.model

data class UserPreferences(
    val username: String = "",
    val themeMode: Int = 0, // from enum 0=SYSTEM, 1=LIGHT, 2=DARK
    val isDynamicThemeEnabled: Boolean = true,
    val isDailyReminderEnabled: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val hasSeenDisclaimer: Boolean = false,
    val recentlyViewedSunnahIds: List<String> = emptyList()
)
