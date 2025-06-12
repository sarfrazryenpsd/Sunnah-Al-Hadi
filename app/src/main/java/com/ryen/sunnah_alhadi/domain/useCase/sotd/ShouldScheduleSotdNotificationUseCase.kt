package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase

class ShouldScheduleSotdNotificationUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Boolean>() {

    override suspend fun execute(): Boolean {
        val isDailyReminderEnabled = userPreferencesRepository.getUserPreferences().isDailyReminderEnabled
        val isAlreadyScheduled = userPreferencesRepository.isSotdNotificationScheduled()

        return isDailyReminderEnabled && !isAlreadyScheduled
    }
}