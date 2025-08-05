package com.ryen.sunnah_alhadi.presentation.screens.onboarding

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UserPreferencesUpdate
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import com.ryen.sunnah_alhadi.presentation.util.validateUsername
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import com.ryen.sunnah_alhadi.util.NotificationPermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val sotdNotificationScheduler: SotdNotificationScheduler,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        observeUserPreferences()
        checkNotificationPermission()
    }

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
            OnboardingEvent.RequestNotificationPermission -> requestNotificationPermission()
            OnboardingEvent.DismissPermissionDialog -> dismissPermissionDialog()
            is OnboardingEvent.UpdatePermissionStatus -> updatePermissionStatus(event.hasPermission)
        }
    }

    private fun checkNotificationPermission() {
        val hasPermission = NotificationPermissionUtils.hasNotificationPermission(context)
        _uiState.update { it.copy(hasNotificationPermission = hasPermission) }
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
        val currentState = _uiState.value

        // If enabling notifications but no permission, show permission dialog
        if (enabled && !currentState.hasNotificationPermission) {
            _uiState.update { it.copy(showPermissionDialog = true) }
            return
        }

        _uiState.update { it.copy(isNotificationEnabled = enabled) }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = hasPermission) }
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
                    isSotdNotificationEnabled = currentState.isNotificationEnabled,
                    hasCompletedOnboarding = true
                )

                updateUserPreferencesUseCase(updatedPreferences)

                // Handle notification scheduling
                handleNotificationScheduling(currentState.isNotificationEnabled, currentState.selectedNotificationTime)

                delay(100)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save preferences: ${e.localizedMessage}") }
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                val completedPreferences = UserPreferencesUpdate(
                    username = if (currentState.isUsernameValid) currentState.username else "",
                    themeMode = currentState.selectedTheme,
                    isDynamicThemeEnabled = currentState.isDynamicThemeEnabled,
                    isDailyReminderEnabled = currentState.isNotificationEnabled,
                    sotdNotificationTime = currentState.selectedNotificationTime,
                    isSotdNotificationEnabled = currentState.isNotificationEnabled,
                    hasCompletedOnboarding = true
                )

                updateUserPreferencesUseCase(completedPreferences)

                // Handle notification scheduling
                handleNotificationScheduling(currentState.isNotificationEnabled, currentState.selectedNotificationTime)

                delay(100)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to complete onboarding: ${e.localizedMessage}") }
            }
        }
    }

    private fun saveUsernamePreference(username: String) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(UserPreferencesUpdate(username = username))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save username: ${e.localizedMessage}") }
            }
        }
    }

    private fun saveThemePreferences(themeMode: ThemeMode, isDynamicEnabled: Boolean) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(
                        themeMode = themeMode,
                        isDynamicThemeEnabled = isDynamicEnabled
                    )
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save theme preferences: ${e.localizedMessage}") }
            }
        }
    }

    private fun saveNotificationPreferences(isEnabled: Boolean, time: NotificationTime) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(
                        isDailyReminderEnabled = isEnabled,
                        sotdNotificationTime = time,
                        isSotdNotificationEnabled = isEnabled
                    )
                )

                // Handle notification scheduling immediately after saving
                handleNotificationScheduling(isEnabled, time)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save notification preferences: ${e.localizedMessage}") }
            }
        }
    }

    private suspend fun handleNotificationScheduling(isEnabled: Boolean, time: NotificationTime) {
        try {
            if (isEnabled && _uiState.value.hasNotificationPermission) {
                sotdNotificationScheduler.scheduleNotification(time)
            } else {
                sotdNotificationScheduler.cancelNotification()
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Failed to schedule notifications: ${e.localizedMessage}") }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            _uiState.update { it.copy(showPermissionDialog = true) }
        } else {
            // Pre-Android 13, no permission needed
            _uiState.update { it.copy(hasNotificationPermission = true) }
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesFlowUseCase()
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load preferences: ${exception.localizedMessage}",
                            isLoading = false
                        )
                    }
                }
                .collect { preferences ->
                    _uiState.update {
                        it.copy(
                            username = if (it.isUsernameValid) it.username else preferences.username,
                            selectedTheme = ThemeMode.entries[preferences.themeMode],
                            isDynamicThemeEnabled = preferences.isDynamicThemeEnabled,
                            isNotificationEnabled = preferences.isDailyReminderEnabled,
                            selectedNotificationTime = preferences.sotdNotificationTime,
                            hasNotificationPermission = NotificationPermissionUtils.hasNotificationPermission(context),
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun dismissPermissionDialog() {
        _uiState.update { it.copy(showPermissionDialog = false) }
    }

    fun handlePermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                hasNotificationPermission = granted,
                showPermissionDialog = false
            )
        }

        // If permission granted and notifications are enabled, schedule them
        if (granted && _uiState.value.isNotificationEnabled) {
            viewModelScope.launch {
                handleNotificationScheduling(_uiState.value.isNotificationEnabled, _uiState.value.selectedNotificationTime)
            }
        }
    }
}