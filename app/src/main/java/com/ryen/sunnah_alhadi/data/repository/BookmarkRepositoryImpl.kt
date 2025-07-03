package com.ryen.sunnah_alhadi.data.repository

import android.util.Log
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.model.SunnahBookmarked
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override suspend fun getAllBookmarks(): Result<List<SunnahBookmarked>> {
        return try {
            val bookmarks = bookmarkDao.getAllBookmarks().map { it.toDomain() }
            Result.Success(bookmarks)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error getting all bookmarks", e)
            Result.Error(e, "Failed to load bookmarks")
        }
    }

    override suspend fun getBookmarkedSunnahs(): Result<List<Sunnah>> {
        return try {
            val sunnahs = bookmarkDao.getBookmarkedSunnahs().map { it.toDomain(isBookmarked = true) }
            Result.Success(sunnahs)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error getting bookmarked sunnahs", e)
            Result.Error(e, "Failed to load bookmarked sunnahs")
        }
    }

    override fun getBookmarkedSunnahsFlow(): Flow<Result<List<Sunnah>>> {
        return bookmarkDao.getBookmarkedSunnahsFlow()
            .map { entities ->
                try {
                    val sunnahs = entities.map { it.toDomain(isBookmarked = true) }
                    Result.Success(sunnahs)
                } catch (e: Exception) {
                    Log.e("BookmarkRepo", "Error in bookmarked sunnahs flow", e)
                    Result.Error(e, "Failed to load bookmarked sunnahs")
                }
            }
            .catch { e ->
                Log.e("BookmarkRepo", "Flow error", e)
                emit(Result.Error(e, "Database connection error"))
            }
    }

    override suspend fun isBookmarked(sunnahId: String): Result<Boolean> {
        return try {
            val isBookmarked = bookmarkDao.isBookmarked(sunnahId)
            Result.Success(isBookmarked)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error checking bookmark status for $sunnahId", e)
            Result.Error(e, "Failed to check bookmark status")
        }
    }

    override suspend fun addBookmark(sunnahId: String): Result<Unit> {
        return try {

            val bookmark = BookmarkEntity(sunnahId = sunnahId)
            bookmarkDao.addBookmark(bookmark)
            Log.d("BookmarkRepo", "Added bookmark for $sunnahId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error adding bookmark for $sunnahId", e)
            Result.Error(e, "Failed to add bookmark")
        }
    }

    override suspend fun removeBookmark(sunnahId: String): Result<Unit> {
        return try {
            bookmarkDao.removeBookmark(sunnahId)
            Log.d("BookmarkRepo", "Removed bookmark for $sunnahId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error removing bookmark for $sunnahId", e)
            Result.Error(e, "Failed to remove bookmark")
        }
    }

    override suspend fun toggleBookmark(sunnahId: String): Result<Boolean> {
        return try {

            val isCurrentlyBookmarked = when (val result = isBookmarked(sunnahId)) {
                is Result.Success -> result.data
                is Result.Error -> return result
            }

            val toggleResult = if (isCurrentlyBookmarked) {
                removeBookmark(sunnahId)
            } else {
                addBookmark(sunnahId)
            }

            when (toggleResult) {
                is Result.Success -> Result.Success(!isCurrentlyBookmarked)
                is Result.Error -> toggleResult
            }
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error toggling bookmark for $sunnahId", e)
            Result.Error(e, "Failed to toggle bookmark")
        }
    }
}