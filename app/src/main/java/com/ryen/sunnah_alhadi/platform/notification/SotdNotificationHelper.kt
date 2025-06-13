package com.ryen.sunnah_alhadi.platform.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ryen.sunnah_alhadi.MainActivity
import com.ryen.sunnah_alhadi.R
import com.ryen.sunnah_alhadi.domain.model.Sunnah

class SotdNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "sunnah_of_the_day"
        const val CHANNEL_NAME = "Sunnah of the Day"
        const val NOTIFICATION_ID = 1001
    }

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily Sunnah notifications to inspire your day"
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSotdNotification(sunnah: Sunnah) {
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                Log.w("SotdNotification", "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        // Intent to open the app and show SOTD
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("show_sotd", true)
            putExtra("sotd_id", sunnah.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with a valid icon
            .setContentTitle("🌟 Sunnah of the Day")
            .setContentText(sunnah.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${sunnah.title}\n\nTap to read today's blessed Sunnah")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}