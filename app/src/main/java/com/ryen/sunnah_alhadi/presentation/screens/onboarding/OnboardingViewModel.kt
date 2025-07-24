package com.ryen.sunnah_alhadi.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UserPreferencesUpdate
import com.ryen.sunnah_alhadi.presentation.util.validateUsername
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.UpdateUsername -> updateUsername(event.username)
            is OnboardingEvent.SelectTheme -> updateTheme(event.theme)
            is OnboardingEvent.ToggleDynamicTheme -> updateDynamicTheme(event.enabled)
            is OnboardingEvent.ToggleNotification -> updateNotification(event.enabled)
            is OnboardingEvent.SelectNotificationTime -> updateNotificationTime(event.time)
            OnboardingEvent.NextStep -> nextStep()
            OnboardingEvent.PreviousStep -> previousStep()
            OnboardingEvent.DismissOnboarding -> dismissOnboarding()
            OnboardingEvent.CompleteOnboarding -> completeOnboarding()
        }
    }

    private fun updateUsername(username: String) {
        val validation = validateUsername(username)
        _uiState.update { currentState ->
            currentState.copy(
                username = username,
                usernameError = validation.errorMessage,
                isUsernameValid = validation.isValid
            )
        }
    }

    private fun updateTheme(theme: ThemeMode) {
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    private fun updateDynamicTheme(enabled: Boolean) {
        _uiState.update { it.copy(isDynamicThemeEnabled = enabled) }
    }

    private fun updateNotification(enabled: Boolean) {
        _uiState.update { it.copy(isNotificationEnabled = enabled) }
    }

    private fun updateNotificationTime(time: NotificationTime) {
        _uiState.update { it.copy(selectedNotificationTime = time) }
    }

    private fun nextStep() {
        val currentState = _uiState.value

        // Save current step's data before proceeding
        when (currentState.currentStep) {
            OnboardingStep.USERNAME -> {
                if (currentState.isUsernameValid) {
                    saveUsernamePreference(currentState.username)
                    _uiState.update { it.copy(currentStep = OnboardingStep.THEME) }
                }
            }
            OnboardingStep.THEME -> {
                saveThemePreferences(currentState.selectedTheme, currentState.isDynamicThemeEnabled)
                _uiState.update { it.copy(currentStep = OnboardingStep.NOTIFICATION) }
            }
            OnboardingStep.NOTIFICATION -> {
                saveNotificationPreferences(currentState.isNotificationEnabled, currentState.selectedNotificationTime)
                _uiState.update { it.copy(currentStep = OnboardingStep.WELCOME) }
            }
            OnboardingStep.WELCOME -> {
                completeOnboarding()
            }
        }
    }

    private fun previousStep() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            OnboardingStep.THEME -> OnboardingStep.USERNAME
            OnboardingStep.NOTIFICATION -> OnboardingStep.THEME
            OnboardingStep.WELCOME -> OnboardingStep.NOTIFICATION
            OnboardingStep.USERNAME -> OnboardingStep.USERNAME // Stay on first step
        }
        _uiState.update { it.copy(currentStep = previousStep) }
    }

    private fun dismissOnboarding() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // Save current progress with default values for incomplete steps
                val preferences = getUserPreferencesUseCase()
                val updatedPreferences = UserPreferencesUpdate(
                    username = if (currentState.isUsernameValid) currentState.username else preferences.username,
                    themeMode = currentState.selectedTheme,
                    isDynamicThemeEnabled = currentState.isDynamicThemeEnabled,
                    isDailyReminderEnabled = currentState.isNotificationEnabled,
                    sotdNotificationTime = currentState.selectedNotificationTime,
                    hasCompletedOnboarding = true
                )

                updateUserPreferencesUseCase(updatedPreferences)
            } catch (e: Exception) {
                // Handle error - could show snackbar or log
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val preferences = getUserPreferencesUseCase()

                val completedPreferences = UserPreferencesUpdate(
                    username = if (currentState.isUsernameValid) currentState.username else preferences.username,
                    themeMode = currentState.selectedTheme,
                    isDynamicThemeEnabled = currentState.isDynamicThemeEnabled,
                    isDailyReminderEnabled = currentState.isNotificationEnabled,
                    sotdNotificationTime = currentState.selectedNotificationTime,
                    hasCompletedOnboarding = true
                )

                updateUserPreferencesUseCase(completedPreferences)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun saveUsernamePreference(username: String) {
        viewModelScope.launch {
            try {
                val preferences = getUserPreferencesUseCase()
                updateUserPreferencesUseCase(UserPreferencesUpdate(username = username))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun saveThemePreferences(themeMode: ThemeMode, isDynamicEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val preferences = getUserPreferencesUseCase()
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(
                        themeMode = themeMode,
                        isDynamicThemeEnabled = isDynamicEnabled
                    )
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun saveNotificationPreferences(isEnabled: Boolean, time: NotificationTime) {
        viewModelScope.launch {
            try {
                val preferences = getUserPreferencesUseCase()
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(
                        isDailyReminderEnabled = isEnabled,
                        sotdNotificationTime = time,
                        isSotdNotificationEnabled = isEnabled
                    )
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun canProceedToNextStep(): Boolean {
        return when (_uiState.value.currentStep) {
            OnboardingStep.USERNAME -> _uiState.value.isUsernameValid
            else -> true
        }
    }

    fun canGoToPreviousStep(): Boolean {
        return _uiState.value.currentStep != OnboardingStep.USERNAME
    }
}