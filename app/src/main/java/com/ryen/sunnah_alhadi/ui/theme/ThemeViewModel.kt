package com.ryen.sunnah_alhadi.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase
) : ViewModel() {

    val uiState: StateFlow<ThemeUiState> = getUserPreferencesFlowUseCase()
        .map { preferences ->
            ThemeUiState(
                themeMode = ThemeMode.entries.getOrElse(preferences.themeMode) { ThemeMode.SYSTEM },
                isDynamicThemeEnabled = preferences.isDynamicThemeEnabled,
                userPreferences = preferences
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeUiState()
        )

    val shouldShowOnboarding: StateFlow<Boolean> = getUserPreferencesFlowUseCase()
        .map { !it.hasCompletedOnboarding }
        .catch { emit(true) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
}

data class ThemeUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicThemeEnabled: Boolean = false,
    val userPreferences: UserPreferences = UserPreferences()
)