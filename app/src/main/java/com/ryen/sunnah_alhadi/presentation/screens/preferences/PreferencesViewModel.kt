package com.ryen.sunnah_alhadi.presentation.screens.preferences
/*

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UserPreferencesUpdate
import com.ryen.sunnah_alhadi.presentation.util.validateUsername
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import com.ryen.sunnah_alhadi.ui.theme.ThemeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val themeViewModel: ThemeViewModel,
    private val bugReportRepository: BugReportRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    private val notificationPermissionLauncher = MutableStateFlow<ActivityResultLauncher<String>?>(null)

    init {
        observeUserPreferences()
        checkNotificationPermission()
        loadAppInfo()
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
                            userPreferences = preferences,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            _uiState.update { it.copy(hasNotificationPermission = hasPermission) }
        } else {
            // Pre-Android 13 devices don't need notification permission
            _uiState.update { it.copy(hasNotificationPermission = true) }
        }
    }

    private fun loadAppInfo() {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "Unknown"
            val versionCode = PreferenceActions.getAppVersionCode(context).toString()

            _uiState.update {
                it.copy(
                    appVersion = versionName,
                    buildNumber = versionCode
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            _uiState.update {
                it.copy(
                    appVersion = "Unknown",
                    buildNumber = "Unknown"
                )
            }
        }
    }

    fun onEvent(event: PreferencesEvent) {
        when (event) {
            is PreferencesEvent.UpdateUsername -> updateUsername(event.username)
            is PreferencesEvent.UpdateDynamicTheme -> updateDynamicTheme(event.enabled)
            is PreferencesEvent.UpdateThemeMode -> updateThemeMode(event.themeMode)
            is PreferencesEvent.UpdateSotdNotification -> updateSotdNotification(event.enabled)
            is PreferencesEvent.UpdateNotificationTime -> updateNotificationTime(event.time)
            PreferencesEvent.RequestNotificationPermission -> requestNotificationPermission()
            PreferencesEvent.DismissPermissionDialog -> dismissPermissionDialog()
            PreferencesEvent.ShowBugReportDialog -> showBugReportDialog()
            PreferencesEvent.DismissBugReportDialog -> dismissBugReportDialog()
            is PreferencesEvent.SubmitBugReport -> submitBugReport(event.description, event.email)
            PreferencesEvent.ShowPrivacyPolicyDialog -> showPrivacyPolicyDialog()
            PreferencesEvent.DismissPrivacyPolicyDialog -> dismissPrivacyPolicyDialog()
            PreferencesEvent.ShowTermsOfServiceDialog -> showTermsOfServiceDialog()
            PreferencesEvent.DismissTermsOfServiceDialog -> dismissTermsOfServiceDialog()
            PreferencesEvent.ShowAboutDialog -> showAboutDialog()
            PreferencesEvent.DismissAboutDialog -> dismissAboutDialog()
            PreferencesEvent.RateApp -> handleRateApp()
            PreferencesEvent.ShareApp -> handleShareApp()
            PreferencesEvent.ContactDeveloper -> handleContactDeveloper()
        }
    }

    private fun updateUsername(username: String) {
        val validation = validateUsername(username)
        _uiState.update { it.copy(usernameValidation = validation) }

        if (validation.isValid) {
            viewModelScope.launch {
                try {
                    updateUserPreferencesUseCase(
                        UserPreferencesUpdate(username = username.trim())
                    )
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(error = "Failed to update username: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun updateDynamicTheme(enabled: Boolean) {
        // Direct update to ThemeViewModel for immediate UI response
        themeViewModel.updateDynamicTheme(enabled)

        // Also persist to preferences
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(isDynamicThemeEnabled = enabled)
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update dynamic theme: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun updateThemeMode(themeMode: ThemeMode) {
        // Direct update to ThemeViewModel for immediate UI response
        themeViewModel.updateThemeMode(themeMode)

        // Also persist to preferences
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(themeMode = themeMode)
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update theme mode: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun updateSotdNotification(enabled: Boolean) {
        val currentState = _uiState.value

        // If enabling notifications but no permission, show permission dialog
        if (enabled && !currentState.hasNotificationPermission) {
            _uiState.update { it.copy(showPermissionDialog = true) }
            return
        }

        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(isSotdNotificationEnabled = enabled)
                )

                // Schedule or cancel notifications based on the setting
                if (enabled) {
                    scheduleNotifications()
                } else {
                    cancelNotifications()
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update notifications: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun updateNotificationTime(time: NotificationTime) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(sotdNotificationTime = time)
                )

                // Reschedule notifications with new time if notifications are enabled
                _uiState.value.userPreferences?.let { preferences ->
                    if (preferences.isSotdNotificationEnabled) {
                        scheduleNotifications()
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update notification time: ${e.localizedMessage}")
                }
            }
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

        // If permission granted, enable notifications
        if (granted) {
            updateSotdNotification(true)
        }
    }

    private fun scheduleNotifications() {
        // Implementation depends on your notification scheduling system
        // This would typically interact with WorkManager or AlarmManager
        try {
            // NotificationScheduler.scheduleDaily(context, time)
            // Log success
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Failed to schedule notifications: ${e.localizedMessage}")
            }
        }
    }

    private fun cancelNotifications() {
        try {
            // NotificationScheduler.cancelAll(context)
            // Log cancellation
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Failed to cancel notifications: ${e.localizedMessage}")
            }
        }
    }

    private fun showBugReportDialog() {
        _uiState.update { it.copy(showBugReportDialog = true) }
    }

    private fun dismissBugReportDialog() {
        _uiState.update { it.copy(showBugReportDialog = false) }
    }

    private fun submitBugReport(description: String, email: String) {
        if (description.isBlank()) {
            _uiState.update {
                it.copy(error = "Please provide a description of the issue")
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val report = BugReport(
                    description = description.trim(),
                    userEmail = email.trim(),
                    appVersion = _uiState.value.appVersion,
                    deviceInfo = "${Build.MODEL} (Android ${Build.VERSION.RELEASE})",
                    timestamp = System.currentTimeMillis()
                )

                // Save bug report locally first
                bugReportRepository.saveBugReport(report)

                // Log to Firebase Crashlytics with privacy-safe information
                Firebase.crashlytics.log("Bug report submitted")
                Firebase.crashlytics.setCustomKey("report_id", report.id)
                Firebase.crashlytics.setCustomKey("app_version", report.appVersion)
                Firebase.crashlytics.setCustomKey("device_info", report.deviceInfo)
                Firebase.crashlytics.setCustomKey("has_email", email.isNotBlank())

                // Try to sync immediately if online
                try {
                    bugReportRepository.syncPendingReports()
                } catch (syncException) {
                    // Sync failed, but report is saved locally - that's okay
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showBugReportDialog = false,
                        error = null
                    )
                }

                // Show success message (you might want to use SnackBar or Toast)
                showSuccessMessage("جزاك الله خيراً! Bug report submitted successfully.")

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to submit bug report. Your report has been saved and will be sent when connection is available."
                    )
                }
            }
        }
    }

    private fun showPrivacyPolicyDialog() {
        _uiState.update { it.copy(showPrivacyPolicyDialog = true) }
    }

    private fun dismissPrivacyPolicyDialog() {
        _uiState.update { it.copy(showPrivacyPolicyDialog = false) }
    }

    private fun showTermsOfServiceDialog() {
        _uiState.update { it.copy(showTermsOfServiceDialog = true) }
    }

    private fun dismissTermsOfServiceDialog() {
        _uiState.update { it.copy(showTermsOfServiceDialog = false) }
    }

    private fun showAboutDialog() {
        _uiState.update { it.copy(showAboutDialog = true) }
    }

    private fun dismissAboutDialog() {
        _uiState.update { it.copy(showAboutDialog = false) }
    }

    private fun handleRateApp() {
        try {
            PreferenceActions.openPlayStoreRating(context)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Unable to open Play Store. Please try again later.")
            }
        }
    }

    private fun handleShareApp() {
        try {
            PreferenceActions.shareApp(context)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Unable to share app. Please try again later.")
            }
        }
    }

    private fun handleContactDeveloper() {
        try {
            PreferenceActions.contactDeveloper(context)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Unable to open email app. Please try again later.")
            }
        }
    }

    private fun showSuccessMessage(message: String) {
        // This could be implemented as a SnackBar or Toast
        // For now, we'll clear any existing error and rely on UI feedback
        _uiState.update { it.copy(error = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
    }
}*/
