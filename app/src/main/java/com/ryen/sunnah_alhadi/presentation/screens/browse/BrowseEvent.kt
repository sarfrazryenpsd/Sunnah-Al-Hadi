package com.ryen.sunnah_alhadi.presentation.screens.browse

sealed class BrowseUiEvent {
    data class TabChanged(val tab: BrowseTab) : BrowseUiEvent()
    data class SearchQueryChanged(val query: String) : BrowseUiEvent()
    data class FilterToggled(val filter: FilterType) : BrowseUiEvent()
    data class SunnahCardClickedByIndex(val index: Int) : BrowseUiEvent()
    object ClosePager : BrowseUiEvent()
    data class PagerPageChanged(val index: Int) : BrowseUiEvent()
    data class ToggleBookmark(val sunnahId: String) : BrowseUiEvent()
    object RetryLoading : BrowseUiEvent()
    object ClearSearch : BrowseUiEvent()
    object ClearAllFilters : BrowseUiEvent()
}