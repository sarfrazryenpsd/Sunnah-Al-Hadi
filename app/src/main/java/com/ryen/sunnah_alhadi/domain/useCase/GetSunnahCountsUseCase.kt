package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import javax.inject.Inject

class GetSunnahCountsUseCase @Inject constructor(
    private val repository: SunnahRepository
) {
    suspend operator fun invoke(categoryIds: List<Int>): Map<Int, Int> {
        return repository.getSunnahCounts(categoryIds)
    }
}