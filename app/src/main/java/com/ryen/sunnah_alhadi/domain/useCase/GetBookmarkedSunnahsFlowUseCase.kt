package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBookmarkedSunnahsFlowUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : NoParamFlowUseCase<Result<List<Sunnah>>>() {
    override fun execute(): Flow<Result<List<Sunnah>>> {
        return bookmarkRepository.getBookmarkedSunnahsFlow()
    }
}