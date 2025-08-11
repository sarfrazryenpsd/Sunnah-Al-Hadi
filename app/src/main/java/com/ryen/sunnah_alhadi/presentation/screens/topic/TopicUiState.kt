package com.ryen.sunnah_alhadi.presentation.screens.topic

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah

data class TopicUiState(
    val isLoading: Boolean = true,
    val category: Category? = null,
    val sunnahs: List<Sunnah> = emptyList(),
    val error: String? = null,
    val isPagerVisible: Boolean = false,
    val selectedSunnahIndex: Int = 0
)