
package com.ryen.sunnah_alhadi.data.local.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

data class UserPreferencesProto(
    val username: String = "",
    val themeMode: Int = 0, // 0=SYSTEM, 1=LIGHT, 2=DARK
    val isDynamicThemeEnabled: Boolean = true,
    val isDailyReminderEnabled: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val hasSeenDisclaimer: Boolean = false,
    val recentlyViewedSunnahIds: List<String> = emptyList()
)

// Serializer for Proto DataStore
object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}


