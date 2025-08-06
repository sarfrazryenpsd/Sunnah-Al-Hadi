package com.ryen.sunnah_alhadi.presentation.screens.home

sealed class HomeEvent{

    data object ToggleSotd : HomeEvent()
    data object ToggleDisclaimer : HomeEvent()
    data object DismissSotd: HomeEvent()
    data object MarkSotdAsSeen: HomeEvent()
    data object NavigateToAllTopics : HomeEvent()
    data class  NavigateToTopic(val categoryId: Int) : HomeEvent()
    data class OpenSunnah(val sunnahId: String) : HomeEvent()
    data object HandleNotificationLaunch : HomeEvent()
    object AutoShowSotdCheck : HomeEvent()

}