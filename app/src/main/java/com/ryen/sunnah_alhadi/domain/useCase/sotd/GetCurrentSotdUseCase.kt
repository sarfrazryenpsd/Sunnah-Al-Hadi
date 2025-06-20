package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.SotdState
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase

class GetCurrentSotdUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<SotdState>() {

    override suspend fun execute(): SotdState {
        val currentSotdId = userPreferencesRepository.getCurrentSotd()
        val isSeen = userPreferencesRepository.isSotdSeen()
        val generatedDate = userPreferencesRepository.getSotdGeneratedDate()

        val sunnah = if (currentSotdId.isNotEmpty()) {
            when(val result = sunnahRepository.getSunnahById(currentSotdId)){
                is RepositoryResult.Success -> result.data
                is RepositoryResult.Error -> {
                    // Handle error
                    null
                }
            }
        } else null

        return SotdState(
            currentSotd = sunnah,
            isSeen = isSeen,
            isAvailable = sunnah != null,
            generatedDate = generatedDate
        )
    }
}