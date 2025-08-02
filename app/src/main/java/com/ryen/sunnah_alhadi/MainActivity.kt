package com.ryen.sunnah_alhadi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.ryen.sunnah_alhadi.presentation.navigation.MainNavigation
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: DbSeederViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {


            // Global theme state management
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeUiState by themeViewModel.uiState.collectAsState()

            // Check if onboarding should be shown
            val showOnboarding = themeViewModel.shouldShowOnboarding()

            val windowSizeClass = calculateWindowSizeClass(this)

            SunnahAlHadiTheme(
                windowSizeClass = windowSizeClass,
                themeMode = themeUiState.themeMode,
                isDynamicColorEnabled = themeUiState.isDynamicThemeEnabled
            ) {
                MainNavigation(showOnboarding)
            }
        }
    }
}



