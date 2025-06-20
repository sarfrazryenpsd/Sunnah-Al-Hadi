package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetSunnahDetailUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : UseCase<String, Sunnah?>() {

    override suspend fun execute(parameters: String): Sunnah? =
        when(val result = sunnahRepository.getSunnahById(parameters)){
            is RepositoryResult.Success -> result.data
            is RepositoryResult.Error -> {
                // Handle error
                null
            }
        }
}