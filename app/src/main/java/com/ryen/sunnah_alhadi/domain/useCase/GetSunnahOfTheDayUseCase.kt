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
        return when (val sunnah = sunnahRepository.getRandomSunnahForSotd(recentlyViewed)) {
            is RepositoryResult.Success -> sunnah.data
            is RepositoryResult.Error -> null
        }
    }
}