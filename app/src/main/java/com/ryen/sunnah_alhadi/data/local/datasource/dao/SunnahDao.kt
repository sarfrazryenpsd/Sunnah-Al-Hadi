package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahWithBookmark
import com.ryen.sunnah_alhadi.domain.model.CategorySunnahCount
import kotlinx.coroutines.flow.Flow

@Dao
interface SunnahDao {
    @Query("SELECT * FROM sunnahs")
    suspend fun getAllSunnahs(): List<SunnahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSunnah(sunnah: SunnahEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sunnahs: List<SunnahEntity>)

    @Delete
    suspend fun delete(sunnah: SunnahEntity)

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
    SELECT categoryId, COUNT(*) as sunnahCount
    FROM sunnahs
    WHERE categoryId IN (:ids)
    GROUP BY categoryId
    """)
    suspend fun getSunnahCountByCategoryIds(ids: Set<Int>): List<CategorySunnahCount>

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

    @Query("""
    SELECT id FROM sunnahs 
    WHERE id NOT IN (:excludeIds) 
    ORDER BY RANDOM() 
    LIMIT 1
    """)
    suspend fun getRandomSunnahIdForSotd(excludeIds: List<String>): String

    @Query("SELECT * FROM sunnahs WHERE id IN (:sunnahIds)")
    suspend fun getSunnahsByIds(sunnahIds: List<String>): List<SunnahEntity>


}