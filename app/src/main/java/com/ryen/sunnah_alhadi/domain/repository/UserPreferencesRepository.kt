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
    fun getUserPreferencesFlow(): Flow<UserPreferences>

}