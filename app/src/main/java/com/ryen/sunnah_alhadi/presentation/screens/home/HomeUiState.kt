package com.ryen.sunnah_alhadi.presentation.screens.home

import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah

data class HomeUiState(
    val userName: String = "",
    val featuredCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val showSotd: Boolean = false,
    val showDisclaimer: Boolean = false,
    val sotd: Sunnah? = null,
    val recentSotd: List<Sunnah> = emptyList(),
    val homeSunnah: Sunnah? = null,
)