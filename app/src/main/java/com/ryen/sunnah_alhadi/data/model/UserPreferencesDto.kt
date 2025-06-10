package com.ryen.sunnah_alhadi.data.model

import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.domain.model.UserPreferences

fun ProtoUserPreferences.toDomain(): UserPreferences {
    return UserPreferences(
        username = this.username,
        themeMode = this.themeMode,
        isDynamicThemeEnabled = this.isDynamicThemeEnabled,
        isDailyReminderEnabled = this.isDailyReminderEnabled,
        hasCompletedOnboarding = this.hasCompletedOnboarding,
        hasSeenDisclaimer = this.hasSeenDisclaimer,
        recentlyViewedSunnahIds = this.recentlyViewedSunnahIdsList.toList()
    )
}

fun UserPreferences.toProto(): ProtoUserPreferences {
    return ProtoUserPreferences.newBuilder()
        .setUsername(this.username)
        .setThemeMode(this.themeMode)
        .setIsDynamicThemeEnabled(this.isDynamicThemeEnabled)
        .setIsDailyReminderEnabled(this.isDailyReminderEnabled)
        .setHasCompletedOnboarding(this.hasCompletedOnboarding)
        .setHasSeenDisclaimer(this.hasSeenDisclaimer)
        .addAllRecentlyViewedSunnahIds(this.recentlyViewedSunnahIds)
        .build()
}