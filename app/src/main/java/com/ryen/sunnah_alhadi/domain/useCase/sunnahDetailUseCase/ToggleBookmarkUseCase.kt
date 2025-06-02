package com.ryen.sunnah_alhadi.domain.useCase.sunnahDetailUseCase

import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository

class ToggleBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(sunnahId: String) {
        bookmarkRepository.toggleBookmark(sunnahId)
    }
}