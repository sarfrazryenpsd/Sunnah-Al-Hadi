package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetRecentlyViewedSunnahsUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sunnahRepository: SunnahRepository
) : NoParamUseCase<List<Sunnah>>() {

    override suspend fun execute(): List<Sunnah> {
        return try {
            val recentIds = userPreferencesRepository.getRecentlyViewedIds()
            sunnahRepository.getSunnahsByIds(recentIds)
        } catch (e: Exception) {
            // Return empty list on error to prevent crashes
            emptyList()
        }
    }
}