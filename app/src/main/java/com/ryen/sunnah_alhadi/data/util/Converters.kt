package com.ryen.sunnah_alhadi.data.util

import androidx.room.TypeConverter
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.database.entity.Reference
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Room TypeConverters for serializing complex objects to/from database storage
 * using Kotlin Serialization
 */
class Converters {
    // Configure Json serializer with lenient settings for better stability
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true // Handle nulls more gracefully
        encodeDefaults = true // Include default values
    }

    // ContentBlock converters
    @TypeConverter
    fun fromContentBlockList(value: List<ContentBlock>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toContentBlockList(value: String): List<ContentBlock> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            try {
                json.decodeFromString(value)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Reference converters
    @TypeConverter
    fun fromReferenceList(value: List<Reference>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toReferenceList(value: String): List<Reference>? {
        return if (value.isBlank() || value == "[]") {
            null
        } else {
            try {
                json.decodeFromString(value)
            } catch (e: Exception) {
                null
            }
        }
    }

    // ExtraContent converters
    @TypeConverter
    fun fromExtraContentList(value: List<ExtraContent>?): String {
        return value?.let { json.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toExtraContentList(value: String): List<ExtraContent>? {
        return if (value.isBlank() || value == "[]") {
            null
        } else {
            try {
                json.decodeFromString(value)
            } catch (e: Exception) {
                println("Error deserializing ExtraContent list: ${e.message}")
                null
            }
        }
    }
}