@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)

package com.ryen.sunnah_alhadi.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.presentation.util.ValidationResult
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode

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
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector,
    iconColor: Color,
    trailingContent: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
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
            modifier = Modifier
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
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    leadingIcon: ImageVector,
    iconColor: Color,
    enabled: Boolean = true
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSegmentedButton(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeOptions = listOf(
        ThemeMode.LIGHT to "Light",
        ThemeMode.DARK to "Dark",
        ThemeMode.SYSTEM to "System"
    )

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        themeOptions.forEachIndexed { index, (theme, label) ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = themeOptions.size
                ),
                onClick = { onThemeSelected(theme) },
                selected = selectedTheme == theme,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun NotificationTimeDropdown(
    selectedTime: NotificationTime,
    onTimeSelected: (NotificationTime) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val timeOptions = NotificationTime.entries

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedTime.displayName,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
            enabled = enabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            timeOptions.forEach { time ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = time.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onTimeSelected(time)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (time == selectedTime) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun BugReportDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (description: String, email: String) -> Unit,
    isLoading: Boolean = false
) {
    if (!showDialog) return

    var description by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isDescriptionError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Report Bug",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.reportbug_heading),
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        isDescriptionError = it.isBlank()
                    },
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Bug Description *") },
                    placeholder = { Text("Please describe the issue you encountered...") },
                    isError = isDescriptionError,
                    supportingText = if (isDescriptionError) {
                        { Text("Description is required") }
                    } else null,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("your.email@example.com") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Your device information will be included automatically to help with debugging.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        onSubmit(description.trim(), email.trim())
                    } else {
                        isDescriptionError = true
                    }
                },
                enabled = !isLoading && description.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ContentDisplayDialog(
    title: String,
    content: String,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.8f)
    )
}

@Composable
fun NotificationPermissionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!showDialog) return
    val boldPart = "Sunnah Of the Day"
    val base = stringResource(R.string.notificationDialogue1, boldPart)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        },
        title = {
            Text(
                text = "Enable Reminder",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = highlightText(base, boldPart, SpanStyle(fontWeight = FontWeight.Bold)),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(R.string.notifcationDialogue2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Allow Notifications")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        }
    )
}

private fun highlightText(fullText: String, target: String, highlightStyle: SpanStyle): AnnotatedString {
    val start = fullText.indexOf(target)
    return buildAnnotatedString {
        if (start >= 0) {
            append(fullText.substring(0, start))
            withStyle(highlightStyle) {
                append(target)
            }
            append(fullText.substring(start + target.length))
        } else {
            append(fullText) // fallback
        }
    }
}





//-------------------------------------------------------------Previews----------------------------------------------------------------------------------

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
                leadingIcon = Icons.Default.Person,
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
            leadingIcon = Icons.Default.Person,
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
            leadingIcon = Icons.Default.Person,
            iconColor = MaterialTheme.colorScheme.primary,
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.Person,
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
                leadingIcon = Icons.Default.Person,
                iconColor = MaterialTheme.colorScheme.primary,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            PreferenceItem(
                title = "Title",
                subtitle = "Subtitle",
                leadingIcon = Icons.Default.Person,
                iconColor = MaterialTheme.colorScheme.primary,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun ThemeSegmentButtonPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        ThemeSegmentedButton(
            selectedTheme = ThemeMode.LIGHT,
            onThemeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationTimeDropdownPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        NotificationTimeDropdown(
            selectedTime = NotificationTime.MORNING,
            onTimeSelected = {},
            enabled = true,
        )
    }
}

@Preview
@Composable
private fun BugReportDialoguePrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        BugReportDialog(
            showDialog = true,
            onDismiss = {},
            onSubmit = { _, _ -> },
            isLoading = false
        )
    }
}

@Preview
@Composable
private fun ContentDisplayPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) { 
        ContentDisplayDialog(
            title = "Title",
            content = "Content",
            showDialog = true,
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun NotificationPermissionDialogPrev() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp))
    ) {
        NotificationPermissionDialog(
            showDialog = true,
            onDismiss = {},
            onConfirm = {}
        )
    }
}

