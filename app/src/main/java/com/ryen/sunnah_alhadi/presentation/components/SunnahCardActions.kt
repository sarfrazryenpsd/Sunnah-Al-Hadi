package com.ryen.sunnah_alhadi.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.domain.useCase.ExportAction
import com.ryen.sunnah_alhadi.presentation.viewmodel.ExportState
import com.ryen.sunnah_alhadi.presentation.viewmodel.ImageExportViewModel

/**
 * Bookmark, save and share buttons for a sunnah card. Shared by [SunnahPager] and the Sunnah of
 * the Day card so both behave the same: save writes a PNG to the gallery, share opens the system
 * share sheet, and both render the card off-screen in the user's in-app theme.
 */
@Composable
fun SunnahCardActions(
    sunnah: Sunnah,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageExportViewModel: ImageExportViewModel = hiltViewModel()
    val exportState by imageExportViewModel.exportState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // Parent for the off-screen render, so the image uses the user's in-app theme (light/dark/dynamic)
    val compositionContext = rememberCompositionContext()
    val exportCard = { card: Sunnah, action: ExportAction ->
        imageExportViewModel.exportSunnahAsImage(context, compositionContext, card, action)
    }

    // Android 8-9 need WRITE_EXTERNAL_STORAGE at runtime to write to the gallery; 10+ need nothing.
    var pendingSave by remember { mutableStateOf<Sunnah?>(null) }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        pendingSave?.let { card ->
            if (granted) {
                exportCard(card, ExportAction.SAVE)
            } else {
                Toast.makeText(context, "Storage permission is needed to save the card", Toast.LENGTH_LONG).show()
            }
        }
        pendingSave = null
    }
    val saveCard = { card: Sunnah ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pendingSave = card
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            exportCard(card, ExportAction.SAVE)
        }
    }

    // Handle export state changes
    LaunchedEffect(exportState) {
        when (val state = exportState) {
            is ExportState.Success -> {
                if (state.action == ExportAction.SAVE && state.uri != null) {
                    Toast.makeText(context, "Sunnah card saved successfully", Toast.LENGTH_SHORT).show()
                }
                // Share is handled by the system share sheet
                imageExportViewModel.resetState()
            }

            is ExportState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                Log.e("ImageExport", state.message)
                imageExportViewModel.resetState()
            }

            is ExportState.Loading -> {
                val message = when (state.action) {
                    ExportAction.SAVE -> "Saving sunnah card..."
                    ExportAction.SHARE -> "Preparing sunnah card for sharing..."
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            ExportState.Idle -> {}
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardActionButton(
            icon = if (sunnah.isBookmarked) R.drawable.interface_bookmarked else R.drawable.interface_bookmark,
            contentDescription = if (sunnah.isBookmarked) "Remove Bookmark" else "Add Bookmark",
            onClick = { onBookmarkClick(sunnah.id) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CardActionButton(
                icon = R.drawable.interface_download,
                contentDescription = "Save Sunnah Card",
                onClick = { saveCard(sunnah) }
            )
            CardActionButton(
                icon = R.drawable.interface_share,
                contentDescription = "Share Sunnah Card",
                onClick = { exportCard(sunnah, ExportAction.SHARE) }
            )
        }
    }
}

@Composable
private fun CardActionButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}
