package com.ryen.sunnah_alhadi.presentation.navigation
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data object AllTopic : NavKey

@Serializable
data object Browse : NavKey

@Serializable
data object Preferences : NavKey

@Serializable
data class Topic(val categoryId: String) : NavKey






