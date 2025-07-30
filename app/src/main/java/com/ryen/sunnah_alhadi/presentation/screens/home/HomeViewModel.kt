package com.ryen.sunnah_alhadi.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetRecentlyViewedSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahCountsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val getCurrentSotdUseCase: GetCurrentSotdUseCase,
    private val getSunnahByIdUseCase: GetSunnahByIdUseCase,
    private val getSunnahCountsUseCase: GetSunnahCountsUseCase,
    private val getRecentlyViewedSunnahsUseCase: GetRecentlyViewedSunnahsUseCase,
    private val shouldShowSotdCardUseCase: ShouldShowSotdCardUseCase,
    private val markSotdAsSeenUseCase: MarkSotdAsSeenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    init {
        loadHomeData()
    }

    fun loadCounts(categoryIds: List<Int>) {
        viewModelScope.launch {
            val categoryIdToSunnahCount = getSunnahCountsUseCase(categoryIds)
            _uiState.value = _uiState.value.copy(
                sunnahCount = categoryIdToSunnahCount
            )
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ToggleSotd -> toggleSotdOverlay()
            is HomeEvent.DismissSotd -> dismissSotdOverlay()
            is HomeEvent.MarkSotdAsSeen -> markSotdAsSeen()
            is HomeEvent.ToggleDisclaimer -> toggleDisclaimer()
            is HomeEvent.NavigateToAllTopics -> { /* Handle navigation */ }
            is HomeEvent.NavigateToTopic -> { /* Handle navigation */ }
            is HomeEvent.OpenSunnah -> { /* Handle navigation */ }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Load all data concurrently
                val homeDataDeferred = async { getHomeDataUseCase() }
                val sotdStateDeferred = async { getCurrentSotdUseCase() }
                val recentSotdDeferred = async { getRecentlyViewedSunnahsUseCase() }
                val shouldShowSotdDeferred = async { shouldShowSotdCardUseCase() }

                val homeDataResult = homeDataDeferred.await()
                val sotdState = sotdStateDeferred.await()
                val recentSotd = recentSotdDeferred.await()
                val shouldShowSotd = shouldShowSotdDeferred.await()

                // Process results and handle potential errors
                when (homeDataResult) {
                    is Result.Success -> {
                        val homeData = homeDataResult.data


                        // Determine if SOTD should auto-show (first launch today + not seen)
                        val autoShowSotd = shouldShowSotd &&
                                sotdState.currentSotd != null &&
                                !sotdState.isSeen

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userName = homeData.userName,
                            featuredCategories = homeData.featuredCategories,
                            recentSotd = recentSotd,
                            sotd = sotdState.currentSotd,
                            showSotd = autoShowSotd
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = homeDataResult.exception.message ?: "Failed to load home data"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun toggleSotdOverlay() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            showSotd = !currentState.showSotd
        )
    }

    private fun dismissSotdOverlay() {
        _uiState.value = _uiState.value.copy(showSotd = false)
        // Mark SOTD as seen when dismissed
        markSotdAsSeen()
    }

    private fun markSotdAsSeen() {
        viewModelScope.launch {
            try {
                markSotdAsSeenUseCase()
            } catch (e: Exception) {
                // Handle error silently or show toast
            }
        }
    }

    private fun toggleDisclaimer() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            showDisclaimer = !currentState.showDisclaimer
        )
    }

    // Helper function to refresh data (can be called from pull-to-refresh)
    fun refreshData() {
        loadHomeData()
    }
}