package com.ryen.sunnah_alhadi.domain.useCase.browseUseCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class SearchSunnahsUseCase(
    private val sunnahRepository: SunnahRepository
) {
    suspend operator fun invoke(query: String): List<Sunnah> {
        return sunnahRepository.searchSunnahs(query)
    }
}