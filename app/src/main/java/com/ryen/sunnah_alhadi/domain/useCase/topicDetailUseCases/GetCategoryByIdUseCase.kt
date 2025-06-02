package com.ryen.sunnah_alhadi.domain.useCase.topicDetailUseCases

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository

class GetCategoryByIdUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: Int): Category? {
        return categoryRepository.getCategoryById(categoryId)
    }
}