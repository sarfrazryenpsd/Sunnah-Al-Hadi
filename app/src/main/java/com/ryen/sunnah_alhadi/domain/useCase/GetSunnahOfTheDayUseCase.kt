package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetSunnahOfTheDayUseCase @Inject constructor(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Result<Sunnah?>>() {
    override suspend fun execute(): Result<Sunnah?> {
        val recentlyViewed = userPreferencesRepository.getRecentlyViewedIds()
        return sunnahRepository.getRandomSunnahForSotd(recentlyViewed)
    }
}