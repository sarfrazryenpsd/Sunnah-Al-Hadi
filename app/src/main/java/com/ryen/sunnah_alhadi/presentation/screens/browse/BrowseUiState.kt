package com.ryen.sunnah_alhadi.presentation.screens.browse

import com.ryen.sunnah_alhadi.domain.model.Sunnah

data class BrowseUiState(
    val currentTab: BrowseTab = BrowseTab.ALL_SUNNAH,
    val searchQuery: String = "",
    val allSunnahs: List<Sunnah> = emptyList(),
    val bookmarkedSunnahs: List<Sunnah> = emptyList(),
    val filteredSunnahs: List<Sunnah> = emptyList(),
    val selectedFilters: Set<FilterType> = emptySet(),
    val isPagerVisible: Boolean = false,
    val selectedSunnahIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class BrowseTab {
    ALL_SUNNAH,
    SAVED
}

enum class FilterType(val displayName: String, val icon: String) {
    HAS_VERSES("Verses", "📖"),
    HAS_SUPPLICATIONS("Supplications", "🤲"),
    HAS_REFERENCES("References", "📚"),
    HAS_NOTES("Notes", "📝"),
    HAS_PARABLES("Parables", "🌳"),
    HAS_SCHOLARLY_EXPLANATION("Scholarly Explanation", "🎓"),
    HAS_EXPLANATION("Explanation", "💡"),
    HAS_WARNINGS("Warnings", "⚠️"),
    HAS_BENEFITS("Benefits", "🌟"),
}