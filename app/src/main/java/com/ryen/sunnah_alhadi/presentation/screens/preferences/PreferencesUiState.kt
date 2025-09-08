package com.ryen.sunnah_alhadi.presentation.screens.preferences

import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.presentation.util.ValidationResult

data class PreferencesUiState(
    val userPreferences: UserPreferences? = null,
    val username: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null,
    val hasNotificationPermission: Boolean = false,
    val showUsernameDialog: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val showBugReportDialog: Boolean = false,
    val isBugReportSubmitting: Boolean = false,
    val bugReportDescription: String = "",
    val bugReportEmail: String = "",
    val showPrivacyPolicyDialog: Boolean = false,
    val showTermsOfServiceDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val appVersion: String = "",
    val usernameValidation: ValidationResult = ValidationResult(true, null)
)