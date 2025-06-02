package com.ryen.sunnah_alhadi.domain.useCase.homeUseCases

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository

class GetFeaturedCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): List<Category> {
        return categoryRepository.getFeaturedCategories().shuffled()
    }
}