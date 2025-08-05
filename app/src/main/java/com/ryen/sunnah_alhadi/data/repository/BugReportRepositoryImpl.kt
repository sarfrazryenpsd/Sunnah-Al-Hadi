package com.ryen.sunnah_alhadi.data.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BugReportDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.data.model.toEntity
import com.ryen.sunnah_alhadi.domain.model.BugReport
import com.ryen.sunnah_alhadi.domain.model.BugReportException
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BugReportRepositoryImpl @Inject constructor(
    private val bugReportDao: BugReportDao,
    private val crashlytics: FirebaseCrashlytics,
) : BugReportRepository {

    override suspend fun saveBugReport(report: BugReport) {
        try {
            // Save locally first
            bugReportDao.insertBugReport(report.toEntity())

            // Send to Crashlytics immediately
            sendToCrashlytics(report)
            markReportAsSynced(report.id)

            crashlytics.log("Bug report ${report.id} submitted and synced successfully")

        } catch (e: Exception) {
            throw Exception("Failed to save bug report: ${e.message}")
        }
    }

    private fun sendToCrashlytics(report: BugReport) {
        // Set custom keys for this bug report
        crashlytics.setCustomKey("bug_report_id", report.id)
        crashlytics.setCustomKey("user_email", report.userEmail)
        crashlytics.setCustomKey("app_version", report.appVersion)
        crashlytics.setCustomKey("device_info", report.deviceInfo)
        crashlytics.setCustomKey("report_timestamp", report.timestamp)
        crashlytics.setCustomKey("is_user_reported_bug", true)

        // Create a custom exception with the bug description
        val bugReportException = BugReportException(
            message = "User-reported bug: ${report.description.take(100)}...",
            fullDescription = report.description,
            reportId = report.id
        )

        // Record the exception to Crashlytics
        crashlytics.recordException(bugReportException)

        // Log additional details
        crashlytics.log("=== USER BUG REPORT ===")
        crashlytics.log("Report ID: ${report.id}")
        crashlytics.log("User Email: ${report.userEmail}")
        crashlytics.log("Description: ${report.description}")
        crashlytics.log("Device: ${report.deviceInfo}")
        crashlytics.log("App Version: ${report.appVersion}")
        crashlytics.log("========================")
    }


    override suspend fun getPendingReports(): List<BugReport> {
        return bugReportDao.getPendingReports().map { it.toDomain() }
    }

    override suspend fun markReportAsSynced(reportId: String) {
        bugReportDao.markAsSynced(reportId)
    }

    override suspend fun syncPendingReports() {
        try {
            val pendingReports = getPendingReports()

            for (report in pendingReports) {
                try {
                    sendToCrashlytics(report)
                    markReportAsSynced(report.id)
                } catch (e: Exception) {
                    crashlytics.recordException(
                        Exception("Failed to sync individual report: ${report.id}: ${e.message}")
                    )
                }
            }

            // Clean up old reports
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            bugReportDao.deleteSyncedOldReports(thirtyDaysAgo)

        } catch (e: Exception) {
            crashlytics.recordException(
                Exception("Bug report sync failed: ${e.message}")
            )
        }
    }

    override suspend fun getPendingReportsCount(): Int {
        return bugReportDao.getPendingReportsCount()
    }
}

