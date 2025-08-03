package com.ryen.sunnah_alhadi.presentation.screens.preferences

import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

sealed class PreferencesEvent {
    data class UpdateUsername(val username: String) : PreferencesEvent()
    data class UpdateDynamicTheme(val enabled: Boolean) : PreferencesEvent()
    data class UpdateThemeMode(val themeMode: ThemeMode) : PreferencesEvent()
    data class UpdateSotdNotification(val enabled: Boolean) : PreferencesEvent()
    data class UpdateNotificationTime(val time: NotificationTime) : PreferencesEvent()
    data object RequestNotificationPermission : PreferencesEvent()
    data object DismissPermissionDialog : PreferencesEvent()
    data object ShowBugReportDialog : PreferencesEvent()
    data object DismissBugReportDialog : PreferencesEvent()
    data class SubmitBugReport(val description: String, val email: String) : PreferencesEvent()
    data object ShowPrivacyPolicyDialog : PreferencesEvent()
    data object DismissPrivacyPolicyDialog : PreferencesEvent()
    data object ShowTermsOfServiceDialog : PreferencesEvent()
    data object DismissTermsOfServiceDialog : PreferencesEvent()
    data object ShowAboutDialog : PreferencesEvent()
    data object DismissAboutDialog : PreferencesEvent()
    data object RateApp : PreferencesEvent()
    data object ShareApp : PreferencesEvent()
    data object ContactDeveloper : PreferencesEvent()

    data class UpdatePermissionStatus(val hasPermission: Boolean) : PreferencesEvent()

    data object ClearError: PreferencesEvent()
}