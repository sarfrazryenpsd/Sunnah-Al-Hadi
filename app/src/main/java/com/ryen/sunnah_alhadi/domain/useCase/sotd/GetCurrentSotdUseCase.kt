package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.SotdState
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase
import javax.inject.Inject

class GetCurrentSotdUseCase @Inject constructor(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<SotdState>() {

    override suspend fun execute(): SotdState {
        val currentSotdId = userPreferencesRepository.getCurrentSotd()
        val isSeen = userPreferencesRepository.isSotdSeen()
        val generatedDate = userPreferencesRepository.getSotdGeneratedDate()

        val sunnah = if (currentSotdId.isNotEmpty()) {
            when(val result = sunnahRepository.getSunnahById(currentSotdId)){
                is Result.Success -> result.data
                is Result.Error -> {
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