package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.domain.model.Category

interface CategoryRepository {

    suspend fun getAllCategories(): List<Category>
    suspend fun getCategoryById(id: Int): Category?
    suspend fun getFeaturedCategories(): List<Category>

}