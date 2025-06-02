package com.ryen.sunnah_alhadi.domain.useCase.userPreferenceUseCase

import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): UserPreferences {
        return userPreferencesRepository.getUserPreferences()
    }
}