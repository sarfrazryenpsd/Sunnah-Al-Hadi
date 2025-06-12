package com.ryen.sunnah_alhadi

import android.app.Application
import com.ryen.sunnah_alhadi.di.databaseModule
import com.ryen.sunnah_alhadi.di.sotdModule
import com.ryen.sunnah_alhadi.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SunnahApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SunnahApplication)
            modules(
                databaseModule,
                useCaseModule,
                sotdModule
                // Add other modules here
            )
        }
    }
}