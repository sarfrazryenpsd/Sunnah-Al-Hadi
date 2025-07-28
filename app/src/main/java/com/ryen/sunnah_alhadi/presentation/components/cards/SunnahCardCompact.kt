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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SunnahCompactCard(
    title: String,
    extraIcons: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    borderColor: Color,
) {
    Surface(
        modifier = modifier
            //.wrapContentSize()
            .clip(RoundedCornerShape(8.dp)),
        tonalElevation = 2.dp,
        border = BorderStroke(3.dp, borderColor),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Using standard Text instead of custom BasicText
            BasicText(
                text = title,
                maxLines = 2,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = MaterialTheme.typography.titleMedium.fontSize * .6,
                    maxFontSize = MaterialTheme.typography.titleMedium.fontSize
                )
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
    iconBackground: Color,
    borderColor: Color,
    @DrawableRes iconRes: Int,
    boxSize: Dp,
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.5.dp, borderColor),
        color = iconBackground, // Use color instead of contentColor for background
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .size(boxSize)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

// Simplified preview with fallback colors
@Preview(device = Devices.PHONE, showBackground = true)
@Composable
private fun SunnahCardCompactPrev() {
    MaterialTheme {
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
                SunnahCompactCard(
                    title = "Walk Daily with Worshipful Intention",
                    borderColor = Color(0xFFE5C8EF),
                    extraIcons = listOf(
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_info_details, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.primary,
                                boxSize = 24.dp,
                                iconBackground = MaterialTheme.colorScheme.primaryContainer,
                                borderColor = MaterialTheme.colorScheme.outline
                            )
                        },
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_help, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.secondary,
                                boxSize = 24.dp,
                                iconBackground = MaterialTheme.colorScheme.secondaryContainer,
                                borderColor = MaterialTheme.colorScheme.outline
                            )
                        }
                    )
                )
                SunnahCompactCard(
                    title = "Walk Daily with Worshipful Intention",
                    borderColor = Color(0xFFE5C8EF),
                    extraIcons = listOf(
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_info_details, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.primary,
                                boxSize = 24.dp,
                                iconBackground = MaterialTheme.colorScheme.primaryContainer,
                                borderColor = MaterialTheme.colorScheme.outline
                            )
                        },
                        {
                            ECIconBox(
                                iconRes = android.R.drawable.ic_menu_help, // Using system icon as fallback
                                iconColor = MaterialTheme.colorScheme.secondary,
                                boxSize = 24.dp,
                                iconBackground = MaterialTheme.colorScheme.secondaryContainer,
                                borderColor = MaterialTheme.colorScheme.outline
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
            boxSize = 24.dp,
            iconBackground = MaterialTheme.colorScheme.primaryContainer,
            borderColor = MaterialTheme.colorScheme.outline
        )
    }
}

// Debug version to test basic rendering
@Preview(showBackground = true)
@Composable
private fun SimpleCardTest() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(),
            tonalElevation = 2.dp,
            border = BorderStroke(3.dp, Color.Red),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Test Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "If you see this, basic rendering works",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}