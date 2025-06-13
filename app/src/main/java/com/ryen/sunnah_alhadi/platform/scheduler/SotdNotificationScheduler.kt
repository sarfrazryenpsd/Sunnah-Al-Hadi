package com.ryen.sunnah_alhadi.platform.scheduler

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.platform.worker.SotdNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SotdNotificationScheduler(private val context: Context) {

    companion object {
        private const val WORK_NAME = "sotd_notification_work"
    }

    private val workManager: WorkManager = WorkManager.getInstance(context)

    fun scheduleNotification(notificationTime: NotificationTime) {
        // Cancel existing work
        cancelNotification()

        // Calculate initial delay
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, notificationTime.hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has already passed today, schedule for tomorrow
            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        // Create periodic work request
        val workRequest = PeriodicWorkRequestBuilder<SotdNotificationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .addTag("sotd_notification")
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelNotification() {
        workManager.cancelUniqueWork(WORK_NAME)
    }

    fun isNotificationScheduled(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(WORK_NAME).get()
        return workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
    }
}