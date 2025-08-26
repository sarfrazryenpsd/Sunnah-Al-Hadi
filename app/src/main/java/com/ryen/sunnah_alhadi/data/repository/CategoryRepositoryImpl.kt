package com.ryen.sunnah_alhadi.data.repository

import com.ryen.sunnah_alhadi.data.local.datasource.dao.CategoryDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher
) : CategoryRepository {

    override suspend fun getAllCategories(): List<Category> =
        withContext(ioDispatcher) {
            categoryDao.getAllCategories().map { it.toDomain() }
        }

    override suspend fun getCategoryById(id: Int): Category? =
        withContext(ioDispatcher) {
            categoryDao.getCategoryById(id)?.toDomain()
        }

    override suspend fun getFeaturedCategories(): List<Category> =
        withContext(ioDispatcher) {
            categoryDao.getFeaturedCategories().map { it.toDomain() }
        }
}