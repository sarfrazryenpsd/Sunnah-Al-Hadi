package com.ryen.sunnah_alhadi.data.local.datasource.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "bug_reports")
data class BugReportEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val userEmail: String,
    val appVersion: String,
    val deviceInfo: String,
    val timestamp: Long,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)