package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetSunnahOfTheDayUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Sunnah?>() {
    override suspend fun execute(): Sunnah? {
        val recentlyViewed = userPreferencesRepository.getRecentlyViewedIds()
        val allSunnahs = when(val allSunnahsResult = sunnahRepository.getAllSunnahs()){
            is RepositoryResult.Error -> return null
            is RepositoryResult.Success -> allSunnahsResult.data
        }
        val availableSunnahs = allSunnahs.filterNot { it.id in recentlyViewed }


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