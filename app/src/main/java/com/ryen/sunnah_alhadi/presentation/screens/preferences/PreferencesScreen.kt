@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.preferences

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.presentation.NotificationPermissionHandler
import com.ryen.sunnah_alhadi.presentation.common.CustomTopBar
import com.ryen.sunnah_alhadi.presentation.common.ScreenHeaderSection
import com.ryen.sunnah_alhadi.presentation.components.BugReportDialog
import com.ryen.sunnah_alhadi.presentation.components.ContentDisplayDialog
import com.ryen.sunnah_alhadi.presentation.components.NotificationPermissionDialog
import com.ryen.sunnah_alhadi.presentation.components.NotificationTimeDropdown
import com.ryen.sunnah_alhadi.presentation.components.PreferenceHorizontalItem
import com.ryen.sunnah_alhadi.presentation.components.PreferenceSection
import com.ryen.sunnah_alhadi.presentation.components.PreferenceSwitch
import com.ryen.sunnah_alhadi.presentation.components.PreferenceTextField
import com.ryen.sunnah_alhadi.presentation.components.PreferenceVerticalItem
import com.ryen.sunnah_alhadi.presentation.components.ThemeSegmentedButton
import com.ryen.sunnah_alhadi.presentation.navigation.Home
import com.ryen.sunnah_alhadi.presentation.navigation.Preferences
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import kotlinx.coroutines.delay

@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    NotificationPermissionHandler(
        showPermissionDialog = uiState.showPermissionDialog,
        hasNotificationPermission = uiState.hasNotificationPermission,
        onPermissionResult = { granted ->
            viewModel.handlePermissionResult(granted)
        },
        onDismissDialog = {
            viewModel.onEvent(PreferencesEvent.DismissPermissionDialog)
        }
    )

    // Show success messages
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Auto-dismiss success message after 3 seconds
            delay(3000)
            viewModel.clearSuccessMessage()
        }
    }


    // Handle error display
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar or handle error display
            viewModel.onEvent(PreferencesEvent.ClearError)
        }
    }

    PreferencesScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        context = context
    )

    // Dialogs
    PreferencesDialogs(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Preview
@Composable
fun PreferencesScreenContentPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(
                width = 360.dp,
                height = 640.dp
            )
        )
    ) {
        PreferencesScreenContent(
            uiState = PreferencesUiState(
                isLoading = false,
                userPreferences = UserPreferences(),
                appVersion = "1.0.0",
                buildNumber = "1",
                hasNotificationPermission = true
            ),
            onEvent = {},
            context = LocalContext.current
        )
    }
}


@Composable
fun PreferencesScreenContent(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit,
    context: Context
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            item {
                CustomTopBar()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            // Greeting Section
            item {
                ScreenHeaderSection(
                    screen = Preferences,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
            // Account Section
            item {
                AccountSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            // Appearance Section
            item {
                AppearanceSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            // Notifications Section
            item {
                NotificationsSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            // About Section
            item {
                AboutSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            // Support & Feedback Section
            item {
                SupportSection(
                    uiState = uiState,
                    onEvent = onEvent,
                    context = context
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferencesTopBar(
    successMessage: String?
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall
                )
                // Show success message in subtitle
                successMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun AccountSection(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit
) {
    PreferenceSection(title = "Account") {
        uiState.userPreferences?.let { preferences ->
            PreferenceTextField(
                title = "Username",
                value = preferences.username,
                onValueChange = { onEvent(PreferencesEvent.UpdateUsername(it)) },
                leadingIcon = Icons.Default.Person,
                iconColor = MaterialTheme.colorScheme.primary,
                validation = uiState.usernameValidation,
                characterCount = getUsernameCharacterCount(preferences.username),
                placeholder = "Enter your name"
            )
        }
    }
}

@Composable
private fun AppearanceSection(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit
) {
    PreferenceSection(title = "Appearance") {
        uiState.userPreferences?.let { preferences ->
            // Theme Mode Selection
            PreferenceVerticalItem(
                title = "Theme",
                subtitle = "Choose your preferred theme",
                leadingIcon = Icons.Default.Palette,
                iconColor = Color(0xFF9C27B0), // Purple
                trailingContent = {
                    ThemeSegmentedButton(
                        selectedTheme = ThemeMode.entries[preferences.themeMode],
                        onThemeSelected = { onEvent(PreferencesEvent.UpdateThemeMode(it)) }
                    )
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Dynamic Colors Toggle
            PreferenceSwitch(
                title = "Dynamic Colors",
                subtitle = "Use colors from your wallpaper",
                checked = preferences.isDynamicThemeEnabled,
                onCheckedChange = { onEvent(PreferencesEvent.UpdateDynamicTheme(it)) },
                leadingIcon = Icons.Default.ColorLens,
                iconColor = Color(0xFF4CAF50), // Green
                enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            )
        }
    }
}

@Composable
private fun NotificationsSection(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit
) {
    PreferenceSection(title = "Notifications") {
        uiState.userPreferences?.let { preferences ->
            // Sunnah of the Day Notifications
            PreferenceSwitch(
                title = "Daily Sunnah Reminders",
                subtitle = "Get reminded about beautiful Sunnahs",
                checked = preferences.isSotdNotificationEnabled,
                onCheckedChange = {
                    if (it && !uiState.hasNotificationPermission) {
                        onEvent(PreferencesEvent.RequestNotificationPermission)
                    } else {
                        onEvent(PreferencesEvent.UpdateSotdNotification(it))
                    }
                },
                leadingIcon = Icons.Default.NotificationsActive,
                iconColor = Color(0xFFFF9800) // Orange
            )

            if (preferences.isSotdNotificationEnabled) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Notification Time Selection
                PreferenceVerticalItem(
                    title = "Reminder Time",
                    subtitle = "When to receive daily reminders",
                    leadingIcon = Icons.Default.AccessTime,
                    iconColor = Color(0xFFFFC107), // Amber
                    trailingContent = {
                        NotificationTimeDropdown(
                            selectedTime = preferences.sotdNotificationTime,
                            onTimeSelected = {
                                onEvent(
                                    PreferencesEvent.UpdateNotificationTime(
                                        it
                                    )
                                )
                            },
                            enabled = preferences.isSotdNotificationEnabled
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AboutSection(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit
) {
    PreferenceSection(title = "About") {
        // App Version (Non-clickable)
        PreferenceHorizontalItem(
            title = "App Version",
            subtitle = "Version ${uiState.appVersion} (Build ${uiState.buildNumber})",
            leadingIcon = Icons.Default.Info,
            iconColor = Color(0xFF2196F3), // Blue
            trailingContent = { }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // About Sunnah Al-Hadi
        PreferenceHorizontalItem(
            title = "About Sunnah Al-Hadi",
            subtitle = "Learn more about this app",
            leadingIcon = Icons.AutoMirrored.Default.MenuBook,
            iconColor = Color(0xFF4CAF50), // Green
            onClick = { onEvent(PreferencesEvent.ShowAboutDialog) },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Privacy Policy
        PreferenceHorizontalItem(
            title = "Privacy Policy",
            subtitle = "How we protect your privacy",
            leadingIcon = Icons.Default.Policy,
            iconColor = Color(0xFF9C27B0), // Purple
            onClick = { onEvent(PreferencesEvent.ShowPrivacyPolicyDialog) },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Terms of Service
        PreferenceHorizontalItem(
            title = "Terms of Service",
            subtitle = "App usage terms and conditions",
            leadingIcon = Icons.Default.Description,
            iconColor = Color(0xFF009688), // Teal
            onClick = { onEvent(PreferencesEvent.ShowTermsOfServiceDialog) },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
private fun SupportSection(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit,
    context: Context
) {
    PreferenceSection(title = "Support & Feedback") {
        // Rate App
        PreferenceHorizontalItem(
            title = "Rate App",
            subtitle = "Help us improve by rating on Play Store",
            leadingIcon = Icons.Default.Star,
            iconColor = Color(0xFFFFC107), // Amber
            onClick = {
                PreferenceActions.openPlayStoreRating(context)
                onEvent(PreferencesEvent.RateApp)
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Report Bug
        PreferenceHorizontalItem(
            title = "Report Bug",
            subtitle = "Help us fix issues you encounter",
            leadingIcon = Icons.Default.BugReport,
            iconColor = Color(0xFFF44336), // Red
            onClick = { onEvent(PreferencesEvent.ShowBugReportDialog) },
            trailingContent = {
                if (uiState.isBugReportSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Contact Developer
        PreferenceHorizontalItem(
            title = "Contact Developer",
            subtitle = "Send feedback or ask questions",
            leadingIcon = Icons.Default.Email,
            iconColor = Color(0xFF3F51B5), // Indigo
            onClick = {
                PreferenceActions.contactDeveloper(context)
                onEvent(PreferencesEvent.ContactDeveloper)
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Share App
        PreferenceHorizontalItem(
            title = "Share App",
            subtitle = "Invite others to this blessed app",
            leadingIcon = Icons.Default.Share,
            iconColor = Color(0xFF00BCD4), // Cyan
            onClick = {
                PreferenceActions.shareApp(context)
                onEvent(PreferencesEvent.ShareApp)
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
private fun PreferencesDialogs(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit
) {
    // Permission Dialog
    NotificationPermissionDialog(
        showDialog = uiState.showPermissionDialog,
        onDismiss = { onEvent(PreferencesEvent.DismissPermissionDialog) },
        onConfirm = { onEvent(PreferencesEvent.RequestNotificationPermission) }
    )

    // Bug Report Dialog
    BugReportDialog(
        showDialog = uiState.showBugReportDialog,
        onDismiss = { onEvent(PreferencesEvent.DismissBugReportDialog) },
        onSubmit = { description, email ->
            onEvent(PreferencesEvent.SubmitBugReport(description, email))
        },
        isLoading = uiState.isBugReportSubmitting
    )

    // About Dialog
    ContentDisplayDialog(
        title = "About Sunnah Al-Hadi",
        content = PreferenceContent.aboutContent,
        showDialog = uiState.showAboutDialog,
        onDismiss = { onEvent(PreferencesEvent.DismissAboutDialog) }
    )

    // Privacy Policy Dialog
    ContentDisplayDialog(
        title = "Privacy Policy",
        content = PreferenceContent.privacyPolicyContent,
        showDialog = uiState.showPrivacyPolicyDialog,
        onDismiss = { onEvent(PreferencesEvent.DismissPrivacyPolicyDialog) }
    )

    // Terms of Service Dialog
    ContentDisplayDialog(
        title = "Terms of Service",
        content = PreferenceContent.termsOfServiceContent,
        showDialog = uiState.showTermsOfServiceDialog,
        onDismiss = { onEvent(PreferencesEvent.DismissTermsOfServiceDialog) }
    )
}

// Helper function for username character count (reuse from validation)
private fun getUsernameCharacterCount(username: String): String {
    return "${username.length}/50"
}


/*-----------------------------------------------------PREVIEWS----------------------------------------------------*/

@Preview
@Composable
private fun NotificationSectionPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(
                width = 400.dp,
                height = 900.dp
            )
        )
    ) {
        NotificationsSection(
            uiState = PreferencesUiState(
                isLoading = false,
                userPreferences = UserPreferences(),
                appVersion = "1.0.0",
                buildNumber = "1",
                hasNotificationPermission = false,
                showPermissionDialog = false,
                showBugReportDialog = false,
                showAboutDialog = false,
                showPrivacyPolicyDialog = false,
                showTermsOfServiceDialog = false,
                isBugReportSubmitting = false
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun AppearenceSectionPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(
                width = 400.dp,
                height = 900.dp
            )
        )
    ) {

        AppearanceSection(
            uiState = PreferencesUiState(
                isLoading = false,
                userPreferences = UserPreferences(),
                appVersion = "1.0.0",
                buildNumber = "1",
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun AccountSectionPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(
            DpSize(
                width = 400.dp,
                height = 900.dp
            )
        )
    ) {

        AccountSection(
            uiState = PreferencesUiState(
                isLoading = false,
                userPreferences = UserPreferences(),
                appVersion = "1.0.0",
                buildNumber = "1",
            ),
            onEvent = {}
        )
    }
}