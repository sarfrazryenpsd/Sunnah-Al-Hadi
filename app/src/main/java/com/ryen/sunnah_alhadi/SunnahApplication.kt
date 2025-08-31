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
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import com.ryen.sunnah_alhadi.platform.worker.BugReportSyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class SunnahApplication : Application() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var sotdNotificationScheduler: SotdNotificationScheduler

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Initialize critical components synchronously
        initializeEssentials()

        // Initialize non-critical components asynchronously
        initializeInBackground()
    }

    private fun initializeEssentials() {
        // Only minimal essential initialization here
        // Firebase is already initialized by FirebaseInitProvider
    }

    private fun initializeInBackground() {
        // Stagger initialization tasks to reduce contention
        applicationScope.launch {
            // Initialize Firebase configuration first (most critical)
            initializeFirebase()
        }

        // Delay WorkManager setup slightly to reduce initial load
        applicationScope.launch {
            delay(100) // Small delay to reduce startup contention
            setupBugReportSync()
        }

        // Delay SOTD notifications setup even more as it's least critical for startup
        applicationScope.launch {
            delay(200) // Longer delay for non-critical features
            initializeSotdNotifications()
        }
    }

    private fun initializeFirebase() {
        try {
            // Firebase is already initialized, just configure it
            // The FirebaseModule handles the main initialization
            Firebase.crashlytics.setCustomKey("app_name", "Sunnah Al-Hadi")
            Firebase.crashlytics.setCustomKey("is_religious_app", true)
            Firebase.crashlytics.setUserId("anonymous_user_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            // Silently handle any Firebase initialization issues
            // Don't let them crash the app
        }
    }

    private fun setupBugReportSync() {
        try {
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

            WorkManager.getInstance(this@SunnahApplication)
                .enqueueUniquePeriodicWork(
                    "bug_report_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncWorkRequest
                )
        } catch (e: Exception) {
            // Handle WorkManager setup issues gracefully
            Firebase.crashlytics.recordException(
                Exception("Failed to setup bug report sync", e)
            )
        }
    }

    /**
     * Initialize SOTD notifications for existing users who have completed onboarding
     * and have notifications enabled but may not have notifications scheduled due to
     * app updates or reinstallation.
     */
    private suspend fun initializeSotdNotifications() {
        try {
            val userPrefs = userPreferencesRepository.getUserPreferences()

            // Only schedule for users who completed onboarding and have notifications enabled
            if (userPrefs.hasCompletedOnboarding && userPrefs.isSotdNotificationEnabled) {
                // Check if notifications are already scheduled
                val isScheduled = sotdNotificationScheduler.isNotificationScheduled()

                if (!isScheduled) {
                    // Schedule notifications with user's preferred time
                    sotdNotificationScheduler.scheduleNotification(userPrefs.sotdNotificationTime)
                }
            }
        } catch (e: Exception) {
            // Log error but don't crash the app
            Firebase.crashlytics.recordException(
                Exception("Failed to initialize SOTD notifications", e)
            )
        }
    }
}
