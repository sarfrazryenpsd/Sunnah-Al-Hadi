package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesFlowUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamFlowUseCase<UserPreferences>() {
    override fun execute(): Flow<UserPreferences> {
        return userPreferencesRepository.getUserPreferencesFlow()
    }
}