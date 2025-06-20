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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class UserPreferencesRepositoryImpl (
    private val context: Context
) : UserPreferencesRepository {

    private val Context.dataStore: DataStore<ProtoUserPreferences> by dataStore(
        fileName = "user_preferences.pb",
        serializer = ProtoUserPreferencesSerializer
    )

    // Cache frequently accessed data
    private val _userPreferencesFlow = context.dataStore.data.map { it.toDomain() }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    override suspend fun getUserPreferences(): UserPreferences {
        return _userPreferencesFlow.filterNotNull().first()
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

    // Optimized recently viewed management
    override suspend fun addToRecentlyViewed(sunnahId: String) {
        context.dataStore.updateData { currentPrefs ->
            val currentList = currentPrefs.recentlyViewedSunnahIdsList.toMutableList()

            // More efficient approach
            currentList.removeAll { it == sunnahId } // Remove all occurrences
            currentList.add(0, sunnahId) // Add to beginning

            // Use take instead of manual size check
            val trimmedList = currentList.take(RECENTLY_VIEWED_LIMIT)

            currentPrefs.toBuilder()
                .clearRecentlyViewedSunnahIds()
                .addAllRecentlyViewedSunnahIds(trimmedList)
                .build()
        }
    }

    override suspend fun getCurrentSotd(): String {
        return _userPreferencesFlow.filterNotNull().first().currentSotdId
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
        return NotificationTime.entries.getOrElse(prefs.sotdNotificationTime) {
            NotificationTime.MORNING
        }
    }

    override suspend fun isSotdNotificationEnabled(): Boolean {
        return context.dataStore.data.first().isSotdNotificationEnabled
    }

    // FIXED: Simplified and optimized SOTD update
    override suspend fun updateCurrentSotd(sotdId: String, generatedDate: Long) {
        context.dataStore.updateData { currentPrefs ->
            val currentList = currentPrefs.recentlyViewedSunnahIdsList.toMutableList()

            // Add previous SOTD to recently viewed if it exists and is not empty
            val previousSotdId = currentPrefs.currentSotdId
            if (previousSotdId.isNotEmpty()) {
                currentList.removeAll { it == previousSotdId }
                currentList.add(0, previousSotdId)
            }

            // Keep only latest entries
            val trimmedList = currentList.take(RECENTLY_VIEWED_LIMIT)

            currentPrefs.toBuilder()
                .setCurrentSotdId(sotdId)
                .setSotdGeneratedDate(generatedDate)
                .setIsSotdSeen(false)
                .clearRecentlyViewedSunnahIds()
                .addAllRecentlyViewedSunnahIds(trimmedList)
                .build()
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

    // OPTIMIZED: Use more efficient date comparison
    override suspend fun shouldGenerateNewSotd(): Boolean {
        val prefs = context.dataStore.data.first()
        val generatedDate = prefs.sotdGeneratedDate

        if (generatedDate == 0L) return true // First time

        val currentDate = LocalDate.now()
        val generatedLocalDate = Instant.ofEpochMilli(generatedDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return currentDate != generatedLocalDate
    }

    // Flow versions with caching
    override fun getCurrentSotdFlow(): Flow<String> {
        return context.dataStore.data.map { it.currentSotdId }.distinctUntilChanged()
    }

    override fun isSotdSeenFlow(): Flow<Boolean> {
        return context.dataStore.data.map { it.isSotdSeen }.distinctUntilChanged()
    }

    override fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return _userPreferencesFlow.filterNotNull()
    }

    companion object {
        private const val RECENTLY_VIEWED_LIMIT = 30
    }
}