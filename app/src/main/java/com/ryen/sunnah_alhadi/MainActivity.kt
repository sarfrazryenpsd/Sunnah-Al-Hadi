package com.ryen.sunnah_alhadi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.ryen.sunnah_alhadi.data.util.DatabaseGenerator
import com.ryen.sunnah_alhadi.data.util.DirectSqliteExporter
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val SHOW_DB_GENERATOR = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            SunnahAlHadiTheme {
                var showDbGenerator by remember { mutableStateOf(SHOW_DB_GENERATOR) }

                if (showDbGenerator) {
                    DatabaseGeneratorScreen(
                        onComplete = { showDbGenerator = false }
                    )
                } else {
                    // Your regular app UI here
                }
            }
        }
    }
}




/**
 * Debug-only helper activity to generate the database
 * Can be excluded from release builds using build variants
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseGeneratorScreen(
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGenerating by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Press the button to generate database") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Database Generator") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    isGenerating = true
                    statusMessage = "Generating database..."

                    scope.launch {
                        try {
                            val dbFile = DirectSqliteExporter(context).generateDatabase()

                            statusMessage = "Database generated successfully!\n\n" +
                                    "Location: ${dbFile.absolutePath}\\n\\nSize: ${dbFile.length()} bytes" +
                                    "Check logs for more details.\n" +
                                    "Copy this file to:\napp/src/main/assets/database/"

                            // Copy path to clipboard for convenience
                            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Database Path", dbFile.absolutePath)
                            clipboardManager.setPrimaryClip(clip)

                        } catch (e: Exception) {
                            Log.e("MainActivity", "Database export failed", e)
                            statusMessage = "Error generating database: ${e.message}"
                        } finally {
                            isGenerating = false
                        }
                    }
                },
                enabled = !isGenerating
            ) {
                Text("Generate Database")
            }

            if (isGenerating) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onComplete,
                enabled = !isGenerating
            ) {
                Text("Continue to App")
            }
        }
    }
}
