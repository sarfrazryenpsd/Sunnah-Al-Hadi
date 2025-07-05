package com.ryen.sunnah_alhadi.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Base ViewModel with standardized state management
abstract class BaseViewModel : ViewModel() {

    protected val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected abstract fun getInitialState(): UiState
    protected abstract fun handleEvent(event: UiEvent)

    protected fun setState(state: UiState) {
        _uiState.value = state
    }

    protected fun setLoading() {
        _uiState.value = UiState.Loading
    }

    protected fun setError(message: String) {
        _uiState.value = UiState.Error(message)
    }

    protected fun setEmpty() {
        _uiState.value = UiState.Empty
    }

    protected fun <T> setSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }
}
