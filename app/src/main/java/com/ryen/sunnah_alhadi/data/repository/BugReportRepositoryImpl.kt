package com.ryen.sunnah_alhadi.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.ryen.sunnah_alhadi.data.local.datasource.dao.BugReportDao
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.data.model.toDto
import com.ryen.sunnah_alhadi.data.model.toEntity
import com.ryen.sunnah_alhadi.domain.model.BugReport
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BugReportRepositoryImpl @Inject constructor(
    private val bugReportDao: BugReportDao,
    firestore: FirebaseFirestore,
    @param:ApplicationContext private val context: Context
) : BugReportRepository {

    private val bugReportsCollection = firestore.collection("bug_reports")

    override suspend fun saveBugReport(report: BugReport) {
        try {
            // Save locally first
            bugReportDao.insertBugReport(report.toEntity())

            // Try immediate sync if online
            if (isNetworkAvailable()) {
                syncSingleReport(report)
            }
        } catch (e: Exception) {
            // Local save failed - this is critical
            throw Exception("Failed to save bug report locally: ${e.message}")
        }
    }


    override suspend fun getPendingReports(): List<BugReport> {
        return bugReportDao.getPendingReports().map { it.toDomain() }
    }

    override suspend fun markReportAsSynced(reportId: String) {
        bugReportDao.markAsSynced(reportId)
    }

    override suspend fun syncPendingReports() {
        try {
            if (!isNetworkAvailable()) {
                return // No network, skip sync
            }

            val pendingReports = getPendingReports()

            for (report in pendingReports) {
                try {
                    syncSingleReport(report)
                } catch (e: Exception) {
                    // Log individual sync failure but continue with others
                    Firebase.crashlytics.recordException(
                        Exception("Failed to sync bug report ${report.id}: ${e.message}")
                    )
                }
            }

            // Clean up old synced reports (older than 30 days)
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            bugReportDao.deleteSyncedOldReports(thirtyDaysAgo)

        } catch (e: Exception) {
            // Sync process failed
            Firebase.crashlytics.recordException(
                Exception("Bug report sync process failed: ${e.message}")
            )
        }
    }

    private suspend fun syncSingleReport(report: BugReport) {
        try {
            // Upload to Firestore
            bugReportsCollection
                .document(report.id)
                .set(report.toDto())
                .await()

            // Mark as synced locally
            markReportAsSynced(report.id)

            // Log success to Crashlytics
            Firebase.crashlytics.log("Bug report ${report.id} synced successfully")

        } catch (e: Exception) {
            // Sync failed for this report
            throw Exception("Failed to sync report to Firestore: ${e.message}")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override suspend fun getPendingReportsCount(): Int {
        return bugReportDao.getPendingReportsCount()
    }
}