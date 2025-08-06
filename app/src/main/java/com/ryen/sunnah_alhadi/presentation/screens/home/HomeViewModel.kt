package com.ryen.sunnah_alhadi.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetRecentlyViewedSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahCountsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val getCurrentSotdUseCase: GetCurrentSotdUseCase,
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val getSunnahByIdUseCase: GetSunnahByIdUseCase,
    private val getSunnahCountsUseCase: GetSunnahCountsUseCase,
    private val getRecentlyViewedSunnahsUseCase: GetRecentlyViewedSunnahsUseCase,
    private val shouldShowSotdCardUseCase: ShouldShowSotdCardUseCase,
    private val markSotdAsSeenUseCase: MarkSotdAsSeenUseCase,
    private val generateNewSotdIdUseCase: GenerateNewSotdIdUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        // ✅ Move heavy operations off main thread
        loadCountsAsync()
        observeUserPreferences()
    }

    // ✅ Load counts asynchronously to prevent main thread blocking
    private fun loadCountsAsync() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val categoryIds = (0..29).toList()
                val categoryIdToSunnahCount = getSunnahCountsUseCase(categoryIds)

                // ✅ Update UI state on main thread
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        sunnahCount = categoryIdToSunnahCount
                    )
                }
            } catch (e: Exception) {
                // Handle error silently or log
            }
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
            is HomeEvent.HandleNotificationLaunch -> handleNotificationLaunch()
            is HomeEvent.AutoShowSotdCheck -> autoShowSotdCheck()
        }
    }

    // ✅ Separate method for auto-show check
    private fun autoShowSotdCheck() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val shouldShow = shouldShowSotdCardUseCase()
                val sotdState = getCurrentSotdUseCase()

                val shouldAutoShow = shouldShow &&
                        sotdState.currentSotd != null &&
                        !sotdState.isSeen

                withContext(Dispatchers.Main) {
                    if (shouldAutoShow) {
                        _uiState.update {
                            it.copy(
                                sotd = sotdState.currentSotd,
                                showSotd = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(error = "Failed to check SOTD: ${e.message}")
                    }
                }
            }
        }
    }

    fun handleNotificationLaunch() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val sotdState = getCurrentSotdUseCase()

                if (sotdState.currentSotd != null) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                sotd = sotdState.currentSotd,
                                showSotd = true
                            )
                        }
                    }
                } else {
                    // Generate new SOTD if none exists
                    generateNewSotdIdUseCase()
                    val newSotdState = getCurrentSotdUseCase()

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                sotd = newSotdState.currentSotd,
                                showSotd = newSotdState.currentSotd != null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(error = "Failed to load SOTD: ${e.message}")
                    }
                }
            }
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesFlowUseCase()
                .distinctUntilChanged() // ✅ Prevent unnecessary recompositions
                .flowOn(ioDispatcher) // ✅ Process on IO thread
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load preferences: ${exception.localizedMessage}",
                            isLoading = false
                        )
                    }
                }
                .collect { preferences ->
                    _uiState.update {
                        it.copy(
                            username = preferences.username,
                            error = null
                        )
                    }
                }
        }
    }

    // ✅ Optimized loadHomeData with better thread management
    private fun loadHomeData() {
        viewModelScope.launch(ioDispatcher) {
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            try {
                // Load all data concurrently on IO thread
                val homeDataDeferred = async { getHomeDataUseCase() }
                val sotdStateDeferred = async { getCurrentSotdUseCase() }
                val recentSotdDeferred = async { getRecentlyViewedSunnahsUseCase() }

                val homeDataResult = homeDataDeferred.await()
                val sotdState = sotdStateDeferred.await()
                val recentSotd = recentSotdDeferred.await()

                when (homeDataResult) {
                    is Result.Success -> {
                        val homeData = homeDataResult.data

                        // Generate SOTD if none exists
                        val finalSotdState = if (sotdState.currentSotd == null) {
                            generateNewSotdIdUseCase()
                            getCurrentSotdUseCase()
                        } else {
                            sotdState
                        }

                        // ✅ Update UI on main thread
                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                username = homeData.userName,
                                featuredCategories = homeData.featuredCategories,
                                recentSotd = recentSotd,
                                sotd = finalSotdState.currentSotd,
                                showSotd = false // Don't auto-show here, let MainNavigation handle it
                            )
                        }
                    }
                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = homeDataResult.exception.message ?: "Failed to load home data",
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
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
        markSotdAsSeen()
    }

    private fun markSotdAsSeen() {
        viewModelScope.launch(ioDispatcher) {
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
}
