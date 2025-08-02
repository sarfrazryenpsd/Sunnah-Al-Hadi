package com.ryen.sunnah_alhadi

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SunnahApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = true
        Firebase.crashlytics.setCustomKey("app_name", "Sunnah Al-Hadi")
        Firebase.crashlytics.setCustomKey("is_religious_app", true)

        // Set user identifier (optional - for better crash tracking)
         Firebase.crashlytics.setUserId("anonymous_user_${System.currentTimeMillis()}")
    }
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}