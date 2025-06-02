package com.ryen.sunnah_alhadi.domain.useCase.topicDetailUseCases

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetSunnahsByCategoryUseCase(
    private val sunnahRepository: SunnahRepository
) {
    suspend operator fun invoke(categoryId: Int): List<Sunnah> {
        return sunnahRepository.getSunnahsByCategory(categoryId)
    }
}