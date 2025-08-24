package com.ryen.sunnah_alhadi.presentation.screens.topic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.useCase.GetTopicWithSunnahsUseCase
import com.ryen.sunnah_alhadi.domain.useCase.ToggleBookmarkUseCase
import com.ryen.sunnah_alhadi.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val getTopicWithSunnahsUseCase: GetTopicWithSunnahsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
) : ViewModel() {

    private var categoryId: Int = 0

    private val _uiState = MutableStateFlow(TopicUiState())
    val uiState: StateFlow<TopicUiState> = _uiState.asStateFlow()

    fun initialize(categoryId: Int) {
        this.categoryId = categoryId
        loadTopicData()
    }

    fun onEvent(event: TopicUiEvent) {
        when (event) {
            is TopicUiEvent.RetryLoading -> {
                loadTopicData()
            }
            is TopicUiEvent.SunnahCardClicked -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        isPagerVisible = true,
                        selectedSunnahIndex = event.index
                    )
                }
            }
            is TopicUiEvent.ToggleBookmark -> {
                toggleBookmark(event.sunnahId)
            }

            is TopicUiEvent.ClosePager -> {
                _uiState.update { currentState ->
                    currentState.copy(isPagerVisible = false)
                }
            }
            is TopicUiEvent.PagerPageChanged -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedSunnahIndex = event.index)
                }
            }
        }
    }

    private fun loadTopicData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                when (val result = getTopicWithSunnahsUseCase(categoryId)) {
                    is Result.Success -> {
                        val topicWithSunnahs = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                category = topicWithSunnahs.category,
                                sunnahs = topicWithSunnahs.sunnahs,
                                error = null
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "An error occurred"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
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