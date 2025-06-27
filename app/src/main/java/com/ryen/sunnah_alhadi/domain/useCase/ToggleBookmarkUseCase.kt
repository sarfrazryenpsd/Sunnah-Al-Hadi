package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(parameters: String) {
        bookmarkRepository.toggleBookmark(parameters)
    }
}