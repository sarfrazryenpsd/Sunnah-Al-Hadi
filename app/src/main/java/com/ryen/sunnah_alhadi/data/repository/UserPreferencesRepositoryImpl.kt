package com.ryen.sunnah_alhadi.data.repository

import androidx.datastore.core.DataStore
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.di.ApplicationScope
import com.ryen.sunnah_alhadi.domain.model.NotificationTime
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<ProtoUserPreferences>,
    @param:ApplicationScope private val applicationScope: CoroutineScope
) : UserPreferencesRepository{

    // ✅ Create a default UserPreferences for initial state
    private val defaultUserPreferences = UserPreferences(
        username = "",
        themeMode = 0, // SYSTEM
        isDynamicThemeEnabled = false,
        isDailyReminderEnabled = true,
        hasCompletedOnboarding = false,
        hasSeenDisclaimer = false,
        recentlyViewedSunnahIds = emptyList(),
        currentSotdId = "",
        sotdGeneratedDate = 0L,
        isSotdSeen = false,
        sotdNotificationTime = NotificationTime.MORNING,
        isSotdNotificationEnabled = true
    )
    // Cache frequently accessed data
    private val _userPreferencesFlow: StateFlow<UserPreferences> = dataStore.data
        .map { protoPrefs ->
            try {
                protoPrefs.toDomain()
            } catch (e: Exception) {
                // Return default if parsing fails
                defaultUserPreferences
            }
        }
        .catch { exception ->
            // Emit default preferences on any error
            emit(defaultUserPreferences)
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Lazily, // ✅ Keep alive once started
            initialValue = defaultUserPreferences // ✅ Never null
        )

    override suspend fun getUserPreferences(): UserPreferences {
        return _userPreferencesFlow.first()
    }

    override suspend fun updateUsername(username: String) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setUsername(username)
                .build()
        }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setThemeMode(themeMode.ordinal)
                .build()
        }
    }

    override suspend fun updateDynamicTheme(enabled: Boolean) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsDynamicThemeEnabled(enabled)
                .build()
        }
    }

    override suspend fun updateDailyReminder(enabled: Boolean) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsDailyReminderEnabled(enabled)
                .build()
        }
    }

    override suspend fun markOnboardingCompleted() {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setHasCompletedOnboarding(true)
                .build()
        }
    }

    override suspend fun markDisclaimerSeen() {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setHasSeenDisclaimer(true)
                .build()
        }
    }

    override suspend fun getRecentlyViewedIds(): List<String> {
        return _userPreferencesFlow.first().recentlyViewedSunnahIds
    }

    // Optimized recently viewed management
    override suspend fun addToRecentlyViewed(sunnahId: String) {
        dataStore.updateData { currentPrefs ->
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
        return _userPreferencesFlow.first().currentSotdId
    }

    override suspend fun updateSotdNotificationTime(time: NotificationTime) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setSotdNotificationTime(time.ordinal)
                .build()
        }
    }

    override suspend fun updateSotdNotificationEnabled(enabled: Boolean) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsSotdNotificationEnabled(enabled)
                .build()
        }
    }

    override suspend fun getSotdNotificationTime(): NotificationTime {
        val prefs = _userPreferencesFlow.first()
        return NotificationTime.entries.getOrElse(prefs.sotdNotificationTime.ordinal) {
            NotificationTime.MORNING
        }
    }

    override suspend fun isSotdNotificationEnabled(): Boolean {
        return _userPreferencesFlow.first().isSotdNotificationEnabled
    }

    // FIXED: Simplified and optimized SOTD update
    override suspend fun updateCurrentSotd(sotdId: String, generatedDate: Long) {
        dataStore.updateData { currentPrefs ->
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
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsSotdSeen(true)
                .build()
        }
    }

    override suspend fun markSotdAsUnseen() {
        dataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setIsSotdSeen(false)
                .build()
        }
    }

    override suspend fun isSotdSeen(): Boolean {
        return _userPreferencesFlow.first().isSotdSeen
    }

    override suspend fun getSotdGeneratedDate(): Long {
        return _userPreferencesFlow.first().sotdGeneratedDate
    }

    // OPTIMIZED: Use more efficient date comparison
    override suspend fun shouldGenerateNewSotd(): Boolean {
        val prefs = _userPreferencesFlow.first()
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
        return _userPreferencesFlow.map { it.currentSotdId }.distinctUntilChanged()
    }

    override fun isSotdSeenFlow(): Flow<Boolean> {
        return _userPreferencesFlow.map { it.isSotdSeen }.distinctUntilChanged()
    }

    override fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return _userPreferencesFlow
    }

    companion object {
        private const val RECENTLY_VIEWED_LIMIT = 30
    }
}