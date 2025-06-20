package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahWithBookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface SunnahDao {
    @Query("SELECT * FROM sunnahs")
    suspend fun getAllSunnahs(): List<SunnahEntity>

    @Query("SELECT * FROM sunnahs WHERE id = :id")
    suspend fun getSunnahById(id: String): SunnahEntity?

    @Query("""
        SELECT s.*, 
               CASE WHEN b.sunnahId IS NOT NULL THEN 1 ELSE 0 END as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        LEFT JOIN bookmarks b ON s.id = b.sunnahId
        ORDER BY s.title ASC
    """)
    suspend fun getAllSunnahsWithBookmarkStatus(): List<SunnahWithBookmark>

    @Query("""
        SELECT s.*, 
               1 as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        INNER JOIN bookmarks b ON s.id = b.sunnahId
        ORDER BY b.bookmarkedAt DESC
    """)
    suspend fun getBookmarkedSunnahs(): List<SunnahWithBookmark>

    @Query("""
        SELECT s.*, 
               1 as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        INNER JOIN bookmarks b ON s.id = b.sunnahId
        ORDER BY b.bookmarkedAt DESC
    """)
    fun getBookmarkedSunnahsFlow(): Flow<List<SunnahWithBookmark>>

    @Query("""
        SELECT s.*, 
               CASE WHEN b.sunnahId IS NOT NULL THEN 1 ELSE 0 END as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        LEFT JOIN bookmarks b ON s.id = b.sunnahId
        ORDER BY s.title ASC
    """)
    fun getAllSunnahsWithBookmarkStatusFlow(): Flow<List<SunnahWithBookmark>>

    @Query("""
        SELECT s.*, 
               CASE WHEN b.sunnahId IS NOT NULL THEN 1 ELSE 0 END as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        LEFT JOIN bookmarks b ON s.id = b.sunnahId
        WHERE s.categoryId = :categoryId
        ORDER BY s.title ASC
    """)
    suspend fun getSunnahsByCategoryWithBookmarkStatus(categoryId: Int): List<SunnahWithBookmark>

    @Query("""
        SELECT s.*, 
               CASE WHEN b.sunnahId IS NOT NULL THEN 1 ELSE 0 END as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        LEFT JOIN bookmarks b ON s.id = b.sunnahId
        WHERE s.categoryId = :categoryId
        ORDER BY s.title ASC
    """)
    fun getSunnahsByCategoryWithBookmarkStatusFlow(categoryId: Int): Flow<List<SunnahWithBookmark>>

    @Query("""
        SELECT s.*, 
               CASE WHEN b.sunnahId IS NOT NULL THEN 1 ELSE 0 END as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        LEFT JOIN bookmarks b ON s.id = b.sunnahId
        WHERE s.id = :id
    """)
    suspend fun getSunnahByIdWithBookmarkStatus(id: String): SunnahWithBookmark?

    @Query("SELECT * FROM sunnahs WHERE categoryId = :categoryId")
    suspend fun getSunnahsByCategory(categoryId: Int): List<SunnahEntity>

    @Query("""
        SELECT s.*, 
               CASE WHEN b.sunnahId IS NOT NULL THEN 1 ELSE 0 END as isBookmarked,
               b.bookmarkedAt as bookmarkedAt
        FROM sunnahs s 
        LEFT JOIN bookmarks b ON s.id = b.sunnahId
        ORDER BY RANDOM()
    """)
    suspend fun getRandomSunnahsWithBookmarkStatus(): List<SunnahWithBookmark>

}