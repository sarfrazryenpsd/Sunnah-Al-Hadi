package com.ryen.sunnah_alhadi.presentation.screens.preferences

import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.presentation.util.ValidationResult

data class PreferencesUiState(
    val userPreferences: UserPreferences? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val usernameValidation: ValidationResult = ValidationResult(true),
    val hasNotificationPermission: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val showBugReportDialog: Boolean = false,
    val showPrivacyPolicyDialog: Boolean = false,
    val showTermsOfServiceDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val appVersion: String = "",
    val buildNumber: String = ""
)