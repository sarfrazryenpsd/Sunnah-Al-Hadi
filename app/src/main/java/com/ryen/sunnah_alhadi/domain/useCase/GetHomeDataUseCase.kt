package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository

class GetHomeDataUseCase(
    private val categoryRepository: CategoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<HomeData>() {

    override suspend fun execute(): HomeData {
        val featuredCategories = categoryRepository.getFeaturedCategories()
        val userPrefs = userPreferencesRepository.getUserPreferences()

        return HomeData(
            greeting = userPrefs.username.ifBlank { "Brother/Sister" },
            featuredCategories = featuredCategories
        )
    }
}

data class HomeData(
    val greeting: String,
    val featuredCategories: List<Category>
)