package com.ryen.sunnah_alhadi.platform.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import org.koin.core.context.GlobalContext

class SotdNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val koinApplication = GlobalContext.get()
            val generateSotdIdUseCase = koinApplication.get<GenerateNewSotdUseCase>()
            val userPrefsRepository = koinApplication.get<UserPreferencesRepository>()
            val notificationHelper = koinApplication.get<SotdNotificationHelper>()
            val getSunnahByIdUseCase = koinApplication.get<GetSunnahByIdUseCase>()

            val userPrefs = userPrefsRepository.getUserPreferences()
            if (!userPrefs.isSotdNotificationEnabled) {
                return Result.success()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionGranted = ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!permissionGranted) {
                    return Result.success()
                }
            }
            val shouldGenerate = userPrefsRepository.shouldGenerateNewSotd()
            if (shouldGenerate) {
                val newSotdId = generateSotdIdUseCase()
                newSotdId?.let { id ->
                    val sunnah = getSunnahByIdUseCase(id)
                    sunnah?.let { notificationHelper.showSotdNotification(it) }
                }
            }


            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}