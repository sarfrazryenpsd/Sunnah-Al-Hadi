package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase
import com.ryen.sunnah_alhadi.util.Result

class GenerateNewSotdUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<String?>() {

    override suspend fun execute(): String? {
        if (!userPreferencesRepository.shouldGenerateNewSotd()) {
            return userPreferencesRepository.getCurrentSotd().takeIf { it.isNotEmpty() }
        }

        val recentlyViewed = userPreferencesRepository.getRecentlyViewedIds()
        val currentSotd = userPreferencesRepository.getCurrentSotd()

        val exclusionList = if (currentSotd.isNotEmpty()) {
            (recentlyViewed + currentSotd).distinct()
        } else {
            recentlyViewed
        }

        return when (val result = sunnahRepository.getRandomSunnahForSotd(exclusionList)) {
            is Result.Success -> {
                val newSotdId = result.data.id
                userPreferencesRepository.updateCurrentSotd(
                    sotdId = newSotdId,
                    generatedDate = System.currentTimeMillis()
                )
                newSotdId // ✅ Return the generated ID
            }
            is Result.Error -> null
        }
    }
}
