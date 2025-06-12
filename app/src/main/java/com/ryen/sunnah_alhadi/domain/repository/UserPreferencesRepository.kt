package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    suspend fun getUserPreferences(): UserPreferences
    suspend fun updateUsername(username: String)
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateDynamicTheme(enabled: Boolean)
    suspend fun updateDailyReminder(enabled: Boolean)
    suspend fun markOnboardingCompleted()
    suspend fun markDisclaimerSeen()
    suspend fun getRecentlyViewedIds(): List<String>
    suspend fun addToRecentlyViewed(sunnahId: String)

    suspend fun getCurrentSotd(): String
    suspend fun updateCurrentSotd(sotdId: String, generatedDate: Long)
    suspend fun markSotdAsSeen()
    suspend fun markSotdAsUnseen() // For testing or manual reset
    suspend fun isSotdSeen(): Boolean
    suspend fun getSotdGeneratedDate(): Long
    suspend fun shouldGenerateNewSotd(): Boolean // Helper method
    suspend fun updateSotdNotificationScheduled(scheduled: Boolean)
    suspend fun isSotdNotificationScheduled(): Boolean

    // Flow versions for real-time updates
    fun getCurrentSotdFlow(): Flow<String>
    fun isSotdSeenFlow(): Flow<Boolean>
    fun getUserPreferencesFlow(): Flow<UserPreferences>

}