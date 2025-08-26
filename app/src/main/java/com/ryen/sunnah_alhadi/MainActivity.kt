package com.ryen.sunnah_alhadi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(), // For light backgrounds
                darkScrim = Color.Black.copy(alpha = 0.2f).toArgb(), // Adjust scrim if needed
                detectDarkMode = { /* Use true for dark mode detection */ false }
            )
        )
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

                val isFromNotification = intent?.getBooleanExtra("show_sotd", false) ?: false


                MainNavigation(
                    showOnboarding = !themeUiState.userPreferences.hasCompletedOnboarding,
                    isFromNotification = isFromNotification
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent

        // ✅ For notification clicks while app is running, recreate with new intent
        if (intent.getBooleanExtra("show_sotd", false)) {
            recreate() // Simple approach to handle new intent
        }
    }
}



