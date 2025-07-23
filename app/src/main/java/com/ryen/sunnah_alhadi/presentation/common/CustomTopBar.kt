package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryen.sunnah_alhadi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    isTopLevel: Boolean,
    onNavigateBack: () -> Unit,
    onOrbClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    TopAppBar(
        title = {  },
        navigationIcon = {
            if (isTopLevel) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground ),
                    contentDescription = "App Icon"
                )
            } else {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (isTopLevel) {
                IconButton(onClick = onOrbClick) {
                    Icon(Icons.Default.Favorite, contentDescription = "Action 1")
                }
                IconButton(onClick = onInfoClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Action 2")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}


@Preview
@Composable
fun CustomTopBarPreview() {
    CustomTopBar(
        isTopLevel = true,
        onNavigateBack = {},
        onOrbClick = {},
        onInfoClick = {}
    )
}



