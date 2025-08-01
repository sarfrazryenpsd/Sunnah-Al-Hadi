@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.ryen.sunnah_alhadi.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.presentation.util.ValidationResult
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun PreferenceSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
fun PreferenceItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector,
    iconColor: Color,
    trailingContent: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val clickableModifier = if (onClick != null && enabled) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = if (enabled) iconColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        trailingContent()
    }
}

@Composable
fun PreferenceSwitch(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    leadingIcon: ImageVector,
    iconColor: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    PreferenceItem(
        title = title,
        subtitle = subtitle,
        leadingIcon = leadingIcon,
        iconColor = iconColor,
        enabled = enabled,
        modifier = modifier,
        onClick = if (enabled) { { onCheckedChange(!checked) } } else null,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled
            )
        }
    )
}

@Composable
fun PreferenceTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    iconColor: Color,
    validation: ValidationResult,
    characterCount: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(12.dp),
            isError = !validation.isValid,
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    validation.errorMessage?.let { errorMsg ->
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = characterCount,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreferenceTextFieldPreview() {
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
        ) {
            PreferenceTextField(
                title = "Title",
                value = "Value",
                onValueChange = {},
                leadingIcon = androidx.compose.material.icons.Icons.Default.Person,
                iconColor = MaterialTheme.colorScheme.primary,
                validation = ValidationResult(true, null),
                characterCount = "10/20",
                placeholder = "Placeholder"
            )
        }
}

@Preview(showBackground = true)
@Composable
private fun PreferenceSwitchPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))

    ) {
        PreferenceSwitch(
            title = "Title",
            subtitle = "Subtitle",
            checked = true,
            onCheckedChange = {},
            leadingIcon = androidx.compose.material.icons.Icons.Default.Person,
            iconColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreferenceItemPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        PreferenceItem(
            title = "Title",
            subtitle = "Subtitle",
            leadingIcon = androidx.compose.material.icons.Icons.Default.Person,
            iconColor = MaterialTheme.colorScheme.primary,
            trailingContent = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}

@Preview
@Composable
private fun PreferenceSectionPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        PreferenceSection(title = "Title") {
            PreferenceItem(
                title = "Title",
                subtitle = "Subtitle",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Person,
                iconColor = MaterialTheme.colorScheme.primary,
                trailingContent = {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            PreferenceItem(
                title = "Title",
                subtitle = "Subtitle",
                leadingIcon = androidx.compose.material.icons.Icons.Default.Person,
                iconColor = MaterialTheme.colorScheme.primary,
                trailingContent = {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

