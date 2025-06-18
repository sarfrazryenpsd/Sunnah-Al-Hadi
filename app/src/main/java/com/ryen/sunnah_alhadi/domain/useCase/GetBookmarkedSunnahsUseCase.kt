package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarkedSunnahsFlowUseCase(
    private val bookmarkRepository: BookmarkRepository
) : NoParamFlowUseCase<RepositoryResult<List<Sunnah>>>() {
    override fun execute(): Flow<RepositoryResult<List<Sunnah>>> {
        return bookmarkRepository.getBookmarkedSunnahsFlow()
    }
}