package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.model.SotdState
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrentSotdFlowUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamFlowUseCase<SotdState>() {

    override fun execute(): Flow<SotdState> {
        return userPreferencesRepository.getUserPreferencesFlow()
            .map { prefs ->
                val sunnah = if (prefs.currentSotdId.isNotEmpty()) {
                    sunnahRepository.getSunnahById(prefs.currentSotdId)
                } else null

                SotdState(
                    currentSotd = sunnah,
                    isSeen = prefs.isSotdSeen,
                    isAvailable = sunnah != null,
                    generatedDate = prefs.sotdGeneratedDate
                )
            }
    }
}