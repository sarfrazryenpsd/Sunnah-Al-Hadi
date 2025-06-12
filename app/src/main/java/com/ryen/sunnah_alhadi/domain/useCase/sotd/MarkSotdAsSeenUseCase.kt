package com.ryen.sunnah_alhadi.domain.useCase.sotd

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.NoParamUseCase

class MarkSotdAsSeenUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Unit>() {

    override suspend fun execute() {
        userPreferencesRepository.markSotdAsSeen()
    }
}

