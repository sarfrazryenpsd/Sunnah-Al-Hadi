package com.ryen.sunnah_alhadi.presentation.screens.allTopics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.useCase.GetAllCategoriesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahCountsUseCase
import com.ryen.sunnah_alhadi.presentation.util.CategoryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTopicsViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getSunnahCountsUseCase: GetSunnahCountsUseCase
) : ViewModel() {

    // Map category IDs to their corresponding 3D illustration resources
    private val categoryImageMap = CategoryUtils.categoryImageMap

    private val _uiState = MutableStateFlow(AllTopicsUiState())
    val uiState: StateFlow<AllTopicsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AllTopicsUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        loadAllTopics()
    }

    fun onEvent(event: AllTopicsUiEvent) {
        when (event) {
            is AllTopicsUiEvent.TopicClicked -> {
                viewModelScope.launch {
                    _eventFlow.emit(event) // emit navigation event
                }
            }

            is AllTopicsUiEvent.RetryLoading -> {
                loadAllTopics()
            }

            is AllTopicsUiEvent.RefreshTopics -> {
                refreshTopics()
            }
        }
    }

    private fun loadAllTopics() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }

                // Fetch all categories from database
                val categories = getAllCategoriesUseCase()
                val categoryIds = categories.map { it.id }

                // Get sunnah counts for all categories (uses existing caching)
                val sunnahCounts = getSunnahCountsUseCase(categoryIds)

                // Combine categories with their counts and images
                val topicsWithCounts = categories.map { category ->
                    TopicWithCount(
                        category = category,
                        sunnahCount = sunnahCounts[category.id] ?: 0,
                        imageRes = categoryImageMap[category.id] ?: R.drawable.ec_warning
                    )
                }

                _uiState.update {
                    it.copy(
                        topics = topicsWithCounts,
                        isLoading = false,
                        error = null
                    )
                }


            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load topics"
                    )
                }
            }
        }
    }

    private fun refreshTopics() {
        // Clear any existing cache and reload
        viewModelScope.launch {
            // This would clear the sunnah count cache if needed
            loadAllTopics()
        }
    }
}