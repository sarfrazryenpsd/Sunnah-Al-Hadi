package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase

class ShouldShowSotdCardUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Boolean>() {

    override suspend fun execute(): Boolean {
        val currentSotdId = userPreferencesRepository.getCurrentSotd()
        val isSeen = userPreferencesRepository.isSotdSeen()
        val shouldGenerate = userPreferencesRepository.shouldGenerateNewSotd()

        // Show SOTD card if:
        // 1. There's a current SOTD that hasn't been seen, OR
        // 2. A new SOTD should be generated (new day)
        return (currentSotdId.isNotEmpty() && !isSeen) || shouldGenerate
    }
}