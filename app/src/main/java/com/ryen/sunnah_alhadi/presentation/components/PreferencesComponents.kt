@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)

package com.ryen.sunnah_alhadi.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
import com.ryen.sunnah_alhadi.ui.theme.appTypography

@Composable
fun PreferenceSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.appTypography.notificationType,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
fun PreferenceHorizontalItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    @DrawableRes leadingIcon: Int,
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
            .padding(horizontal = 6.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (enabled) iconColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.08f
                        )
                    )
            ){
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = if (enabled) iconColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.38f
                    ),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.appTypography.extraAndNotificationTitle.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.38f
                    )
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.appTypography.notificationSubtitle,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.38f
                        )
                    )
                }
            }
        }

        //Spacer(modifier = Modifier.width(16.dp))

        trailingContent()
    }
}

@Composable
fun PreferenceVerticalItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    @DrawableRes leadingIcon: Int,
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(horizontal = 6.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (enabled) iconColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.08f
                        )
                    )
            ){
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = if (enabled) iconColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.38f
                    ),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.appTypography.extraAndNotificationTitle.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.38f
                    )
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.appTypography.notificationSubtitle,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.38f
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
    @DrawableRes leadingIcon: Int,
    iconColor: Color,
    enabled: Boolean = true
) {
    PreferenceHorizontalItem(
        title = title,
        subtitle = subtitle,
        leadingIcon = leadingIcon,
        iconColor = iconColor,
        enabled = enabled,
        modifier = modifier,
        onClick = if (enabled) {
            { onCheckedChange(!checked) }
        } else null,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.inversePrimary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
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
private data class ThemeOptions (
    val themeMode: ThemeMode,
    val label: String,
    val icon: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSegmentedButton(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeOptions = listOf(
        ThemeOptions(ThemeMode.LIGHT, "Light", R.drawable.interface_light),
        ThemeOptions(ThemeMode.DARK, "Dark", R.drawable.interface_dark),
        ThemeOptions(ThemeMode.SYSTEM, "System", R.drawable.interface_system)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        themeOptions.forEachIndexed { index, theme ->
            ThemeModeToggle(
                themeIcon = theme.icon,
                themeLabel = theme.label,
                isSelected = selectedTheme == theme.themeMode,
                onClick = { onThemeSelected(theme.themeMode) },
                modifier = Modifier.weight(1f)
            )
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
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp)
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
fun UserNameDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    username: String,
    placeholder: String,
    usernameError: String?,
    isUserNameValid: Boolean,
    onUsernameChange: (String) -> Unit,
    onSave: (String) -> Unit
) {
    if (!showDialog) return

    val usernameState = remember { mutableStateOf(username) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change name",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Let's personalize your experience.\nWhat is your good name?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = usernameState.value,
                    onValueChange = {
                        usernameState.value = it
                        onUsernameChange(it)
                    },
                    label = { Text("Name") },
                    isError = usernameError != null,
                    supportingText = {
                        usernameError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error
                            )
                        } ?: Text(
                            text = "Enter your preferred name for the app",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(usernameState.value)
                },
                enabled = isUserNameValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    )
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

private fun highlightText(
    fullText: String,
    target: String,
    highlightStyle: SpanStyle
): AnnotatedString {
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

@Composable
fun ThemeModeToggle(
    @DrawableRes themeIcon: Int,
    themeLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animate corner radius: 16.dp (rounded) -> 32.dp (circular)
    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 16.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cornerRadius",
    )

    // Optionally animate border width for extra feedback
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 300),
        label = "borderWidth",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.height(64.dp),
            shape = RoundedCornerShape(cornerRadius),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                ),
            border =
                androidx.compose.foundation.BorderStroke(
                    width = borderWidth,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.inversePrimary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(themeIcon),
                    contentDescription = themeLabel,
                    modifier = Modifier.size(24.dp),
                    tint =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = themeLabel,
            style = MaterialTheme.typography.labelMedium,
            color =
                if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}




//-------------------------------------------------------------Previews----------------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = android.graphics.Color.WHITE.toLong())
@Composable
private fun ThemeOptionPreview() {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ThemeModeToggle(
                isSelected = true,
                onClick = {},
                themeLabel = "Light",
                themeIcon = R.drawable.interface_light,
                modifier = Modifier.weight(1f),
            )
            ThemeModeToggle(
                isSelected = false,
                onClick = {},
                themeLabel = "Dark",
                themeIcon = R.drawable.interface_dark,
                modifier = Modifier.weight(1f),
            )
        }
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
            leadingIcon = R.drawable.interface_user,
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
        PreferenceHorizontalItem(
            title = "Title",
            subtitle = "Subtitle",
            leadingIcon = R.drawable.interface_user,
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
            PreferenceHorizontalItem(
                title = "Title",
                subtitle = "Subtitle",
                leadingIcon = R.drawable.interface_user,
                iconColor = MaterialTheme.colorScheme.primary,
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            PreferenceHorizontalItem(
                title = "Title",
                subtitle = "Subtitle",
                leadingIcon = R.drawable.interface_user,
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

