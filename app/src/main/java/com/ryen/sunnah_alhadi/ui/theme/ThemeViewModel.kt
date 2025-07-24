package com.ryen.sunnah_alhadi.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    init {
        observeUserPreferences()
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesFlowUseCase().collect { preferences ->
                _uiState.update { currentState ->
                    currentState.copy(
                        themeMode = ThemeMode.entries.find { it.ordinal == preferences.themeMode }
                            ?: ThemeMode.LIGHT,
                        isDynamicThemeEnabled = preferences.isDynamicThemeEnabled,
                        userPreferences = preferences
                    )
                }
            }
        }
    }

    fun shouldShowOnboarding(): Boolean {
        return _uiState.value.userPreferences?.hasCompletedOnboarding == false
    }
}

data class ThemeUiState(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val isDynamicThemeEnabled: Boolean = true,
    val userPreferences: UserPreferences? = null
)