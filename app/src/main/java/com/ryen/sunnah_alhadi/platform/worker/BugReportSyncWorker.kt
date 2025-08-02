package com.ryen.sunnah_alhadi.platform.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ryen.sunnah_alhadi.domain.repository.BugReportRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

@HiltWorker
class BugReportSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val crashlytics: FirebaseCrashlytics,
    private val bugReportRepository: BugReportRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            crashlytics.log("Starting background bug report sync")

            val pendingCount = bugReportRepository.getPendingReportsCount()
            if (pendingCount > 0) {
                bugReportRepository.syncPendingReports()
                crashlytics.log("Background bug report sync completed. Synced $pendingCount reports")
            } else {
                crashlytics.log("No pending bug reports to sync")
            }

            Result.success()
        } catch (e: Exception) {
            crashlytics.recordException(
                Exception("Background bug report sync failed: ${e.message}")
            )

            // Retry with exponential backoff for network issues
            if (e is IOException || e.message?.contains("network", ignoreCase = true) == true) {
                Result.retry()
            } else {
                // For other errors, don't retry to avoid infinite loops
                Result.failure()
            }
        }
    }
}