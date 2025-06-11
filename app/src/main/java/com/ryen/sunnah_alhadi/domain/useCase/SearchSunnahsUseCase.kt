package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class SearchSunnahsUseCase(
    private val sunnahRepository: SunnahRepository
) : UseCase<SearchParams, List<Sunnah>>() {

    override suspend fun execute(parameters: SearchParams): List<Sunnah> {
        if (parameters.query.length < 2) return emptyList()

        var results = sunnahRepository.searchSunnahs(parameters.query.trim())

        // Apply filters
        if (parameters.categoryId != null) {
            results = results.filter { it.categoryId == parameters.categoryId }
        }

        // Apply sorting
        results = when (parameters.sortBy) {
            SortBy.TITLE -> results.sortedBy { it.title }
            SortBy.CATEGORY -> results.sortedBy { it.categoryId }
            SortBy.RELEVANCE -> results // Already sorted by relevance from search
        }

        return results
    }
}

data class SearchParams(
    val query: String,
    val categoryId: Int? = null,
    val sortBy: SortBy = SortBy.RELEVANCE
)

enum class SortBy { TITLE, CATEGORY, RELEVANCE }