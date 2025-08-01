package com.ryen.sunnah_alhadi.presentation.screens.preferences

import java.util.UUID

data class BugReport(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val userEmail: String,
    val appVersion: String,
    val deviceInfo: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)