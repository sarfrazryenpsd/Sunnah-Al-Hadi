package com.ryen.sunnah_alhadi.domain.useCase.homeUseCases

import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class MarkDisclaimerSeenUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        userPreferencesRepository.markDisclaimerSeen()
    }
}