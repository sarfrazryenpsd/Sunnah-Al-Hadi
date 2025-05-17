package com.ryen.sunnah_alhadi.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "sunnahs",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = CASCADE
    )],
    indices = [Index("categoryId")]
)
data class SunnahEntity(
    @PrimaryKey val id: String, // e.g., "00_05"
    val categoryId: Int,
    val title: String,
    val body: List<ContentBlock>,
    val references: List<Reference>?,  // optional
    val extra: List<ExtraContent>?     // optional
)

@Serializable
data class ContentBlock(
    val type: ContentType,       // "arabic_text" or "english_text"
    val subtype: String,    // e.g., "verse", "normal"
    val content: String
)

@Serializable
data class ExtraContent(
    val title: String,
    val content: List<ContentBlock>
)

@Serializable
data class Reference(
    val text: String
)

@Serializable
enum class ContentType {
    ARABIC_TEXT,
    ENGLISH_TEXT
}

@Serializable
enum class ArabicSubtype {
    VERSE,
    SUPPLICATION,
    HONORIFIC,
    OTHER
}

@Serializable
enum class EnglishSubtype {
    NORMAL,
    TRANSLATION
}