package com.ryen.sunnah_alhadi.domain.useCase.bookmarkUseCase

import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository

class GetBookmarkedSunnahsUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(): List<Sunnah> {
        return bookmarkRepository.getBookmarkedSunnahs()
    }
}