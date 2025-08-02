package com.ryen.sunnah_alhadi.data.model

import com.ryen.sunnah_alhadi.domain.model.BugReport

data class BugReportDto(
    val id: String,
    val description: String,
    val userEmail: String,
    val appVersion: String,
    val deviceInfo: String,
    val timestamp: Long,
    val reportedAt: Long = System.currentTimeMillis()
)

fun BugReport.toDto(): BugReportDto {
    return BugReportDto(
        id = id,
        description = description,
        userEmail = userEmail,
        appVersion = appVersion,
        deviceInfo = deviceInfo,
        timestamp = timestamp
    )
}