package com.ryen.sunnah_alhadi.domain.useCase.userPreferenceUseCase

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

class UpdateUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun updateUsername(username: String) {
        userPreferencesRepository.updateUsername(username)
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        userPreferencesRepository.updateThemeMode(themeMode)
    }

    suspend fun updateDynamicTheme(enabled: Boolean) {
        userPreferencesRepository.updateDynamicTheme(enabled)
    }

    suspend fun updateDailyReminder(enabled: Boolean) {
        userPreferencesRepository.updateDailyReminder(enabled)
    }
}