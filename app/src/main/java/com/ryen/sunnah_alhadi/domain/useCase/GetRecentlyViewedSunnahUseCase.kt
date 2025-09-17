package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetRecentlyViewedSunnahsUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sunnahRepository: SunnahRepository
) : NoParamFlowUseCase<List<Sunnah>>() {

    override fun execute(): Flow<List<Sunnah>> {
        return userPreferencesRepository.getUserPreferencesFlow()
            .mapLatest { prefs ->
                val ids = prefs.recentlyViewedSunnahIds
                sunnahRepository.getSunnahsByIds(ids)
            }
            .catch { emit(emptyList()) }
    }
}
