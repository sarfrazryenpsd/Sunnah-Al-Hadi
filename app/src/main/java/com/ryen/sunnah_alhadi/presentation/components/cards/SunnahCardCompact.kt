@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun SunnahCardCompact(
    title: String,
    extraIcons: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    borderColor: Color,
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Using standard Text instead of custom BasicText
            Text(
                text = title,
                maxLines = 2,
                style = MaterialTheme.appTypography.sunnahCompactCardTitle,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                extraIcons.forEach { iconComposable ->
                    iconComposable()
                }
            }
        }
    }
}

@Composable
fun ECIconBox(
    iconColor: Color,
    @DrawableRes iconRes: Int,
) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier.size(20.dp)
    )
}

// Simplified preview with fallback colors
@Preview(device = Devices.PHONE, showBackground = true)
@Composable
private fun SunnahCardCompactPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                SunnahCardCompact(
                    title = "Walk Daily with Worshipful Intention",
                    borderColor = Color(0xFFE5C8EF),
                    extraIcons = listOf(
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_info_details, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.primary,
                            )
                        },
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_help, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    )
                )
                SunnahCardCompact(
                    title = "Walk Daily with Worshipful Intention",
                    borderColor = Color(0xFFE5C8EF),
                    extraIcons = listOf(
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_info_details, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.primary,
                            )
                        },
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_help, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ECIconPrev() {
    MaterialTheme {
        ECIconBox(
            iconRes = android.R.drawable.ic_menu_info_details,
            iconColor = MaterialTheme.colorScheme.primary,
        )
    }
}
