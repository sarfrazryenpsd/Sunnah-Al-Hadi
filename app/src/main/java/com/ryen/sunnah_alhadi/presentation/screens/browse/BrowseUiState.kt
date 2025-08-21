package com.ryen.sunnah_alhadi.presentation.screens.browse

import com.ryen.sunnah_alhadi.R
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

enum class FilterType(val displayName: String, val icon: Int) {
    HAS_VERSES("Verses", R.drawable.ec_verse),
    HAS_SUPPLICATIONS("Supplications", R.drawable.ec_supplication),
    HAS_REFERENCES("References", R.drawable.ec_reference),
    HAS_NOTES("Notes", R.drawable.ec_note),
    HAS_PARABLES("Parables", R.drawable.ec_parable),
    HAS_SCHOLARLY_EXPLANATION("Scholarly Explanation", R.drawable.ec_scholar),
    HAS_EXPLANATION("Explanation", R.drawable.ec_explanation),
    HAS_WARNINGS("Warnings", R.drawable.ec_warning),
    HAS_BENEFITS("Benefits", R.drawable.ec_benefit),
}