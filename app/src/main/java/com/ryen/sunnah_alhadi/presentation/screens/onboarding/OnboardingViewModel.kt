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
            is OnboardingEvent.SelectTheme -> _uiState.update { it.copy(selectedTheme = event.theme) } // inlined small setter
            is OnboardingEvent.ToggleDynamicTheme -> _uiState.update { it.copy(isDynamicThemeEnabled = event.enabled) }
            is OnboardingEvent.ToggleNotification -> updateNotification(event.enabled)
            is OnboardingEvent.SelectNotificationTime -> _uiState.update { it.copy(selectedNotificationTime = event.time) }
            OnboardingEvent.NextStep -> nextStep()
            OnboardingEvent.PreviousStep -> previousStep()
            OnboardingEvent.DismissOnboarding -> saveOnboardingProgress(keepExistingUsername = true, errorPrefix = "Failed to save preferences")
            OnboardingEvent.CompleteOnboarding -> saveOnboardingProgress(keepExistingUsername = false, errorPrefix = "Failed to complete onboarding")
            OnboardingEvent.RequestNotificationPermission -> requestNotificationPermission()
            OnboardingEvent.DismissPermissionDialog -> _uiState.update { it.copy(showPermissionDialog = false) }
            is OnboardingEvent.UpdatePermissionStatus -> _uiState.update { it.copy(hasNotificationPermission = event.hasPermission) }
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

    private fun updateNotification(enabled: Boolean) {
        val currentState = _uiState.value
        if (enabled && !currentState.hasNotificationPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                _uiState.update { it.copy(showPermissionDialog = true) }
            } else {
                _uiState.update { it.copy(hasNotificationPermission = true, isNotificationEnabled = true) }
            }
        } else {
            _uiState.update { it.copy(isNotificationEnabled = enabled) }
        }
    }

    private fun nextStep() {
        val currentState = _uiState.value
        // save before proceeding
        when (currentState.currentStep) {
            OnboardingStep.USERNAME -> if (currentState.isUsernameValid) {
                saveUsernamePreference(currentState.username)
                _uiState.update { it.copy(currentStep = OnboardingStep.THEME) }
            }
            OnboardingStep.THEME -> {
                saveThemePreferences(currentState.selectedTheme, currentState.isDynamicThemeEnabled)
                _uiState.update { it.copy(currentStep = OnboardingStep.NOTIFICATION) }
            }
            OnboardingStep.NOTIFICATION -> {
                saveNotificationPreferences(currentState.isNotificationEnabled, currentState.selectedNotificationTime)
                _uiState.update { it.copy(currentStep = OnboardingStep.WELCOME) }
            }
            OnboardingStep.WELCOME -> saveOnboardingProgress(keepExistingUsername = false, errorPrefix = "Failed to complete onboarding")
        }
    }

    private fun previousStep() {
        val previousStep = when (_uiState.value.currentStep) {
            OnboardingStep.THEME -> OnboardingStep.USERNAME
            OnboardingStep.NOTIFICATION -> OnboardingStep.THEME
            OnboardingStep.WELCOME -> OnboardingStep.NOTIFICATION
            OnboardingStep.USERNAME -> OnboardingStep.USERNAME
        }
        _uiState.update { it.copy(currentStep = previousStep) }
    }

    /**
     * Unified function for both dismissOnboarding() and completeOnboarding()
     * Removed duplication by passing keepExistingUsername + errorPrefix.
     */
    private fun saveOnboardingProgress(
        keepExistingUsername: Boolean,
        errorPrefix: String
    ) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val preferences = getUserPreferencesUseCase()

                val updatedPreferences = UserPreferencesUpdate(
                    username = when {
                        currentState.isUsernameValid -> currentState.username
                        keepExistingUsername -> preferences.username
                        else -> ""
                    },
                    themeMode = currentState.selectedTheme,
                    isDynamicThemeEnabled = currentState.isDynamicThemeEnabled,
                    sotdNotificationTime = currentState.selectedNotificationTime,
                    isSotdNotificationEnabled = currentState.isNotificationEnabled,
                    hasCompletedOnboarding = true
                )

                updateUserPreferencesUseCase(updatedPreferences)
                handleNotificationScheduling(currentState.isNotificationEnabled, currentState.selectedNotificationTime)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "$errorPrefix: ${e.localizedMessage}") }
            }
        }
    }

    /**
     * All "saveXPreference" methods: switched to ioDispatcher for consistency
     * (I/O work shouldn't run on Main).
     */
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
                updateUserPreferencesUseCase(UserPreferencesUpdate(themeMode = themeMode, isDynamicThemeEnabled = isDynamicEnabled))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save theme preferences: ${e.localizedMessage}") }
            }
        }
    }

    private fun saveNotificationPreferences(isEnabled: Boolean, time: NotificationTime) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(UserPreferencesUpdate(sotdNotificationTime = time, isSotdNotificationEnabled = isEnabled))
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
            _uiState.update { it.copy(hasNotificationPermission = true) }
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch { // switched to ioDispatcher (DataStore/DB read)
            getUserPreferencesFlowUseCase()
                .catch { exception ->
                    _uiState.update { it.copy(error = "Failed to load preferences: ${exception.localizedMessage}", isLoading = false) }
                }
                .collect { preferences ->
                    _uiState.update {
                        it.copy(
                            username = if (it.isUsernameValid) it.username else preferences.username,
                            selectedTheme = ThemeMode.entries[preferences.themeMode],
                            isDynamicThemeEnabled = preferences.isDynamicThemeEnabled,
                            isNotificationEnabled = preferences.isSotdNotificationEnabled,
                            selectedNotificationTime = preferences.sotdNotificationTime,
                            hasNotificationPermission = NotificationPermissionUtils.hasNotificationPermission(context),
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun handlePermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasNotificationPermission = granted, showPermissionDialog = false) }

        if (granted) {
            _uiState.update { it.copy(isNotificationEnabled = true) }
            viewModelScope.launch {
                handleNotificationScheduling(true, _uiState.value.selectedNotificationTime)
            }
        } else {
            _uiState.update { it.copy(isNotificationEnabled = false) }
        }
    }
}
