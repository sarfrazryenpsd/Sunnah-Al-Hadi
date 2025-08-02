package com.ryen.sunnah_alhadi.presentation.screens.preferences

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri

object PreferenceActions {

    fun openPlayStoreRating(context: Context) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "market://details?id=${context.packageName}".toUri()
            )
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to web browser
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
            )
            context.startActivity(webIntent)
        }
    }

    fun shareApp(context: Context) {
        val shareText = """
السلام عليكم ورحمة الله وبركاته

I'd like to share this beautiful Islamic app with you: Sunnah Al-Hadi

🌟 500+ authentic Sunnahs from Prophet Muhammad ﷺ
📚 Categorized by daily activities
🔖 Bookmark your favorites
📱 Works completely offline

Download it from Play Store:
https://play.google.com/store/apps/details?id=${context.packageName}

May Allah ﷻ bless you and your family.

بارك الله فيك
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Sunnah Al-Hadi - Islamic App")
        }

        context.startActivity(Intent.createChooser(intent, "Share Sunnah Al-Hadi"))
    }

    fun contactDeveloper(context: Context) {
        val emailTemplate = """
بسم الله الرحمن الرحيم
السلام عليكم ورحمة الله وبركاته

App: Sunnah Al-Hadi
Version: ${getAppVersion(context)}
Device: ${Build.MODEL}
Android: ${Build.VERSION.RELEASE}

Message:
[Please write your message here]

جزاك الله خيراً
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("developer@sunnahalhadi.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Sunnah Al-Hadi - User Feedback")
            putExtra(Intent.EXTRA_TEXT, emailTemplate)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback: copy email to clipboard
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Developer Email", "developer@sunnahalhadi.com")
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Email copied to clipboard: developer@sunnahalhadi.com", Toast.LENGTH_LONG).show()
        }
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }
}