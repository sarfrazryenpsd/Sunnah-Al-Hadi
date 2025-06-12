package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.UseCase

class MarkSotdNotificationScheduledUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) : UseCase<Boolean, Unit>() {

    override suspend fun execute(parameters: Boolean) {
        userPreferencesRepository.updateSotdNotificationScheduled(parameters)
    }
}