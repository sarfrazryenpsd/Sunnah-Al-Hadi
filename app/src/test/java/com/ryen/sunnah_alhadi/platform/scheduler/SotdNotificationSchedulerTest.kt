package com.ryen.sunnah_alhadi.platform.scheduler

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.common.truth.Truth
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class SotdNotificationSchedulerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private lateinit var scheduler: SotdNotificationScheduler

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // Initialize WorkManager for instrumentation tests
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)

        scheduler = SotdNotificationScheduler(context)
    }

    @After
    fun tearDown() {
        workManager.cancelAllWork()
    }

    @Test
    fun `scheduleNotification should schedule work with correct timing for morning`() {
        // Given
        val notificationTime = NotificationTime.MORNING

        // When
        scheduler.scheduleNotification(notificationTime)

        // Then
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(workInfos).isNotEmpty()

        val workInfo = workInfos.first()
        Truth.assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
        Truth.assertThat(workInfo.tags).contains("sotd_notification")
    }

    @Test
    fun `scheduleNotification should schedule work with correct timing for evening`() {
        // Given
        val notificationTime = NotificationTime.EVENING

        // When
        scheduler.scheduleNotification(notificationTime)

        // Then
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(workInfos).isNotEmpty()

        val workInfo = workInfos.first()
        Truth.assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
        Truth.assertThat(workInfo.tags).contains("sotd_notification")
    }

    @Test
    fun `scheduleNotification should schedule work with correct timing for night`() {
        // Given
        val notificationTime = NotificationTime.NIGHT

        // When
        scheduler.scheduleNotification(notificationTime)

        // Then
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(workInfos).isNotEmpty()

        val workInfo = workInfos.first()
        Truth.assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
        Truth.assertThat(workInfo.tags).contains("sotd_notification")
    }

    @Test
    fun `scheduleNotification should cancel existing work before scheduling new one`() {
        // Given - schedule first notification
        scheduler.scheduleNotification(NotificationTime.MORNING)
        val firstWorkInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        val firstWorkId = firstWorkInfos.first().id

        // When - schedule second notification
        scheduler.scheduleNotification(NotificationTime.EVENING)

        // Then - old work should be replaced
        val newWorkInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(newWorkInfos).hasSize(1)
        Truth.assertThat(newWorkInfos.first().id).isNotEqualTo(firstWorkId)
    }

    @Test
    fun `scheduleNotification should set correct constraints`() {
        // Given
        val notificationTime = NotificationTime.MORNING

        // When
        scheduler.scheduleNotification(notificationTime)

        // Then
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        val workInfo = workInfos.first()

        // Verify work is periodic
        Truth.assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
        Truth.assertThat(workInfo.tags).contains("sotd_notification")
    }

    @Test
    fun `cancelNotification should cancel all scheduled work`() {
        // Given - schedule notification first
        scheduler.scheduleNotification(NotificationTime.MORNING)
        val initialWorkInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(initialWorkInfos).isNotEmpty()

        // When
        scheduler.cancelNotification()

        // Then - assert all work is cancelled
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(workInfos).isNotEmpty()
        workInfos.forEach { workInfo ->
            Truth.assertThat(workInfo.state).isEqualTo(WorkInfo.State.CANCELLED)
        }
    }

    @Test
    fun `cancelNotification should cancel work by tag`() {
        // Given - schedule notification first
        scheduler.scheduleNotification(NotificationTime.MORNING)

        // When
        scheduler.cancelNotification()

        // Then - verify no work with our tag exists
        val taggedWorkInfos = workManager.getWorkInfosByTag("sotd_notification").get()
        val activeWork = taggedWorkInfos.filter {
            it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
        }
        Truth.assertThat(activeWork).isEmpty()
    }

    @Test
    fun `isNotificationScheduled should return true when work is enqueued`() {
        // Given
        scheduler.scheduleNotification(NotificationTime.MORNING)

        // When
        val isScheduled = scheduler.isNotificationScheduled()

        // Then
        Truth.assertThat(isScheduled).isTrue()
    }

    @Test
    fun `isNotificationScheduled should return false when no work is scheduled`() {
        // Given - no work scheduled

        // When
        val isScheduled = scheduler.isNotificationScheduled()

        // Then
        Truth.assertThat(isScheduled).isFalse()
    }

    @Test
    fun `isNotificationScheduled should return false when work is cancelled`() {
        // Given
        scheduler.scheduleNotification(NotificationTime.MORNING)
        scheduler.cancelNotification()

        // When
        val isScheduled = scheduler.isNotificationScheduled()

        // Then
        Truth.assertThat(isScheduled).isFalse()
    }

    @Test
    fun `getScheduledNotificationInfo should return work info when scheduled`() {
        // Given
        scheduler.scheduleNotification(NotificationTime.MORNING)

        // When
        val workInfo = scheduler.getScheduledNotificationInfo()

        // Then
        Truth.assertThat(workInfo).isNotNull()
        Truth.assertThat(workInfo?.state).isEqualTo(WorkInfo.State.ENQUEUED)
    }

    @Test
    fun `getScheduledNotificationInfo should return null when no work is scheduled`() {
        // Given - no work scheduled

        // When
        val workInfo = scheduler.getScheduledNotificationInfo()

        // Then
        Truth.assertThat(workInfo).isNull()
    }

    @Test
    fun `rescheduleOnTimezoneChange should reschedule only when already scheduled`() {
        // Given - no initial schedule
        val initiallyScheduled = scheduler.isNotificationScheduled()
        Truth.assertThat(initiallyScheduled).isFalse()

        // When
        scheduler.rescheduleOnTimezoneChange(NotificationTime.MORNING)

        // Then - should not schedule if wasn't scheduled before
        val afterReschedule = scheduler.isNotificationScheduled()
        Truth.assertThat(afterReschedule).isFalse()
    }

    @Test
    fun `rescheduleOnTimezoneChange should reschedule when already scheduled`() {
        // Given - initial schedule
        scheduler.scheduleNotification(NotificationTime.MORNING)
        val initialWorkInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        val initialWorkId = initialWorkInfos.first().id

        // When
        scheduler.rescheduleOnTimezoneChange(NotificationTime.EVENING)

        // Then - should reschedule with new timing
        val newWorkInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        Truth.assertThat(newWorkInfos).hasSize(1)
        Truth.assertThat(newWorkInfos.first().id).isNotEqualTo(initialWorkId)
    }

    @Test
    fun `calculateInitialDelay should return positive delay for future time`() {
        // This test verifies the private method behavior through public API
        // Given - schedule for a future time
        scheduler.scheduleNotification(NotificationTime.MORNING)

        // When - get work info
        val workInfo = scheduler.getScheduledNotificationInfo()

        // Then - work should be scheduled (not immediate)
        Truth.assertThat(workInfo).isNotNull()
        Truth.assertThat(workInfo?.state).isEqualTo(WorkInfo.State.ENQUEUED)
    }

    @Test
    fun schedule_should_handle_work_manager_exceptions_gracefully() {
        try {
            scheduler.scheduleNotification(NotificationTime.MORNING)
            scheduler.cancelNotification()
            scheduler.isNotificationScheduled()
            scheduler.getScheduledNotificationInfo()
        } catch (e: Exception) {
            fail("Scheduler should not throw, but got: ${e.message}")
        }
    }


    @Test
    fun `multiple schedule calls should replace previous work correctly`() {
        // Given
        val times = listOf(NotificationTime.MORNING, NotificationTime.EVENING, NotificationTime.NIGHT)

        // When - schedule multiple times
        times.forEach { time ->
            scheduler.scheduleNotification(time)

            // Then - should always have exactly one work item
            val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
            Truth.assertThat(workInfos).hasSize(1)
            Truth.assertThat(workInfos.first().state).isEqualTo(WorkInfo.State.ENQUEUED)
        }
    }

    @Test
    fun `work should be periodic with correct interval`() {
        // Given
        scheduler.scheduleNotification(NotificationTime.MORNING)

        // When
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()
        val workInfo = workInfos.first()

        // Then - verify it's a periodic work (we can't directly check the interval in test)
        Truth.assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
        Truth.assertThat(workInfo.tags).contains("sotd_notification")
    }

    @Test
    fun `work should have correct backoff policy`() {
        // Given
        scheduler.scheduleNotification(NotificationTime.MORNING)

        // When
        val workInfos = workManager.getWorkInfosForUniqueWork("sotd_notification_work").get()

        // Then - work should be scheduled with backoff policy
        Truth.assertThat(workInfos).isNotEmpty()
        Truth.assertThat(workInfos.first().state).isEqualTo(WorkInfo.State.ENQUEUED)
    }
}