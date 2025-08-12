package com.ryen.sunnah_alhadi.presentation.screens.browse

import com.ryen.sunnah_alhadi.domain.model.Sunnah

sealed class BrowseUiEvent {
    data class TabChanged(val tab: BrowseTab) : BrowseUiEvent()
    data class SearchQueryChanged(val query: String) : BrowseUiEvent()
    data class FilterToggled(val filter: FilterType) : BrowseUiEvent()
    data class SunnahCardClicked(val sunnah: Sunnah) : BrowseUiEvent()
    object RetryLoading : BrowseUiEvent()
    object ClearSearch : BrowseUiEvent()
    object ClearAllFilters : BrowseUiEvent()
}