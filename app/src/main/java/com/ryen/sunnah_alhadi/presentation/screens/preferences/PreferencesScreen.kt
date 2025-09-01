@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.screens.preferences

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.R
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
import com.ryen.sunnah_alhadi.presentation.components.PreferenceVerticalItem
import com.ryen.sunnah_alhadi.presentation.components.ThemeSegmentedButton
import com.ryen.sunnah_alhadi.presentation.components.UserNameDialog
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
@PreviewScreenSizes
@PreviewLightDark
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
                username = "Muhammed",
                isLoading = false,
                userPreferences = UserPreferences(username = "Muhammed"),
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background
            )
        ) {
            CustomTopBar()

            Spacer(modifier = Modifier.height(24.dp))

            ScreenHeaderSection(
                screen = Preferences,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {


            // Appearance Section
            item {
                AppearanceSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Notifications Section
            item {
                NotificationsSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // About Section
            item {
                AboutSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
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




@Composable
private fun AppearanceSection(
    uiState: PreferencesUiState,
    onEvent: (PreferencesEvent) -> Unit
) {
    PreferenceSection(title = "Personalization") {
        uiState.userPreferences?.let { preferences ->

            PreferenceHorizontalItem(
                title = preferences.username,
                subtitle = "Your name to display in app",
                leadingIcon = R.drawable.interface_user,
                iconColor = Color(0xFF3F51B5), // Deep Purple
                trailingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ec_note),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp),
                    )
                },
                modifier = Modifier.clickable {
                    onEvent(PreferencesEvent.ShowUserNameDialog)
                }
            )

            // Theme Mode Selection
            PreferenceVerticalItem(
                title = "Appearance",
                subtitle = "Choose your preferred theme mode",
                leadingIcon = R.drawable.interface_theme,
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
                leadingIcon = R.drawable.interface_color,
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
    PreferenceSection(title = "Notification") {
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
                leadingIcon = R.drawable.interface_bell,
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
                    leadingIcon = R.drawable.interface_clock,
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
            leadingIcon = R.drawable.interface_info,
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
            leadingIcon = R.drawable.ec_explanation,
            iconColor = Color(0xFF4CAF50), // Green
            onClick = { onEvent(PreferencesEvent.ShowAboutDialog) },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.interface_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
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
            leadingIcon = R.drawable.interface_policy,
            iconColor = Color(0xFF9C27B0), // Purple
            onClick = { onEvent(PreferencesEvent.ShowPrivacyPolicyDialog) },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.interface_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
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
            leadingIcon = R.drawable.interface_terms,
            iconColor = Color(0xFF009688), // Teal
            onClick = { onEvent(PreferencesEvent.ShowTermsOfServiceDialog) },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.interface_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
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
            leadingIcon = R.drawable.interface_star,
            iconColor = Color(0xFFFFC107), // Amber
            onClick = {
                PreferenceActions.openPlayStoreRating(context)
                onEvent(PreferencesEvent.RateApp)
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.interface_open),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
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
            leadingIcon = R.drawable.interface_bug,
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
                        painter = painterResource(R.drawable.interface_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
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
            leadingIcon = R.drawable.interface_mail,
            iconColor = Color(0xFF3F51B5), // Indigo
            onClick = {
                PreferenceActions.contactDeveloper(context)
                onEvent(PreferencesEvent.ContactDeveloper)
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.interface_open),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
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
            leadingIcon = R.drawable.interface_share,
            iconColor = Color(0xFF00BCD4), // Cyan
            onClick = {
                PreferenceActions.shareApp(context)
                onEvent(PreferencesEvent.ShareApp)
            },
            trailingContent = {
                Icon(
                    painter = painterResource(R.drawable.interface_open),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
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

    //Username change Dialog
    UserNameDialog(
        showDialog = uiState.showUsernameDialog,
        onDismiss = { onEvent(PreferencesEvent.DismissUsernameDialog) },
        username = uiState.username,
        usernameError = uiState.usernameValidation.errorMessage,
        isUserNameValid = uiState.usernameValidation.isValid,
        onUsernameChange = { onEvent(PreferencesEvent.UpdateUsername(it)) },
        onSave = { onEvent(PreferencesEvent.SaveUsername(it)) }
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

