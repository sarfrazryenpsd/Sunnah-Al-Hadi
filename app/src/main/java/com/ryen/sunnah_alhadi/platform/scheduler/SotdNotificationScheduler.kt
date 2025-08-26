package com.ryen.sunnah_alhadi.platform.scheduler

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.platform.worker.SotdNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SotdNotificationScheduler @Inject constructor(@param:ApplicationContext private val context: Context) {

    companion object {
        private const val WORK_NAME = "sotd_notification_work"
        private const val TAG_SOTD = "sotd_notification"
    }

    private val workManager: WorkManager = WorkManager.getInstance(context)

    fun scheduleNotification(notificationTime: NotificationTime) {
        // Cancel existing work first
        cancelNotification()

        val initialDelay = calculateInitialDelay(notificationTime)

        // Create constraints to ensure reliability
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .build()

        // Use daily periodic work with more robust scheduling
        val workRequest = PeriodicWorkRequestBuilder<SotdNotificationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
            flexTimeInterval = 30, // 30-minute flex window
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(TAG_SOTD)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        Log.d(
            "SotdScheduler",
            "Scheduled notification for ${notificationTime.name} with ${initialDelay}ms delay"
        )
    }

    private fun calculateInitialDelay(notificationTime: NotificationTime): Long {
        val now = ZonedDateTime.now()
        var targetTime = now.withHour(notificationTime.hour)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        // If the time has already passed today, schedule for tomorrow
        if (targetTime.isBefore(now) || targetTime.isEqual(now)) {
            targetTime = targetTime.plusDays(1)
        }

        val delayMillis = Duration.between(now, targetTime).toMillis()
        return maxOf(delayMillis, 0L) // Ensure non-negative delay
    }

    fun cancelNotification() {
        workManager.cancelUniqueWork(WORK_NAME)
        workManager.cancelAllWorkByTag(TAG_SOTD)
        Log.d("SotdScheduler", "Cancelled SOTD notifications")
    }

    suspend fun isNotificationScheduled(): Boolean = withContext(Dispatchers.IO) {
        try {
            val workInfos = workManager.getWorkInfosForUniqueWork(WORK_NAME).get()
            workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        } catch (e: Exception) {
            Log.e("SotdScheduler", "Error checking notification status", e)
            false
        }
    }

    suspend fun getScheduledNotificationInfo(): WorkInfo? = withContext(Dispatchers.IO) {
        try {
            val workInfos = workManager.getWorkInfosForUniqueWork(WORK_NAME).get()
            workInfos.firstOrNull { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        } catch (e: Exception) {
            Log.e("SotdScheduler", "Error getting notification info", e)
            null
        }
    }


    // Add method to reschedule on timezone change
    suspend fun rescheduleOnTimezoneChange(notificationTime: NotificationTime) {
        if (isNotificationScheduled()) {
            scheduleNotification(notificationTime)
        }
    }
}