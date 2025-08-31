package com.ryen.sunnah_alhadi.platform.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.domain.useCase.GetSunnahByIdUseCase
import com.ryen.sunnah_alhadi.domain.useCase.sotd.GenerateNewSotdIdUseCase
import com.ryen.sunnah_alhadi.platform.notification.SotdNotificationHelper
import com.ryen.sunnah_alhadi.platform.scheduler.SotdNotificationScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SotdNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val generateNewSotdIdUseCase: GenerateNewSotdIdUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: SotdNotificationHelper,
    private val getSunnahByIdUseCase: GetSunnahByIdUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SotdWorker", "SOTD Worker started execution")

            // Early exit if notifications are disabled
            val userPrefs = userPreferencesRepository.getUserPreferences()
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
            val shouldGenerate = userPreferencesRepository.shouldGenerateNewSotd()
            Log.d("SotdWorker", "Should generate new SOTD: $shouldGenerate")
            if (!shouldGenerate) {
                Log.d("SotdWorker", "SOTD already generated for today")
                return Result.success()
            }

            // Generate new SOTD
            Log.d("SotdWorker", "Generating new SOTD...")
            val newSotdId = generateNewSotdIdUseCase()
            if (newSotdId == null) {
                Log.e("SotdWorker", "Failed to generate new SOTD ID")
                return Result.retry()
            }
            Log.d("SotdWorker", "Generated SOTD ID: $newSotdId")

            // Get sunnah and show notification
            when (val sunnah = getSunnahByIdUseCase(newSotdId)) {
                is com.ryen.sunnah_alhadi.util.Result.Error -> {
                    Log.e("SotdWorker", "Failed to get sunnah for ID: $newSotdId")
                    return Result.retry()
                }

                is com.ryen.sunnah_alhadi.util.Result.Success -> {
                    if (sunnah.data == null) {
                        Log.e("SotdWorker", "Sunnah data is null for ID: $newSotdId")
                        return Result.retry()
                    }

                    Log.d("SotdWorker", "Showing notification for: ${sunnah.data.title}")
                    notificationHelper.showSotdNotification(sunnah.data)
                    Log.d("SotdWorker", "SOTD notification sent successfully")
                    val notificationTime = userPreferencesRepository.getSotdNotificationTime()
                    val scheduler = SotdNotificationScheduler(applicationContext)
                    scheduler.scheduleNextNotification(notificationTime)
                    return Result.success()
                }
            }

        } catch (e: SecurityException) {
            Log.e("SotdWorker", "Security exception: ${e.message}")
            Result.success() // Don't retry on permission issues
        } catch (e: Exception) {
            Log.e("SotdWorker", "Error in SOTD worker", e)

            val notificationTime = userPreferencesRepository.getSotdNotificationTime()
            val scheduler = SotdNotificationScheduler(applicationContext)
            scheduler.scheduleNextNotification(notificationTime)

            // Implement exponential backoff for retries
            val runAttemptCount = runAttemptCount
            if (runAttemptCount < 3) {
                Log.d("SotdWorker", "Retrying... Attempt: ${runAttemptCount + 1}")
                Result.retry()
            } else {
                Log.e("SotdWorker", "Max retry attempts reached, failing")
                Result.failure()
            }
        }
    }
}