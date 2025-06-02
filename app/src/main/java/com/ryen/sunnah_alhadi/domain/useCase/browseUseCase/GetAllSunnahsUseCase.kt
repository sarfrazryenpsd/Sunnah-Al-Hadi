package com.ryen.sunnah_alhadi.domain.useCase.browseUseCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetAllSunnahsUseCase(
    private val sunnahRepository: SunnahRepository
) {
    suspend operator fun invoke(): List<Sunnah> {
        return sunnahRepository.getAllSunnahs().shuffled()
    }
}