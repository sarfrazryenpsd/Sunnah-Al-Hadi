package com.ryen.sunnah_alhadi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.ryen.sunnah_alhadi.data.util.DatabaseGenerator
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunnahAlHadiTheme {
                fun createDatabaseFromJson() {
                    lifecycleScope.launch {
                        val generator = DatabaseGenerator(applicationContext)
                        generator.generateDatabaseFromJson()
                        Log.d("Database", "Database created successfully")
                    }
                }
            }
        }
    }
}
