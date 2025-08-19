@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun CustomBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Pair("Home", R.drawable.interface_home),
        Pair("Grid", R.drawable.interface_browse),
        Pair("Settings", R.drawable.interface_preferences)
    )
    Box(
        modifier = modifier
            .zIndex(1f)
            .width(240.dp)
            .height(80.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(32),
            shadowElevation = 18.dp
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(80.dp)
                    .width(240.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    val selectedIconColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                    val bgColor =
                        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clickable { onItemSelected(index) } // Provide selection handler
                            .background(bgColor, shape = RoundedCornerShape(32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(item.second),
                            contentDescription = item.first,
                            tint = selectedIconColor,
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CustomBottomBarPreview() {
    var selected by remember { mutableIntStateOf(0) }
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ){ CustomBottomBar(selectedIndex = selected, onItemSelected = { selected = it }) }
}
