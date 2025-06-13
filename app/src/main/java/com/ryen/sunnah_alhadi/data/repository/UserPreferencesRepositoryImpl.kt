package com.ryen.sunnah_alhadi.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ryen.sunnah_alhadi.data.local.proto.ProtoUserPreferencesSerializer
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

class UserPreferencesRepositoryImpl (
    private val context: Context
) : UserPreferencesRepository {

    private val Context.dataStore: DataStore<ProtoUserPreferences> by dataStore(
        fileName = "user_preferences.pb",
        serializer = ProtoUserPreferencesSerializer
    )

    override suspend fun getUserPreferences(): UserPreferences {
        return context.dataStore.data.first().toDomain()
    }

    override suspend fun updateUsername(username: String) {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setUsername(username)
                .build()
        }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setThemeMode(themeMode.ordinal)
                .build()
        }
    }

    override suspend fun updateDynamicTheme(enabled: Boolean) {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsDynamicThemeEnabled(enabled)
                .build()
        }
    }

    override suspend fun updateDailyReminder(enabled: Boolean) {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsDailyReminderEnabled(enabled)
                .build()
        }
    }

    override suspend fun markOnboardingCompleted() {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setHasCompletedOnboarding(true)
                .build()
        }
    }

    override suspend fun markDisclaimerSeen() {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setHasSeenDisclaimer(true)
                .build()
        }
    }

    override suspend fun getRecentlyViewedIds(): List<String> {
        return context.dataStore.data.first().recentlyViewedSunnahIdsList.toList()
    }

    override suspend fun addToRecentlyViewed(sunnahId: String) {
        context.dataStore.updateData { currentPrefs ->
            val currentList = currentPrefs.recentlyViewedSunnahIdsList.toMutableList()

            // Remove if already exists to avoid duplicates
            currentList.remove(sunnahId)

            // Add to the beginning
            currentList.add(0, sunnahId)

            // Keep only the latest 30 entries
            val trimmedList = if (currentList.size > 30) {
                currentList.take(30)
            } else {
                currentList
            }

            currentPrefs.toBuilder()
                .clearRecentlyViewedSunnahIds()
                .addAllRecentlyViewedSunnahIds(trimmedList)
                .build()
        }
    }

    override suspend fun getCurrentSotd(): String {
        return context.dataStore.data.first().currentSotdId
    }

    override suspend fun updateSotdNotificationTime(time: NotificationTime) {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setSotdNotificationTime(time.ordinal)
                .build()
        }
    }

    override suspend fun updateSotdNotificationEnabled(enabled: Boolean) {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsSotdNotificationEnabled(enabled)
                .build()
        }
    }

    override suspend fun getSotdNotificationTime(): NotificationTime {
        val prefs = context.dataStore.data.first()
        return NotificationTime.entries[prefs.sotdNotificationTime]
    }

    override suspend fun isSotdNotificationEnabled(): Boolean {
        return context.dataStore.data.first().isSotdNotificationEnabled
    }

    override suspend fun updateCurrentSotd(sotdId: String, generatedDate: Long) {
        context.dataStore.updateData { currentPrefs ->
            // Add current SOTD to recently viewed if it exists
            val currentList = currentPrefs.recentlyViewedSunnahIdsList.toMutableList()
            if (currentPrefs.currentSotdId.isNotEmpty()) {
                currentList.remove(currentPrefs.currentSotdId)
                currentList.add(0, currentPrefs.currentSotdId)

                // Keep only latest 30
                val trimmedList = if (currentList.size > 30) {
                    currentList.take(30)
                } else {
                    currentList
                }

                currentPrefs.toBuilder()
                    .setCurrentSotdId(sotdId)
                    .setSotdGeneratedDate(generatedDate)
                    .setIsSotdSeen(false)
                    .clearRecentlyViewedSunnahIds()
                    .addAllRecentlyViewedSunnahIds(trimmedList)
                    .build()
            } else {
                currentPrefs.toBuilder()
                    .setCurrentSotdId(sotdId)
                    .setSotdGeneratedDate(generatedDate)
                    .setIsSotdSeen(false)
                    .build()
            }
        }
    }

    override suspend fun markSotdAsSeen() {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsSotdSeen(true)
                .build()
        }
    }

    override suspend fun markSotdAsUnseen() {
        context.dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsSotdSeen(false)
                .build()
        }
    }

    override suspend fun isSotdSeen(): Boolean {
        return context.dataStore.data.first().isSotdSeen
    }

    override suspend fun getSotdGeneratedDate(): Long {
        return context.dataStore.data.first().sotdGeneratedDate
    }

    override suspend fun shouldGenerateNewSotd(): Boolean {
        val prefs = context.dataStore.data.first()
        val today = System.currentTimeMillis()
        val generatedDate = prefs.sotdGeneratedDate

        // Check if it's a new day (compare dates, not exact time)
        return if (generatedDate == 0L) {
            true // First time
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = today
            val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
            val todayYear = calendar.get(Calendar.YEAR)

            calendar.timeInMillis = generatedDate
            val generatedDay = calendar.get(Calendar.DAY_OF_YEAR)
            val generatedYear = calendar.get(Calendar.YEAR)

            todayDay != generatedDay || todayYear != generatedYear
        }
    }

    // Flow versions
    override fun getCurrentSotdFlow(): Flow<String> {
        return context.dataStore.data.map { it.currentSotdId }
    }

    override fun isSotdSeenFlow(): Flow<Boolean> {
        return context.dataStore.data.map { it.isSotdSeen }
    }

    override fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return context.dataStore.data.map { it.toDomain() }
    }
}