package com.ryen.sunnah_alhadi.presentation.screens.allTopics

import com.ryen.sunnah_alhadi.domain.model.Category

data class AllTopicsUiState(
    val topics: List<TopicWithCount> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class TopicWithCount(
    val category: Category,
    val sunnahCount: Int,
    val imageRes: Int // 3D illustration resource ID
)