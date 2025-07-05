package com.ryen.sunnah_alhadi.presentation.common

// Standardized UI State pattern
sealed interface UiState {
    data object Loading : UiState
    data class Error(val message: String) : UiState
    data object Empty : UiState
    data class Success<T>(val data: T) : UiState
}
