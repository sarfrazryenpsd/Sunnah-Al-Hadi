package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetSunnahOfTheDayUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Sunnah?>() {
    override suspend fun execute(): Sunnah? {
        val recentlyViewed = userPreferencesRepository.getRecentlyViewedIds()
        return when (val sunnah = sunnahRepository.getRandomSunnahForSotd(recentlyViewed)) {
            is Result.Success -> sunnah.data.let {
                userPreferencesRepository.addToRecentlyViewed(it.id)
                it
            }
            is Result.Error -> null
        }
    }
}