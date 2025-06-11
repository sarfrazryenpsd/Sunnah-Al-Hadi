package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class GetBookmarkedSunnahsFlowUseCase(
    private val bookmarkRepository: BookmarkRepository
) : NoParamFlowUseCase<List<Sunnah>>() {
    override fun execute(): Flow<List<Sunnah>> {
        return bookmarkRepository.getBookmarkedSunnahsFlow()
    }
}