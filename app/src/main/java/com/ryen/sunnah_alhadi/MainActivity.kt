package com.ryen.sunnah_alhadi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryen.sunnah_alhadi.presentation.navigation.MainNavigation
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //private val viewModel: DbSeederViewModel by viewModels()

    // Set when opened from the Sunnah of the Day notification; cleared once the card is shown
    private var openSotdFromNotification by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Only on a fresh launch: after rotation or other recreation the overlay restores itself
        if (savedInstanceState == null) {
            openSotdFromNotification = intent?.getBooleanExtra(EXTRA_SHOW_SOTD, false) == true
        }


        enableEdgeToEdge()
        setContent {


            // Global theme state management
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeUiState by themeViewModel.uiState.collectAsStateWithLifecycle()


            // Check if onboarding should be shown

            val sotdId = intent?.getStringExtra("sotd_id")

            val windowSizeClass = calculateWindowSizeClass(this)

            SunnahAlHadiTheme(
                windowSizeClass = windowSizeClass,
                themeMode = themeUiState.themeMode,
                isDynamicColorEnabled = themeUiState.isDynamicThemeEnabled
            ) {
                val isDarkBackground = MaterialTheme.colorScheme.background.luminance() < 0.5

                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.Transparent.toArgb(), // For light backgrounds
                        darkScrim = Color.Black.copy(alpha = 0.2f).toArgb(), // Adjust scrim if needed
                        detectDarkMode = { isDarkBackground }
                    )
                )
                MainNavigation(
                    showOnboarding = !themeUiState.userPreferences.hasCompletedOnboarding,
                    isFromNotification = openSotdFromNotification,
                    onNotificationHandled = { openSotdFromNotification = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent

        // Notification tapped while the app is running: open the card without recreating the activity
        if (intent.getBooleanExtra(EXTRA_SHOW_SOTD, false)) {
            openSotdFromNotification = true
        }
    }

    companion object {
        /** Set by SotdNotificationHelper on the notification's tap intent. */
        const val EXTRA_SHOW_SOTD = "show_sotd"
    }
}



