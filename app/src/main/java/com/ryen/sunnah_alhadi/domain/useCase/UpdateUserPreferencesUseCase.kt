package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

class UpdateUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) : UseCase<UserPreferencesUpdate, Unit>() {

    override suspend fun execute(parameters: UserPreferencesUpdate) {
        parameters.username?.let {
            userPreferencesRepository.updateUsername(it)
        }
        parameters.themeMode?.let {
            userPreferencesRepository.updateThemeMode(it)
        }
        parameters.isDynamicThemeEnabled?.let {
            userPreferencesRepository.updateDynamicTheme(it)
        }
        parameters.isDailyReminderEnabled?.let {
            userPreferencesRepository.updateDailyReminder(it)
        }
    }
}

data class UserPreferencesUpdate(
    val username: String? = null,
    val themeMode: ThemeMode? = null,
    val isDynamicThemeEnabled: Boolean? = null,
    val isDailyReminderEnabled: Boolean? = null
)
