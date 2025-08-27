package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    isTopLevel: Boolean = true,
    onBackClick: () -> Unit = {},
    actionContents: @Composable () -> Unit = {}
) {
    val icon = if (isTopLevel) R.drawable.sunnahlogo else R.drawable.interface_left
    val size = if (isTopLevel) 40.dp else 24.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Sunnah Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(size).clickable { onBackClick() }
        )
        actionContents()
    }
}



@Preview
@Composable
fun CustomTopBarPreview() {
    CustomTopBar()
}



