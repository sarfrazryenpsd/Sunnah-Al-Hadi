package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetSunnahDetailUseCase(
    private val sunnahRepository: SunnahRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : UseCase<String, Sunnah?>() {

    override suspend fun execute(parameters: String): Sunnah? =
        sunnahRepository.getSunnahById(parameters)
}