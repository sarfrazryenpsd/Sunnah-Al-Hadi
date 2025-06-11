package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Bookmark
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EnhancedBookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override suspend fun getAllBookmarks(): List<Bookmark> {
        return bookmarkDao.getAllBookmarks().map { it.toDomain() }
    }

    override suspend fun getBookmarkedSunnahs(): List<Sunnah> {
        return bookmarkDao.getBookmarkedSunnahs().map { it.toDomain(isBookmarked = true) }
    }

    override fun getBookmarkedSunnahsFlow(): Flow<List<Sunnah>> {
        return bookmarkDao.getBookmarkedSunnahsFlow()
            .map { entities ->
                entities.map { it.toDomain(isBookmarked = true) }
            }
    }

    override suspend fun isBookmarked(sunnahId: String): Boolean {
        return bookmarkDao.isBookmarked(sunnahId)
    }

    override suspend fun addBookmark(sunnahId: String) {
        val bookmark = BookmarkEntity(sunnahId = sunnahId)
        bookmarkDao.addBookmark(bookmark)
    }

    override suspend fun removeBookmark(sunnahId: String) {
        bookmarkDao.removeBookmark(sunnahId)
    }

    override suspend fun toggleBookmark(sunnahId: String) {
        if (isBookmarked(sunnahId)) {
            removeBookmark(sunnahId)
        } else {
            addBookmark(sunnahId)
        }
    }
}