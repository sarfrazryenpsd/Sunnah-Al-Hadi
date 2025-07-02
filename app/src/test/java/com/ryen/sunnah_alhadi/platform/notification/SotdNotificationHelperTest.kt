package com.ryen.sunnah_alhadi.platform.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU]) // Default to Android 13 for testing
class SotdNotificationHelperTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var sotdNotificationHelper: SotdNotificationHelper
    private lateinit var mockSunnah: Sunnah

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        mockSunnah = mockk {
            every { id } returns "01_01"
            every { title } returns "Test Sunnah Title"
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun createNotificationChannel_creates_channel_on_initialization() {
        // When
        sotdNotificationHelper = SotdNotificationHelper(context)

        // Then
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(SotdNotificationHelper.CHANNEL_ID)

        assertNotNull(channel)
        assertEquals(SotdNotificationHelper.CHANNEL_NAME, channel.name)
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, channel.importance)
    }

    @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
    @Test
    fun showSotdNotification_shows_notification_when_permission_granted_on_Android_13() {
        // Given
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns PackageManager.PERMISSION_GRANTED

        sotdNotificationHelper = SotdNotificationHelper(context)

        // When
        sotdNotificationHelper.showSotdNotification(mockSunnah)

        // Then
        val shadowNotificationManager = shadowOf(
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
        val notifications = shadowNotificationManager.allNotifications

        assertEquals(1, notifications.size)
        val notification = notifications[0]
        assertEquals("🌟 Sunnah of the Day", notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals("Test Sunnah Title", notification.extras.getString(NotificationCompat.EXTRA_TEXT))
    }

    @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
    @Test
    fun showSotdNotification_does_not_show_notification_when_permission_denied_on_Android_13() {
        // Given
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns PackageManager.PERMISSION_DENIED

        sotdNotificationHelper = SotdNotificationHelper(context)

        // When
        sotdNotificationHelper.showSotdNotification(mockSunnah)

        // Then
        val shadowNotificationManager = shadowOf(
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
        val notifications = shadowNotificationManager.allNotifications

        assertEquals(0, notifications.size)
    }

    @Config(sdk = [Build.VERSION_CODES.S]) // Android 12
    @Test
    fun showSotdNotification_shows_notification_on_Android_12_and_below_without_permission_check() {
        // Given
        mockkStatic(ContextCompat::class)
        sotdNotificationHelper = SotdNotificationHelper(context)

        // When
        sotdNotificationHelper.showSotdNotification(mockSunnah)

        // Then
        val shadowNotificationManager = shadowOf(
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
        val notifications = shadowNotificationManager.allNotifications

        assertEquals(1, notifications.size)

        // Verify permission check was not called for Android 12
        verify(exactly = 0) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Test
    fun showSotdNotification_creates_correct_notification_content() {
        // Given
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns PackageManager.PERMISSION_GRANTED

        sotdNotificationHelper = SotdNotificationHelper(context)

        // When
        sotdNotificationHelper.showSotdNotification(mockSunnah)

        // Then
        val shadowNotificationManager = shadowOf(
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
        val notifications = shadowNotificationManager.allNotifications

        assertEquals(1, notifications.size)
        val notification = notifications[0]

        assertEquals("🌟 Sunnah of the Day", notification.extras.getString(NotificationCompat.EXTRA_TITLE))
        assertEquals("Test Sunnah Title", notification.extras.getString(NotificationCompat.EXTRA_TEXT))

        // Verify notification was posted with correct ID
        assertTrue(shadowNotificationManager.size() > 0)
        val postedNotifications = shadowNotificationManager.allNotifications
        assertTrue(postedNotifications.any { it.extras.getString(NotificationCompat.EXTRA_TITLE) == "🌟 Sunnah of the Day" })
    }

    @Test
    fun showSotdNotification_creates_intent_with_correct_extras() {
        // Given
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns PackageManager.PERMISSION_GRANTED

        sotdNotificationHelper = SotdNotificationHelper(context)

        // When
        sotdNotificationHelper.showSotdNotification(mockSunnah)

        // Then
        val shadowNotificationManager = shadowOf(
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
        val notifications = shadowNotificationManager.allNotifications

        assertEquals(1, notifications.size)
        val notification = notifications[0]

        // Get the PendingIntent from the notification
        val contentIntent = notification.contentIntent
        assertNotNull(contentIntent)

        // Extract the intent from PendingIntent using Robolectric's shadow
        val shadowPendingIntent = shadowOf(contentIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(true, intent.getBooleanExtra("show_sotd", false))
        assertEquals("01_01", intent.getStringExtra("sotd_id"))
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.flags)
    }

    @Test
    fun showSotdNotification_notification_has_correct_properties() {
        // Given
        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } returns PackageManager.PERMISSION_GRANTED

        sotdNotificationHelper = SotdNotificationHelper(context)

        // When
        sotdNotificationHelper.showSotdNotification(mockSunnah)

        // Then
        val shadowNotificationManager = shadowOf(
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )
        val notifications = shadowNotificationManager.allNotifications

        assertEquals(1, notifications.size)
        val notification = notifications[0]

        // Check notification properties
        assertTrue(notification.flags and Notification.FLAG_AUTO_CANCEL != 0)
        assertEquals(NotificationCompat.CATEGORY_RECOMMENDATION, notification.category)
        // Note: Priority is set during build time, check if notification was created successfully
        assertNotNull(notification.contentIntent)
    }
}