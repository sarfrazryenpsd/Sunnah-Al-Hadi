package com.ryen.sunnah_alhadi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            val windowSizeClass = calculateWindowSizeClass(this)

            SunnahAlHadiTheme(
                windowSizeClass = windowSizeClass
            ) {

            }
        }
    }
}







