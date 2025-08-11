package com.ryen.sunnah_alhadi.presentation.screens.topic

sealed class TopicUiEvent {
    object RetryLoading : TopicUiEvent()
    data class SunnahCardClicked(val index: Int) : TopicUiEvent()
    object ClosePager : TopicUiEvent()
    data class PagerPageChanged(val index: Int) : TopicUiEvent()
}