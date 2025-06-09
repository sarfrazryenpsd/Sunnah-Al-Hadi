package com.ryen.sunnah_alhadi.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SunnahDao {
    @Query("SELECT * FROM sunnahs")
    suspend fun getAllSunnahs(): List<SunnahEntity>

    @Query("SELECT * FROM sunnahs WHERE id = :id")
    suspend fun getSunnahById(id: String): SunnahEntity?

    @Query("SELECT * FROM sunnahs WHERE categoryId = :categoryId")
    suspend fun getSunnahsByCategory(categoryId: Int): List<SunnahEntity>

    @Query("SELECT * FROM sunnahs ORDER BY RANDOM()")
    suspend fun getRandomSunnahs(): List<SunnahEntity>


}