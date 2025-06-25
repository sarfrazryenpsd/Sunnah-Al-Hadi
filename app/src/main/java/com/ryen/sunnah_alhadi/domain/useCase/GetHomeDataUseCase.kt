package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.util.Result

class GetHomeDataUseCase(
    private val categoryRepository: CategoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : NoParamUseCase<Result<HomeData>>() {

    override suspend fun execute(): Result<HomeData> {
        return try {
            val featuredCategories = categoryRepository.getFeaturedCategories()
            val userPrefs = userPreferencesRepository.getUserPreferences()

            Result.Success(HomeData(
                greeting = userPrefs.username.ifBlank { "Brother/Sister" },
                featuredCategories = featuredCategories
            ))
        } catch (e: Exception) {
            Result.Error(e, "Failed to load home data")
        }
    }
}

data class HomeData(
    val greeting: String,
    val featuredCategories: List<Category>
)