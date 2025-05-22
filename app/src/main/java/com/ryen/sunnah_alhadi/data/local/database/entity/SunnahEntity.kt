package com.ryen.sunnah_alhadi.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

object SubtypeSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Subtype", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        when (value) {
            is ArabicSubtype -> encoder.encodeString(value.name)
            is EnglishSubtype -> encoder.encodeString(value.name)
            is String -> encoder.encodeString(value)
            else -> throw SerializationException("Unknown subtype format")
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        val value = decoder.decodeString()
        // Try to parse as enums, fallback to string
        return try {
            ArabicSubtype.valueOf(value)
        } catch (e: IllegalArgumentException) {
            try {
                EnglishSubtype.valueOf(value)
            } catch (e: IllegalArgumentException) {
                value // Fallback to raw string
            }
        }
    }
}

@Serializable
data class ContentBlock(
    val type: ContentType,       // "arabic_text" or "english_text"
    @Serializable(with = SubtypeSerializer::class)
    val subtype: Any,    // e.g., "verse", "normal"
    val content: String
)


@Serializable
data class ExtraContent(
    val type: ExtraContentType,
    val content: List<ContentBlock>
)

@Serializable
data class Reference(
    val source: String
)

@Serializable
enum class ContentType {
    @SerialName("arabic_text")
    ARABIC_TEXT,

    @SerialName("english_text")
    ENGLISH_TEXT
}


@Serializable
enum class ArabicSubtype {
    @SerialName("verse")
    VERSE,
    @SerialName("supplication")
    SUPPLICATION,
    @SerialName("honorifics")
    HONORIFIC,
    @SerialName("other")
    OTHER
}


@Serializable
enum class EnglishSubtype {
    @SerialName("normal")
    NORMAL,
    @SerialName("translation")
    TRANSLATION
}

@Serializable
enum class ExtraContentType {
    @SerialName("parable")
    PARABLE,

    @SerialName("scholarly_explanation")
    SCHOLARLY_EXPLANATION,

    @SerialName("explanation")
    EXPLANATION,

    @SerialName("translation")
    TRANSLATION,

    @SerialName("hadith")
    HADITH,

    @SerialName("note")
    NOTES,

    @SerialName("warning")
    WARNING,

    @SerialName("benefit")
    BENEFIT
}
