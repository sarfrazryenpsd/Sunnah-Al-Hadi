package com.ryen.sunnah_alhadi.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SunnahDao {
    @Query("SELECT * FROM sunnahs WHERE categoryId = :categoryId")
    fun getSunnahsByCategory(categoryId: Int): Flow<List<SunnahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sunnahs: List<SunnahEntity>)
}