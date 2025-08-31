package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase
import javax.inject.Inject

class MarkSotdAsSeenUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Unit>() {

    override suspend fun execute() {
        // Get current SOTD ID before marking as seen
        val currentSotdId = userPreferencesRepository.getCurrentSotd()

        // Mark SOTD as seen
        userPreferencesRepository.markSotdAsSeen()

        // Add to recently viewed if SOTD exists
        if (currentSotdId.isNotEmpty()) {
            userPreferencesRepository.addToRecentlyViewed(currentSotdId)
        }
    }
}

