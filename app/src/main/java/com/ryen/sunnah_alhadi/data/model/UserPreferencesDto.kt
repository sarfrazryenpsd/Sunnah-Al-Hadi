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
        recentlyViewedSunnahIds = this.recentlyViewedSunnahIdsList.toList(),
        currentSotdId = this.currentSotdId,
        sotdGeneratedDate = this.sotdGeneratedDate,
        isSotdSeen = this.isSotdSeen,
        isSotdNotificationScheduled = this.isSotdNotificationScheduled
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
        .setCurrentSotdId(this.currentSotdId)
        .setSotdGeneratedDate(this.sotdGeneratedDate)
        .setIsSotdSeen(this.isSotdSeen)
        .setIsSotdNotificationScheduled(this.isSotdNotificationScheduled)
        .build()
}