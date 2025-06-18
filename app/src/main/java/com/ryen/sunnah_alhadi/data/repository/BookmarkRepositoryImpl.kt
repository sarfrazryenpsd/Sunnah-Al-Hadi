package com.ryen.sunnah_alhadi.data.repository

import android.util.Log
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.SunnahBookmarked
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class BookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override suspend fun getAllBookmarks(): RepositoryResult<List<SunnahBookmarked>> {
        return try {
            val bookmarks = bookmarkDao.getAllBookmarks().map { it.toDomain() }
            RepositoryResult.Success(bookmarks)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error getting all bookmarks", e)
            RepositoryResult.Error(e, "Failed to load bookmarks")
        }
    }

    override suspend fun getBookmarkedSunnahs(): RepositoryResult<List<Sunnah>> {
        return try {
            val sunnahs = bookmarkDao.getBookmarkedSunnahs().map { it.toDomain(isBookmarked = true) }
            RepositoryResult.Success(sunnahs)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error getting bookmarked sunnahs", e)
            RepositoryResult.Error(e, "Failed to load bookmarked sunnahs")
        }
    }

    override fun getBookmarkedSunnahsFlow(): Flow<RepositoryResult<List<Sunnah>>> {
        return bookmarkDao.getBookmarkedSunnahsFlow()
            .map { entities ->
                try {
                    val sunnahs = entities.map { it.toDomain(isBookmarked = true) }
                    RepositoryResult.Success(sunnahs)
                } catch (e: Exception) {
                    Log.e("BookmarkRepo", "Error in bookmarked sunnahs flow", e)
                    RepositoryResult.Error(e, "Failed to load bookmarked sunnahs")
                }
            }
            .catch { e ->
                Log.e("BookmarkRepo", "Flow error", e)
                emit(RepositoryResult.Error(e, "Database connection error"))
            }
    }

    override suspend fun isBookmarked(sunnahId: String): RepositoryResult<Boolean> {
        return try {
            if (sunnahId.isBlank()) {
                return RepositoryResult.Error(
                    IllegalArgumentException("Sunnah ID cannot be blank"),
                    "Invalid sunnah ID"
                )
            }
            val isBookmarked = bookmarkDao.isBookmarked(sunnahId)
            RepositoryResult.Success(isBookmarked)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error checking bookmark status for $sunnahId", e)
            RepositoryResult.Error(e, "Failed to check bookmark status")
        }
    }

    override suspend fun addBookmark(sunnahId: String): RepositoryResult<Unit> {
        return try {
            if (sunnahId.isBlank()) {
                return RepositoryResult.Error(
                    IllegalArgumentException("Sunnah ID cannot be blank"),
                    "Invalid sunnah ID"
                )
            }
            val bookmark = BookmarkEntity(sunnahId = sunnahId)
            bookmarkDao.addBookmark(bookmark)
            Log.d("BookmarkRepo", "Added bookmark for $sunnahId")
            RepositoryResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error adding bookmark for $sunnahId", e)
            RepositoryResult.Error(e, "Failed to add bookmark")
        }
    }

    override suspend fun removeBookmark(sunnahId: String): RepositoryResult<Unit> {
        return try {
            if (sunnahId.isBlank()) {
                return RepositoryResult.Error(
                    IllegalArgumentException("Sunnah ID cannot be blank"),
                    "Invalid sunnah ID"
                )
            }
            bookmarkDao.removeBookmark(sunnahId)
            Log.d("BookmarkRepo", "Removed bookmark for $sunnahId")
            RepositoryResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error removing bookmark for $sunnahId", e)
            RepositoryResult.Error(e, "Failed to remove bookmark")
        }
    }

    override suspend fun toggleBookmark(sunnahId: String): RepositoryResult<Boolean> {
        return try {
            if (sunnahId.isBlank()) {
                return RepositoryResult.Error(
                    IllegalArgumentException("Sunnah ID cannot be blank"),
                    "Invalid sunnah ID"
                )
            }

            val isCurrentlyBookmarked = when (val result = isBookmarked(sunnahId)) {
                is RepositoryResult.Success -> result.data
                is RepositoryResult.Error -> return result
                else -> return RepositoryResult.Error(
                    IllegalStateException("Unexpected result type"),
                    "Failed to check bookmark status"
                )
            }

            val toggleResult = if (isCurrentlyBookmarked) {
                removeBookmark(sunnahId)
            } else {
                addBookmark(sunnahId)
            }

            when (toggleResult) {
                is RepositoryResult.Success -> RepositoryResult.Success(!isCurrentlyBookmarked)
                is RepositoryResult.Error -> toggleResult
                else -> RepositoryResult.Error(
                    IllegalStateException("Unexpected result type"),
                    "Failed to toggle bookmark"
                )
            }
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error toggling bookmark for $sunnahId", e)
            RepositoryResult.Error(e, "Failed to toggle bookmark")
        }
    }
}