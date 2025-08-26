package com.ryen.sunnah_alhadi.data.repository

import android.util.Log
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BookmarkDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.BookmarkRepository
import com.ryen.sunnah_alhadi.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    val bookmarkDao: BookmarkDao,
    private val ioDispatcher: CoroutineDispatcher

) : BookmarkRepository {

    override fun getBookmarkedSunnahsFlow(): Flow<Result<List<Sunnah>>> =
        bookmarkDao.getBookmarkedSunnahsFlow()
            .map { entities ->
                try {
                    // Consider if toDomain is expensive - might need optimization
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
            .flowOn(ioDispatcher)


    override suspend fun toggleBookmark(sunnahId: String): Result<Boolean> = withContext(ioDispatcher) {
        try {
            // Use the DAO's optimized transaction method
            val isNowBookmarked = bookmarkDao.toggleBookmark(sunnahId)
            Log.d("BookmarkRepo", "${if (isNowBookmarked) "Added" else "Removed"} bookmark for $sunnahId")
            Result.Success(isNowBookmarked)
        } catch (e: Exception) {
            Log.e("BookmarkRepo", "Error toggling bookmark for $sunnahId", e)
            Result.Error(e, "Failed to toggle bookmark")
        }
    }
}