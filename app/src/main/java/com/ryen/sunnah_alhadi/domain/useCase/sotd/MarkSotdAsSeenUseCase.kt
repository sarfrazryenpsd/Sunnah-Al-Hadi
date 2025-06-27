package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase
import javax.inject.Inject

class MarkSotdAsSeenUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Unit>() {

    override suspend fun execute() {
        userPreferencesRepository.markSotdAsSeen()
    }
}

