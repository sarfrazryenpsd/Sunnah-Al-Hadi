package com.ryen.sunnah_alhadi.data.model

import com.ryen.sunnah_alhadi.data.local.datasource.entity.BugReportEntity
import com.ryen.sunnah_alhadi.domain.model.BugReport

fun BugReportEntity.toDomain(): BugReport {
    return BugReport(
        id = id,
        description = description,
        userEmail = userEmail,
        appVersion = appVersion,
        deviceInfo = deviceInfo,
        timestamp = timestamp,
        isSynced = isSynced
    )
}

fun BugReport.toEntity(): BugReportEntity {
    return BugReportEntity(
        id = id,
        description = description,
        userEmail = userEmail,
        appVersion = appVersion,
        deviceInfo = deviceInfo,
        timestamp = timestamp,
        isSynced = isSynced
    )
}