package com.ryen.sunnah_alhadi.domain.repository

import com.ryen.sunnah_alhadi.domain.model.BugReport

interface BugReportRepository {
    suspend fun saveBugReport(report: BugReport)
    suspend fun getPendingReports(): List<BugReport>
    suspend fun markReportAsSynced(reportId: String)
    suspend fun syncPendingReports()
    suspend fun getPendingReportsCount(): Int
}