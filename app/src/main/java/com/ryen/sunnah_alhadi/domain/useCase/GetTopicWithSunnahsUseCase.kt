package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.util.Result
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository
import javax.inject.Inject

class GetTopicWithSunnahsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val sunnahRepository: SunnahRepository
) : UseCase<Int, Result<TopicWithSunnahs>>() {

    override suspend fun execute(parameters: Int): Result<TopicWithSunnahs> {
        return try {
            val category = categoryRepository.getCategoryById(parameters)
                ?: return Result.Error(
                    IllegalArgumentException("Category not found"),
                    "Topic not found"
                )

            when (val sunnahsResult = sunnahRepository.getSunnahsByCategory(parameters)) {
                is Result.Success -> Result.Success(
                    TopicWithSunnahs(category, sunnahsResult.data)
                )
                is Result.Error -> sunnahsResult
            }
        } catch (e: Exception) {
            Result.Error(e, "Failed to load topic data")
        }
    }
}

data class TopicWithSunnahs(
    val category: Category,
    val sunnahs: List<Sunnah>
)