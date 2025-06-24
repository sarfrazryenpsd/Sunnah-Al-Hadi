package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase

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

        val allSunnahs = when (val result = sunnahRepository.getAllSunnahs()) {
            is Result.Success -> result.data
            is Result.Error -> {
                // Optional: log the error or handle fallback
                return null
            }
        }

        val availableSunnahs = allSunnahs.filter { it.id !in exclusionList }

        val selectedSunnah = if (availableSunnahs.isNotEmpty()) {
            availableSunnahs.random()
        } else {
            allSunnahs.randomOrNull()
        }

        selectedSunnah?.let { sunnah ->
            userPreferencesRepository.updateCurrentSotd(
                sotdId = sunnah.id,
                generatedDate = System.currentTimeMillis()
            )
            return sunnah.id
        }

        return null
    }
}
