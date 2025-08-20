package com.ryen.sunnah_alhadi.presentation.screens.home

import com.ryen.sunnah_alhadi.presentation.screens.topic.TopicUiEvent

sealed class HomeEvent{

    data object ToggleSotd : HomeEvent()
    data object ToggleDisclaimer : HomeEvent()
    data object DismissSotd: HomeEvent()
    data object MarkSotdAsSeen: HomeEvent()
    data class  NavigateToTopic(val categoryId: Int) : HomeEvent()
    data class SunnahCardClicked(val index: Int) : HomeEvent()
    object ClosePager : HomeEvent()
    data class PagerPageChanged(val index: Int) : HomeEvent()
    data object HandleNotificationLaunch : HomeEvent()
    object AutoShowSotdCheck : HomeEvent()

}