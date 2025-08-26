package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("""
        SELECT s.* FROM sunnahs s 
        INNER JOIN bookmarks b ON s.id = b.sunnahId 
        ORDER BY b.bookmarkedAt DESC
    """)
    fun getBookmarkedSunnahsFlow(): Flow<List<SunnahEntity>>

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE sunnahId = :sunnahId")
    suspend fun isBookmarked(sunnahId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE sunnahId = :sunnahId")
    suspend fun removeBookmark(sunnahId: String)

    // Toggle bookmark - returns true if added, false if removed
    @Transaction
    suspend fun toggleBookmark(sunnahId: String): Boolean {
        return if (isBookmarked(sunnahId)) {
            removeBookmark(sunnahId)
            false
        } else {
            addBookmark(BookmarkEntity(sunnahId))
            true
        }
    }
}