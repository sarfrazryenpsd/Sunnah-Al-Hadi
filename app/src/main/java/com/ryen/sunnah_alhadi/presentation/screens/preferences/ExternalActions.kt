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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

🌟 470+ authentic Sunnahs from Prophet Muhammad ﷺ
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

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // email MIME type
            putExtra(Intent.EXTRA_EMAIL, arrayOf("mdsarfraz.ilanos1915@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Sunnah Al-Hadi - User Feedback")
            putExtra(Intent.EXTRA_TEXT, emailTemplate)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Choose an email app"))
        } catch (e: ActivityNotFoundException) {
            // Fallback: copy email to clipboard
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Developer Email", "mdsarfraz.ilanos1915@gmail.com")
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                context,
                "No email app found. Email copied to clipboard: mdsarfraz.ilanos1915@gmail.com",
                Toast.LENGTH_LONG
            ).show()
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

}