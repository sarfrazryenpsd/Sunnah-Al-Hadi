package com.ryen.sunnah_alhadi.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.database.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    suspend fun getAllBookmarks(): List<BookmarkEntity>

    @Query("""
        SELECT s.* FROM sunnahs s 
        INNER JOIN bookmarks b ON s.id = b.sunnahId 
        ORDER BY b.bookmarkedAt DESC
    """)
    suspend fun getBookmarkedSunnahs(): List<SunnahEntity>

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE sunnahId = :sunnahId")
    suspend fun isBookmarked(sunnahId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE sunnahId = :sunnahId")
    suspend fun removeBookmark(sunnahId: String)
}