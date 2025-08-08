package com.ryen.sunnah_alhadi.presentation.screens.allTopics

sealed class AllTopicsUiEvent {
    data class TopicClicked(val categoryId: Int) : AllTopicsUiEvent()
    object RetryLoading : AllTopicsUiEvent()
    object RefreshTopics : AllTopicsUiEvent()
}