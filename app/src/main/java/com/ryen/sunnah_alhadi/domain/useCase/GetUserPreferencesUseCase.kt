package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
): NoParamUseCase<UserPreferences>() {
    override suspend fun execute(): UserPreferences {
        return userPreferencesRepository.getUserPreferences()
    }
}