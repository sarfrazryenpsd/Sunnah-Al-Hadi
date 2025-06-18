package com.ryen.sunnah_alhadi.platform.worker

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import org.koin.core.context.GlobalContext
import org.koin.mp.KoinPlatform.getKoin

class SotdNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Better DI approach - inject dependencies through constructor or use Application
            val app = applicationContext as? Application
            val koin = app?.let { getKoin() } ?: return Result.failure()

            val generateSotdIdUseCase = koin.get<GenerateNewSotdUseCase>()
            val userPrefsRepository = koin.get<UserPreferencesRepository>()
            val notificationHelper = koin.get<SotdNotificationHelper>()
            val getSunnahByIdUseCase = koin.get<GetSunnahByIdUseCase>()

            // Early exit if notifications are disabled
            val userPrefs = userPrefsRepository.getUserPreferences()
            if (!userPrefs.isSotdNotificationEnabled) {
                Log.d("SotdWorker", "SOTD notifications disabled")
                return Result.success()
            }

            // Check notification permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionGranted = ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!permissionGranted) {
                    Log.w("SotdWorker", "POST_NOTIFICATIONS permission not granted")
                    return Result.success()
                }
            }

            // Check if we need to generate new SOTD
            val shouldGenerate = userPrefsRepository.shouldGenerateNewSotd()
            if (!shouldGenerate) {
                Log.d("SotdWorker", "SOTD already generated for today")
                return Result.success()
            }

            // Generate new SOTD
            val newSotdId = generateSotdIdUseCase()
            if (newSotdId == null) {
                Log.e("SotdWorker", "Failed to generate new SOTD ID")
                return Result.retry()
            }

            // Get sunnah and show notification
            val sunnah = getSunnahByIdUseCase(newSotdId)
            if (sunnah == null) {
                Log.e("SotdWorker", "Failed to get sunnah for ID: $newSotdId")
                return Result.retry()
            }

            notificationHelper.showSotdNotification(sunnah)
            Log.d("SotdWorker", "SOTD notification sent successfully")

            Result.success()

        } catch (e: SecurityException) {
            Log.e("SotdWorker", "Security exception: ${e.message}")
            Result.success() // Don't retry on permission issues
        } catch (e: Exception) {
            Log.e("SotdWorker", "Error in SOTD worker", e)

            // Implement exponential backoff for retries
            val runAttemptCount = runAttemptCount
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}