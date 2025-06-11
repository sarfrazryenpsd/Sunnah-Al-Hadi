package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetAllSunnahsUseCase(
    private val sunnahRepository: SunnahRepository
) : UseCase<BrowseParams, List<Sunnah>>() {

    override suspend fun execute(parameters: BrowseParams): List<Sunnah> {
        var sunnahs = if (parameters.categoryId != null) {
            sunnahRepository.getSunnahsByCategory(parameters.categoryId)
        } else {
            sunnahRepository.getAllSunnahs()
        }

        // Apply sorting
        sunnahs = when (parameters.sortBy) {
            SortBy.TITLE -> sunnahs.sortedBy { it.title }
            SortBy.CATEGORY -> sunnahs.sortedBy { it.categoryId }
            SortBy.RELEVANCE -> sunnahs
        }

        return sunnahs
    }
}

data class BrowseParams(
    val categoryId: Int? = null,
    val sortBy: SortBy = SortBy.TITLE
)
