package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.domain.model.Bookmark
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    suspend fun getAllBookmarks(): List<Bookmark>
    suspend fun getBookmarkedSunnahs(): List<Sunnah>
    suspend fun isBookmarked(sunnahId: String): Boolean
    suspend fun addBookmark(sunnahId: String)
    suspend fun removeBookmark(sunnahId: String)
    suspend fun toggleBookmark(sunnahId: String)
    fun getBookmarkedSunnahsFlow(): Flow<List<Sunnah>>

}