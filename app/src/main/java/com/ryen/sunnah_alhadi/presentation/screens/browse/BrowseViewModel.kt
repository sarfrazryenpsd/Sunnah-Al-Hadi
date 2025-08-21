package com.ryen.sunnah_alhadi.presentation.screens.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.useCase.GetAllSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetBookmarkedSunnahsFlowUseCase
import com.ryen.sunnah_alhadi.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val getAllSunnahsUseCase: GetAllSunnahsUseCase,
    private val getBookmarkedSunnahsFlowUseCase: GetBookmarkedSunnahsFlowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    private val searchDebounce = 150L
    private val searchJob = MutableStateFlow<Job?>(null)

    init {
        loadInitialData()
    }

    fun onEvent(event: BrowseUiEvent) {
        when (event) {
            is BrowseUiEvent.TabChanged -> handleTabChanged(event.tab)
            is BrowseUiEvent.SearchQueryChanged -> handleSearchQueryChanged(event.query)
            is BrowseUiEvent.FilterToggled -> handleFilterToggled(event.filter)
            is BrowseUiEvent.SunnahCardClickedByIndex -> handleSunnahCardClickedByIndex(event.index) // New event handler
            is BrowseUiEvent.RetryLoading -> loadInitialData()
            is BrowseUiEvent.ClearSearch -> handleClearSearch()
            is BrowseUiEvent.ClearAllFilters -> handleClearAllFilters()
            is BrowseUiEvent.ClosePager -> handleClosePager()
            is BrowseUiEvent.PagerPageChanged -> handlePagerPageChanged(event.index)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Get all sunnahs (unwrap Result)
                val allSunnahsResult = getAllSunnahsUseCase()
                if (allSunnahsResult !is Result.Success) {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load sunnahs") }
                    return@launch
                }
                val allSunnahs = allSunnahsResult.data

                // Observe bookmarked sunnahs (unwrap inside collect)
                getBookmarkedSunnahsFlowUseCase().collect { bookmarkedResult ->
                    if (bookmarkedResult is Result.Success) {
                        val bookmarkedSunnahs = bookmarkedResult.data

                        _uiState.update { currentState ->
                            currentState.copy(
                                allSunnahs = allSunnahs,
                                bookmarkedSunnahs = bookmarkedSunnahs,
                                filteredSunnahs = filterSunnahs(
                                    getCurrentTabSunnahs(currentState.currentTab, allSunnahs, bookmarkedSunnahs),
                                    currentState.searchQuery,
                                    currentState.selectedFilters
                                ),
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Failed to load bookmarks") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load Sunnahs: ${e.message}"
                    )
                }
            }
        }
    }

    private fun handleClosePager() {
        _uiState.update {
            it.copy(isPagerVisible = false, selectedSunnahIndex = 0)
        }
    }

    private fun handlePagerPageChanged(index: Int) {
        _uiState.update {
            it.copy(selectedSunnahIndex = index)
        }
    }

    // New method to handle index-based clicks
    private fun handleSunnahCardClickedByIndex(index: Int) {
        _uiState.update {
            it.copy(
                isPagerVisible = true,
                selectedSunnahIndex = index.coerceAtLeast(0)
            )
        }
    }

    private fun handleTabChanged(tab: BrowseTab) {
        _uiState.update { currentState ->
            val tabSunnahs = getCurrentTabSunnahs(tab, currentState.allSunnahs, currentState.bookmarkedSunnahs)
            currentState.copy(
                currentTab = tab,
                filteredSunnahs = filterSunnahs(tabSunnahs, currentState.searchQuery, currentState.selectedFilters)
            )
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Cancel previous search job
        searchJob.value?.cancel()

        // Start new debounced search
        searchJob.value = viewModelScope.launch {
            delay(searchDebounce)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        val currentState = _uiState.value
        val tabSunnahs = getCurrentTabSunnahs(
            currentState.currentTab,
            currentState.allSunnahs,
            currentState.bookmarkedSunnahs
        )

        _uiState.update {
            it.copy(
                filteredSunnahs = filterSunnahs(tabSunnahs, query, currentState.selectedFilters)
            )
        }
    }

    private fun handleFilterToggled(filter: FilterType) {
        _uiState.update { currentState ->
            val newFilters = if (currentState.selectedFilters.contains(filter)) {
                currentState.selectedFilters - filter
            } else {
                currentState.selectedFilters + filter
            }

            val tabSunnahs = getCurrentTabSunnahs(
                currentState.currentTab,
                currentState.allSunnahs,
                currentState.bookmarkedSunnahs
            )

            currentState.copy(
                selectedFilters = newFilters,
                filteredSunnahs = filterSunnahs(tabSunnahs, currentState.searchQuery, newFilters)
            )
        }
    }

    private fun handleClearSearch() {
        _uiState.update { currentState ->
            val tabSunnahs = getCurrentTabSunnahs(
                currentState.currentTab,
                currentState.allSunnahs,
                currentState.bookmarkedSunnahs
            )

            currentState.copy(
                searchQuery = "",
                filteredSunnahs = filterSunnahs(tabSunnahs, "", currentState.selectedFilters)
            )
        }
    }

    private fun handleClearAllFilters() {
        _uiState.update { currentState ->
            val tabSunnahs = getCurrentTabSunnahs(
                currentState.currentTab,
                currentState.allSunnahs,
                currentState.bookmarkedSunnahs
            )

            currentState.copy(
                selectedFilters = emptySet(),
                filteredSunnahs = filterSunnahs(tabSunnahs, currentState.searchQuery, emptySet())
            )
        }
    }

    // Core filtering logic - combines search and content filters
    private fun filterSunnahs(
        sunnahs: List<Sunnah>,
        query: String,
        filters: Set<FilterType>
    ): List<Sunnah> {
        return sunnahs.filter { sunnah ->
            // Title search (case-insensitive, minimum 2 characters)
            val matchesSearch = query.length < 2 ||
                    sunnah.title.contains(query.trim(), ignoreCase = true)

            // Apply content filters (AND logic)
            val matchesFilters = if (filters.isEmpty()) true else {
                filters.all { filter -> applyContentFilter(sunnah, filter) }
            }

            matchesSearch && matchesFilters
        }
    }

    private fun getCurrentTabSunnahs(
        tab: BrowseTab,
        allSunnahs: List<Sunnah>,
        bookmarkedSunnahs: List<Sunnah>
    ): List<Sunnah> {
        return when (tab) {
            BrowseTab.ALL_SUNNAH -> allSunnahs
            BrowseTab.SAVED -> bookmarkedSunnahs
        }
    }

    // Content filter application logic
    private fun applyContentFilter(sunnah: Sunnah, filter: FilterType): Boolean {
        return when (filter) {
            FilterType.HAS_VERSES -> {
                sunnah.body.any { block ->
                    block.subtype.equals("verse", ignoreCase = true)
                }
            }
            FilterType.HAS_SUPPLICATIONS -> {
                sunnah.body.any { block ->
                    block.subtype.equals("supplication", ignoreCase = true)
                }
            }
            FilterType.HAS_REFERENCES -> {
                !sunnah.references.isNullOrEmpty()
            }
            FilterType.HAS_PARABLES-> {
                !sunnah.extra.isNullOrEmpty() && sunnah.extra.any { extra ->
                    extra.type.name.equals("parable", ignoreCase = true)
                }
            }
            FilterType.HAS_NOTES -> {
                !sunnah.extra.isNullOrEmpty() && sunnah.extra.any { extra ->
                    extra.type.name.equals("notes", ignoreCase = true)
                }
            }
            FilterType.HAS_BENEFITS -> {
                !sunnah.extra.isNullOrEmpty() && sunnah.extra.any { extra ->
                    extra.type.name.equals("benefit", ignoreCase = true)
                }
            }
            FilterType.HAS_WARNINGS -> {
                !sunnah.extra.isNullOrEmpty() && sunnah.extra.any { extra ->
                    extra.type.name.equals("warning", ignoreCase = true)
                }
            }
            FilterType.HAS_EXPLANATION -> {
                !sunnah.extra.isNullOrEmpty() && sunnah.extra.any { extra ->
                    extra.type.name.equals("explanation", ignoreCase = true)
                }
            }
            FilterType.HAS_SCHOLARLY_EXPLANATION -> {
                !sunnah.extra.isNullOrEmpty() && sunnah.extra.any { extra ->
                    extra.type.name.equals("scholarly_explanation", ignoreCase = true)
                }
            }
        }
    }
}