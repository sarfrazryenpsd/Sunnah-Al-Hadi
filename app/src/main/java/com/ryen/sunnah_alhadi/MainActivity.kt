package com.ryen.sunnah_alhadi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryen.sunnah_alhadi.presentation.common.LoadingIndicator
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


        enableEdgeToEdge()
        setContent {


            // Global theme state management
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeUiState by themeViewModel.uiState.collectAsState()

            // Check if onboarding should be shown

            val sotdId = intent?.getStringExtra("sotd_id")

            val windowSizeClass = calculateWindowSizeClass(this)

            SunnahAlHadiTheme(
                windowSizeClass = windowSizeClass,
                themeMode = themeUiState.themeMode,
                isDynamicColorEnabled = themeUiState.isDynamicThemeEnabled
            ) {
                val showOnboarding by themeViewModel.shouldShowOnboardingFlow().collectAsState(initial = null)

                val isFromNotification = intent?.getBooleanExtra("show_sotd", false) ?: false


                showOnboarding?.let { shouldShowOnboarding ->
                    MainNavigation(
                        showOnboarding = shouldShowOnboarding,
                        isFromNotification = isFromNotification
                    )
                } ?: run {
                    // ✅ Show loading while determining onboarding state
                    LoadingIndicator()
                }
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



