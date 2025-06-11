package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetSunnahOfTheDayUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Sunnah?>() {
    override suspend fun execute(): Sunnah? {
        val recentlyViewed = userPreferencesRepository.getRecentlyViewedIds()
        val allSunnahs = sunnahRepository.getAllSunnahs()
        val availableSunnahs = allSunnahs.filter { it.id !in recentlyViewed }

        val selectedSunnah = if (availableSunnahs.isNotEmpty()) {
            availableSunnahs.random()
        } else {
            // Reset queue if all viewed
            allSunnahs.randomOrNull()
        }

        selectedSunnah?.let {
            userPreferencesRepository.addToRecentlyViewed(it.id)
        }

        return selectedSunnah
    }
}