package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map { it.toDomain() }
    }

    override suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun getFeaturedCategories(): List<Category> {
        return categoryDao.getFeaturedCategories().map { it.toDomain() }
    }
}