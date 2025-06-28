package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.local.datasource.dao.SunnahDao
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity

@Dao
interface TestSunnahDao : SunnahDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSunnah(sunnah: SunnahEntity)
}

@Dao
interface TestCategoryDao : CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)
}