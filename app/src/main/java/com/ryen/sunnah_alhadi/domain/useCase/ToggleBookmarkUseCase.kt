package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository

class ToggleBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(parameters: String) {
        bookmarkRepository.toggleBookmark(parameters)
    }
}