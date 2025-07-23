package com.ryen.sunnah_alhadi.presentation.screens.home

sealed class HomeEvent{

    data object ToggleSotd : HomeEvent()

    data object ToggleDisclaimer : HomeEvent()

    data object NavigateToAllTopics : HomeEvent()

    data class  NavigateToTopic(val categoryId: Int) : HomeEvent()

    data class OpenSunnah(val sunnahId: String) : HomeEvent()

}