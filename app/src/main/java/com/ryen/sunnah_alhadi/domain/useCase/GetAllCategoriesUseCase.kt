package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository

class GetAllCategoriesUseCase constructor(
    private val categoryRepository: CategoryRepository
): NoParamUseCase<List<Category>>(){
    override suspend fun execute(): List<Category> {
        return categoryRepository.getAllCategories()
    }
}