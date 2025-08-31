package com.ryen.sunnah_alhadi.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.useCase.GetHomeDataUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetRecentlyViewedSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahCountsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GetCurrentSotdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.MarkSotdAsSeenUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.ShouldShowSotdCardUseCase
import com.ryen.sunnah_alhadi.presentation.util.PagerVisibilityState
import com.ryen.sunnah_alhadi.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val getCurrentSotdUseCase: GetCurrentSotdUseCase,
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val getSunnahCountsUseCase: GetSunnahCountsUseCase,
    private val getRecentlyViewedSunnahsUseCase: GetRecentlyViewedSunnahsUseCase,
    private val shouldShowSotdCardUseCase: ShouldShowSotdCardUseCase,
    private val markSotdAsSeenUseCase: MarkSotdAsSeenUseCase,
    private val generateNewSotdIdUseCase: GenerateNewSotdIdUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<HomeEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // ✅ NEW: Separate flow for SOTD overlay requests
    private val _sotdOverlayRequest = MutableSharedFlow<SotdOverlayRequest>()
    val sotdOverlayRequest = _sotdOverlayRequest.asSharedFlow()



    init {
        observeUserPreferences()
        loadHomeData()
        loadCountsAsync()
    }

    // ✅ Load counts asynchronously to prevent main thread blocking
    private fun loadCountsAsync() {
        viewModelScope.launch {
            try {
                val categoryIds = (0..29).toList()
                val categoryIdToSunnahCount = getSunnahCountsUseCase(categoryIds)

                // ✅ Update UI state on main thread
                _uiState.update {
                    it.copy(
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
            is HomeEvent.ToggleSotd -> requestSotdOverlay(SotdOverlayRequest.Manual)
            is HomeEvent.DismissSotd -> {}
            is HomeEvent.MarkSotdAsSeen -> markSotdAsSeen()
            is HomeEvent.ToggleDisclaimer -> toggleDisclaimer()
            is HomeEvent.NavigateToTopic -> {
                viewModelScope.launch {
                    _eventFlow.emit(event) // emit navigation event
                }
            }

            is HomeEvent.ToggleBookmark -> toggleBookmark(event.sunnahId)
            is HomeEvent.HandleNotificationLaunch -> handleNotificationLaunch()
            is HomeEvent.AutoShowSotdCheck -> autoShowSotdCheck()
            is HomeEvent.SunnahCardClicked -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        isPagerVisible = true,
                        selectedSunnahIndex = event.index
                    )
                }
                PagerVisibilityState.setPagerVisibility(true)
            }

            is HomeEvent.ClosePager -> {
                _uiState.update { currentState ->
                    currentState.copy(isPagerVisible = false)
                }
                PagerVisibilityState.setPagerVisibility(false)
            }

            is HomeEvent.PagerPageChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedSunnahIndex = event.index)
                }
            }
        }
    }

    private fun requestSotdOverlay(request: SotdOverlayRequest) {
        viewModelScope.launch {
            _sotdOverlayRequest.emit(request)
        }
    }

    // ✅ Separate method for auto-show check
    // ✅ UPDATED: Auto-show check now emits request instead of updating state
    private fun autoShowSotdCheck() {
        viewModelScope.launch {
            try {
                val shouldShow = shouldShowSotdCardUseCase()
                val sotdState = getCurrentSotdUseCase()

                val shouldAutoShow = shouldShow &&
                        sotdState.currentSotd != null &&
                        !sotdState.isSeen

                if (shouldAutoShow) {
                    // ✅ Update SOTD data but don't trigger overlay here
                    _uiState.update {
                        it.copy(sotd = sotdState.currentSotd)
                    }
                    // ✅ Emit auto-show request
                    _sotdOverlayRequest.emit(SotdOverlayRequest.AutoShow)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to check SOTD: ${e.message}")
                }
            }
        }
    }

    // ✅ UPDATED: Notification launch now emits request
    /*fun handleNotificationLaunch() {
        viewModelScope.launch {
            try {
                val sotdState = getCurrentSotdUseCase()

                if (sotdState.currentSotd != null) {
                    _uiState.update {
                        it.copy(sotd = sotdState.currentSotd)
                    }
                    // ✅ Emit notification request
                    _sotdOverlayRequest.emit(SotdOverlayRequest.FromNotification)
                } else {
                    // Generate new SOTD if none exists
                    generateNewSotdIdUseCase()
                    val newSotdState = getCurrentSotdUseCase()

                    _uiState.update {
                        it.copy(sotd = newSotdState.currentSotd)
                    }

                    if (newSotdState.currentSotd != null) {
                        _sotdOverlayRequest.emit(SotdOverlayRequest.FromNotification)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to load SOTD: ${e.message}")
                }
            }
        }
    }*/

    fun handleNotificationLaunch() {
        viewModelScope.launch {
            try {
                val sotdState = getCurrentSotdUseCase()

                _uiState.update {
                    it.copy(sotd = sotdState.currentSotd)
                }

                if (sotdState.currentSotd != null) {
                    _sotdOverlayRequest.emit(SotdOverlayRequest.FromNotification)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to load SOTD: ${e.message}")
                }
            }
        }
    }

    /*private fun observeUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesFlowUseCase()
                .distinctUntilChanged()
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
    }*/
    private fun observeUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesFlowUseCase()
                .distinctUntilChanged()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load preferences: ${exception.localizedMessage}",
                            isLoading = false
                        )
                    }
                }
                .collect { preferences ->
                    val previousSotdId = _uiState.value.sotd?.id
                    _uiState.update {
                        it.copy(
                            username = preferences.username,
                            error = null
                        )
                    }

                    // ✅ Refresh SOTD on preference changes
                    val sotdState = getCurrentSotdUseCase()
                    _uiState.update {
                        it.copy(sotd = sotdState.currentSotd)
                    }

                    // ✅ Auto-show if new SOTD appeared (e.g., from background worker)
                    if (sotdState.currentSotd?.id != previousSotdId && !sotdState.isSeen) {
                        _sotdOverlayRequest.emit(SotdOverlayRequest.AutoShow)
                    }
                }
        }
    }

    // ✅ Optimized loadHomeData with better thread management
    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

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
                        /*val finalSotdState = if (sotdState.currentSotd == null) {
                            generateNewSotdIdUseCase()
                            getCurrentSotdUseCase()
                        } else {
                            sotdState
                        }*/

                        // ✅ Update UI on main thread
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                username = homeData.userName,
                                featuredCategories = homeData.featuredCategories,
                                recentSotd = recentSotd,
                                sotd = sotdState.currentSotd,
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = homeDataResult.exception.message
                                    ?: "Failed to load home data",
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
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
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update {
                it.copy(
                    showDisclaimer = !currentState.showDisclaimer
                )
            }
        }
    }

    private fun toggleBookmark(sunnah: String) {
        viewModelScope.launch {
            try {
                toggleBookmarkUseCase(sunnah)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, error = "Failed to toggle bookmark: ${e.message}"
                    )
                }
            }
        }
    }
}

sealed class SotdOverlayRequest {
    object AutoShow : SotdOverlayRequest()
    object FromNotification : SotdOverlayRequest()
    object Manual : SotdOverlayRequest()
}
