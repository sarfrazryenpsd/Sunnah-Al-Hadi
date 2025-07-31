package com.ryen.sunnah_alhadi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import com.ryen.sunnah_alhadi.data.local.datasource.AppDatabase
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

        //viewModel.seedDatabase()

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "sunnah_database.db"
            )
                .createFromAsset("database/sunnah_database.db")
                .fallbackToDestructiveMigration(false)
                .build()

            val dbFile = context.getDatabasePath("sunnah_database.db")
            Log.d("DB_CHECK", "DB Path: ${dbFile.absolutePath}, Exists: ${dbFile.exists()}")

            DaoTestScreen(appDatabase = db)

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


@Composable
fun DaoTestScreen(appDatabase: AppDatabase) {
    LaunchedEffect(Unit) {
        val dao = appDatabase.categoryDao()
        val featuredCategories = dao.getAllCategories()

        Log.d("DAO_TEST", "Featured categories: ${featuredCategories.joinToString { it.topic }}")
    }
}







