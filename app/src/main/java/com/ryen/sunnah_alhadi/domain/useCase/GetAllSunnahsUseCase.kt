package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetAllSunnahsUseCase(
    private val sunnahRepository: SunnahRepository
) : UseCase<BrowseParams, List<Sunnah>>() {

    override suspend fun execute(parameters: BrowseParams): List<Sunnah> {
        var sunnahs = if (parameters.categoryId != null) {
            when (val result = sunnahRepository.getSunnahsByCategory(parameters.categoryId)) {
                is RepositoryResult.Success -> result.data
                is RepositoryResult.Error -> {
                    // Handle error
                    emptyList()
                }
            }
        } else {
            when(val allSunnahResult = sunnahRepository.getAllSunnahs()){
                is RepositoryResult.Success -> allSunnahResult.data
                is RepositoryResult.Error -> {
                    // Handle error
                    emptyList()
                }
            }
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
