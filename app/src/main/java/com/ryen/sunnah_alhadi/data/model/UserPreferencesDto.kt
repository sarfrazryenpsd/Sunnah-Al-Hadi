package com.ryen.sunnah_alhadi.data.model

import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.model.UserPreferences


fun ProtoUserPreferences.toDomain(): UserPreferences {
    return UserPreferences(
        username = this.username,
        themeMode = this.themeMode,
        isDynamicThemeEnabled = this.isDynamicThemeEnabled,
        hasCompletedOnboarding = this.hasCompletedOnboarding,
        recentlyViewedSunnahIds = this.recentlyViewedSunnahIdsList.toList(),
        currentSotdId = this.currentSotdId,
        sotdGeneratedDate = this.sotdGeneratedDate,
        isSotdSeen = this.isSotdSeen,
        sotdNotificationTime = NotificationTime.entries.toTypedArray().getOrElse(this.sotdNotificationTime) { NotificationTime.MORNING },
        isSotdNotificationEnabled = this.isSotdNotificationEnabled
    )
}

fun UserPreferences.toProto(): ProtoUserPreferences {
    return ProtoUserPreferences.newBuilder()
        .setUsername(this.username)
        .setThemeMode(this.themeMode)
        .setIsDynamicThemeEnabled(this.isDynamicThemeEnabled)
        .setHasCompletedOnboarding(this.hasCompletedOnboarding)
        .addAllRecentlyViewedSunnahIds(this.recentlyViewedSunnahIds)
        .setCurrentSotdId(this.currentSotdId)
        .setSotdGeneratedDate(this.sotdGeneratedDate)
        .setIsSotdSeen(this.isSotdSeen)
        .setSotdNotificationTime(this.sotdNotificationTime.ordinal)
        .setIsSotdNotificationEnabled(this.isSotdNotificationEnabled)
        .build()
}