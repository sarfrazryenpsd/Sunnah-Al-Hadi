package com.ryen.sunnah_alhadi.domain.useCase

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.repository.CategoryRepository
import com.ryen.sunnah_alhadi.domain.repository.SunnahRepository

class GetTopicWithSunnahsUseCase(
    private val categoryRepository: CategoryRepository,
    private val sunnahRepository: SunnahRepository
) : UseCase<Int, TopicWithSunnahs>() {

    override suspend fun execute(parameters: Int): TopicWithSunnahs {
        val category = categoryRepository.getCategoryById(parameters)
            ?: throw IllegalArgumentException("Category not found")
        val sunnahs = sunnahRepository.getSunnahsByCategory(parameters)

        return TopicWithSunnahs(
            category = category,
            sunnahs = sunnahs
        )
    }
}

data class TopicWithSunnahs(
    val category: Category,
    val sunnahs: List<Sunnah>
)