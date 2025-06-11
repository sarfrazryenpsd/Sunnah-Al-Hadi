package com.ryen.sunnah_alhadi.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ryen.sunnah_alhadi.data.local.proto.ProtoUserPreferencesSerializer
import com.ryen.sunnah_alhadi.data.model.toDomain
import com.ryen.sunnah_alhadi.datastore.ProtoUserPreferences
import com.ryen.sunnah_alhadi.domain.model.UserPreferences
import com.ryen.sunnah_alhadi.domain.repository.UserPreferencesRepository
import com.ryen.sunnah_alhadi.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

    override fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return context.dataStore.data.map { it.toDomain() }
    }
}