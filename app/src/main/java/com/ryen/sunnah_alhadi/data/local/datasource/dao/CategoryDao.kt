package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?

    @Query("SELECT * FROM categories ORDER BY RANDOM() LIMIT 7")
    suspend fun getFeaturedCategories(): List<CategoryEntity>
}