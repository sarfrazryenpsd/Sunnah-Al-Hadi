package com.ryen.sunnah_alhadi.domain.useCase.homeUseCases

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetSunnahOfTheDayUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Sunnah? {
        val allSunnahs = sunnahRepository.getAllSunnahs()
        val recentIds = userPreferencesRepository.getRecentlyViewedIds().toSet()
        val availableSunnahs = allSunnahs.filterNot { it.id in recentIds }

        val sunnahOfTheDay = availableSunnahs.randomOrNull()

        sunnahOfTheDay?.let {
            userPreferencesRepository.addToRecentlyViewed(it.id)
        }

        return sunnahOfTheDay
    }
}