package com.ryen.sunnah_alhadi

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerFactoryEntryPoint {
    fun workerFactory(): HiltWorkerFactory
}

class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkerFactoryEntryPoint::class.java
        )

        val configuration = Configuration.Builder()
            .setWorkerFactory(hiltEntryPoint.workerFactory())
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}