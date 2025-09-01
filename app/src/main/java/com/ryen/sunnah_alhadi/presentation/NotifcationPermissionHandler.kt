package com.ryen.sunnah_alhadi.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.util.NotificationPermissionUtils

@Composable
fun NotificationPermissionHandler(
    showPermissionDialog: Boolean,
    hasNotificationPermission: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onDismissDialog: () -> Unit,
) {
    if (hasNotificationPermission) {
        // Optionally notify the caller immediately
        onPermissionResult(true)
        return
    }
    val context = LocalContext.current
    var permissionAttempts by rememberSaveable {
        mutableIntStateOf(0)
    }
    var showRationale by rememberSaveable  {
        mutableStateOf(false)
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Reset attempts on successful grant
            permissionAttempts = 0
            onPermissionResult(true)
        } else {
            permissionAttempts++

            // Check if we should show rationale (user can still grant permission)
            val shouldShowRationale = NotificationPermissionUtils
                .shouldShowPermissionRationale(context as Activity)

            if (!shouldShowRationale && permissionAttempts >= 2) {
                // User has denied twice and "Don't ask again" is likely checked
                showRationale = true
            } else {
                onPermissionResult(false)
            }
        }
    }

    // Handle permission requests
    LaunchedEffect(showPermissionDialog) {
        if (showPermissionDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (permissionAttempts < 2) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                onDismissDialog()
            } else {
                // Show rationale immediately if attempts >= 2
                showRationale = true
                onDismissDialog()
            }
        }
    }

    // Permission rationale dialog
    if (showRationale) {
        PermissionRationaleDialog(
            onOpenSettings = {
                showRationale = false
                onDismissDialog()
                openAppSettings(context)
            },
            onDismiss = {
                showRationale = false
                onDismissDialog()
                onPermissionResult(false)
            }
        )
    }
}

@Composable
private fun PermissionRationaleDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.interface_notification),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Notification Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "To receive your daily Sunnah reminders, please enable notifications in your device settings.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.interface_system),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Path to enable:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Settings → Apps → Sunnah Al-Hadi → Notifications",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onOpenSettings
            ) {
                Icon(
                    painter = painterResource(R.drawable.interface_system),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip for now")
            }
        }
    )
}

// Helper function to open app settings
private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general settings
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            Log.e("PermissionHandler", "Failed to open settings", ex)
        }
    }
}