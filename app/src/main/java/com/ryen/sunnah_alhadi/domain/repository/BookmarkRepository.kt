package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.data.util.RepositoryResult
import com.ryen.sunnah_alhadi.domain.model.SunnahBookmarked
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    suspend fun getAllBookmarks(): RepositoryResult<List<SunnahBookmarked>>
    suspend fun getBookmarkedSunnahs(): RepositoryResult<List<Sunnah>>
    suspend fun isBookmarked(sunnahId: String): RepositoryResult<Boolean>
    suspend fun addBookmark(sunnahId: String): RepositoryResult<Unit>
    suspend fun removeBookmark(sunnahId: String): RepositoryResult<Unit>
    suspend fun toggleBookmark(sunnahId: String): RepositoryResult<Boolean>
    fun getBookmarkedSunnahsFlow(): Flow<RepositoryResult<List<Sunnah>>>

}