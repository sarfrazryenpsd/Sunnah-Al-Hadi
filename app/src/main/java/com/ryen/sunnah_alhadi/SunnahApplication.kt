package com.ryen.sunnah_alhadi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.ryen.sunnah_alhadi.platform.worker.BugReportSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class SunnahApplication : Application() {


    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()

        initializeFirebase()
        setupBugReportSync()

    }

    private fun initializeFirebase() {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = true
        Firebase.crashlytics.setCustomKey("app_name", "Sunnah Al-Hadi")
        Firebase.crashlytics.setCustomKey("is_religious_app", true)

        // Set user identifier (optional - for better crash tracking)
        Firebase.crashlytics.setUserId("anonymous_user_${System.currentTimeMillis()}")
    }


    private fun setupBugReportSync() {
        // Use WorkManager to periodically sync bug reports
        val syncWorkRequest = PeriodicWorkRequestBuilder<BugReportSyncWorker>(
            24, TimeUnit.HOURS // Sync once per day
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "bug_report_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
    }
}



