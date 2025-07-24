package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import javax.inject.Inject

class UpdateUserPreferencesUseCase @Inject constructor(
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
        parameters.sotdNotificationTime?.let {
            userPreferencesRepository.updateSotdNotificationTime(it)
        }
        parameters.isSotdNotificationEnabled?.let {
            userPreferencesRepository.updateSotdNotificationEnabled(it)
        }
        parameters.hasCompletedOnboarding?.let {
            userPreferencesRepository.markOnboardingCompleted()
        }
    }
}

data class UserPreferencesUpdate(
    val username: String? = null,
    val themeMode: ThemeMode? = null,
    val isDynamicThemeEnabled: Boolean? = null,
    val isDailyReminderEnabled: Boolean? = null,
    val sotdNotificationTime: NotificationTime? = null,
    val isSotdNotificationEnabled: Boolean? = null,
    val hasCompletedOnboarding: Boolean? = null
)
