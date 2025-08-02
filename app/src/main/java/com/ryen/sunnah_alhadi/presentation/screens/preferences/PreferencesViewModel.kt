package com.ryen.sunnah_alhadi.presentation.screens.preferences

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.useCase.GetUserPreferencesFlowUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UpdateUserPreferencesUseCase
import com.ryen.sunnah_alhadi.domain.useCase.UserPreferencesUpdate
import com.ryen.sunnah_alhadi.domain.useCase.bugReport.SubmitBugReportUseCase
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import com.ryen.sunnah_alhadi.presentation.util.validateUsername
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val submitBugReportUseCase: SubmitBugReportUseCase,
    private val sotdNotificationScheduler: SotdNotificationScheduler, // Add this injection
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

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
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toString()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toString()
            }

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
            PreferencesEvent.ClearError -> clearError()
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
                    showSuccessMessage("Username updated successfully")
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(error = "Failed to update username: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    // ✅ CLEAN ARCHITECTURE: Only update preferences, ThemeViewModel handles UI automatically
    private fun updateDynamicTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(isDynamicThemeEnabled = enabled)
                )
                // ✅ ThemeViewModel will automatically observe this change and update UI
                showSuccessMessage("Dynamic theme ${if (enabled) "enabled" else "disabled"}")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to update dynamic theme: ${e.localizedMessage}")
                }
            }
        }
    }

    // ✅ CLEAN ARCHITECTURE: Only update preferences, ThemeViewModel handles UI automatically
    private fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                updateUserPreferencesUseCase(
                    UserPreferencesUpdate(themeMode = themeMode)
                )
                // ✅ ThemeViewModel will automatically observe this change and update UI
                showSuccessMessage("Theme changed to ${themeMode.name.lowercase()}")
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
                    val currentPrefs = getUserPreferencesFlowUseCase().first()
                    sotdNotificationScheduler.scheduleNotification(currentPrefs.sotdNotificationTime)
                } else {
                    sotdNotificationScheduler.cancelNotification()
                }

                showSuccessMessage("Notifications ${if (enabled) "enabled" else "disabled"}")

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
                val currentPrefs = getUserPreferencesFlowUseCase().first()
                if (currentPrefs.isSotdNotificationEnabled) {
                    sotdNotificationScheduler.scheduleNotification(time)
                }

                showSuccessMessage("Notification time updated to ${time.displayName}")

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

    private fun showBugReportDialog() {
        _uiState.update { it.copy(showBugReportDialog = true) }
    }

    private fun dismissBugReportDialog() {
        _uiState.update {
            it.copy(
                showBugReportDialog = false,
                bugReportDescription = "",
                bugReportEmail = ""
            )
        }
    }

    fun updateBugReportField(field: BugReportField, value: String) {
        when (field) {
            BugReportField.DESCRIPTION -> {
                _uiState.update { it.copy(bugReportDescription = value) }
            }
            BugReportField.EMAIL -> {
                _uiState.update { it.copy(bugReportEmail = value) }
            }
        }
    }

    private fun submitBugReport(description: String, email: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isBugReportSubmitting = true) }

                val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"

                val result = submitBugReportUseCase(
                    description = description,
                    userEmail = email,
                    appVersion = _uiState.value.appVersion,
                    deviceInfo = deviceInfo
                )

                result.fold(
                    onSuccess = { reportId ->

                        _uiState.update {
                            it.copy(
                                isBugReportSubmitting = false,
                                showBugReportDialog = false,
                                bugReportDescription = "",
                                bugReportEmail = "",
                                error = null
                            )
                        }

                        showSuccessMessage("جزاك الله خيراً! Bug report submitted successfully.")
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isBugReportSubmitting = false,
                                error = exception.message ?: "Failed to submit bug report. Please try again."
                            )
                        }
                    }
                )

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isBugReportSubmitting = false,
                        error = "An unexpected error occurred. Please try again later."
                    )
                }

                Firebase.crashlytics.recordException(
                    Exception("ViewModel bug report submission failed: ${e.message}", e)
                )
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
        _uiState.update {
            it.copy(
                successMessage = message,
                error = null
            )
        }

        // Clear success message after 3 seconds
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(successMessage = null) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

}

enum class BugReportField {
    DESCRIPTION, EMAIL
}
