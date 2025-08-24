package com.ryen.sunnah_alhadi.presentation.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PagerVisibilityState {
    private val _isPagerVisible = MutableStateFlow(false)
    val isPagerVisible: StateFlow<Boolean> = _isPagerVisible.asStateFlow()

    fun setPagerVisibility(isVisible: Boolean) {
        _isPagerVisible.value = isVisible
    }
}