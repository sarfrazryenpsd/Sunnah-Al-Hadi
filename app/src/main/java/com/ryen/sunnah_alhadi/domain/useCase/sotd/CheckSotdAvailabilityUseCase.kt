package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase

class CheckSotdAvailabilityUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Boolean>() {

    override suspend fun execute(): Boolean {
        val currentSotdId = userPreferencesRepository.getCurrentSotd()
        val isSeen = userPreferencesRepository.isSotdSeen()

        return currentSotdId.isNotEmpty() && !isSeen
    }
}