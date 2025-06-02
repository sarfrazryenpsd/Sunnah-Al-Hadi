package com.ryen.sunnah_alhadi.domain.useCase.sunnahDetailUseCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetSunnahByIdUseCase(
    private val sunnahRepository: SunnahRepository
) {
    suspend operator fun invoke(sunnahId: String): Sunnah? {
        return sunnahRepository.getSunnahById(sunnahId)
    }
}