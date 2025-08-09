package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
): NoParamUseCase<List<Category>>(){
    override suspend fun execute(): List<Category> {
        return categoryRepository.getAllCategories()
    }
}