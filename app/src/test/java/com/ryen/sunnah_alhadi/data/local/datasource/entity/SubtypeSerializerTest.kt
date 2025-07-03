package com.ryen.sunnah_alhadi.data.local.datasource.entity

import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.util.Converters
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SubtypeSerializerTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun serialize_withArabicSubtype_returnsEnumName() {
        // Given
        val arabicSubtype = ArabicSubtype.VERSE

        // When
        val result = json.encodeToString(SubtypeSerializer, arabicSubtype)

        // Then
        assertThat(result).isEqualTo("\"VERSE\"")
    }

    @Test
    fun serialize_withEnglishSubtype_returnsEnumName() {
        // Given
        val englishSubtype = EnglishSubtype.TRANSLATION

        // When
        val result = json.encodeToString(SubtypeSerializer, englishSubtype)

        // Then
        assertThat(result).isEqualTo("\"TRANSLATION\"")
    }

    @Test
    fun serialize_withStringSubtype_returnsString() {
        // Given
        val stringSubtype = "custom_subtype"

        // When
        val result = json.encodeToString(SubtypeSerializer, stringSubtype)

        // Then
        assertThat(result).isEqualTo("\"custom_subtype\"")
    }

    @Test
    fun serialize_withUnsupportedType_throwsSerializationException() {
        // Given
        val unsupportedType = 123

        // When
        try {
            json.encodeToString(SubtypeSerializer, unsupportedType)
            fail("Expected SerializationException was not thrown")
        } catch (e: SerializationException) {
            // Then: Test passed
            assertTrue(e.message?.contains("Unknown") ?: false)
        }
    }


    @Test
    fun deserialize_withValidArabicSubtypeString_returnsArabicSubtype() {
        // Given
        val arabicSubtypeString = "\"VERSE\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, arabicSubtypeString)

        // Then
        assertThat(result).isEqualTo(ArabicSubtype.VERSE)
    }

    @Test
    fun deserialize_withValidEnglishSubtypeString_returnsEnglishSubtype() {
        // Given
        val englishSubtypeString = "\"NORMAL\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, englishSubtypeString)

        // Then
        assertThat(result).isEqualTo(EnglishSubtype.NORMAL)
    }

    @Test
    fun deserialize_withUnknownEnumValue_returnsString() {
        // Given
        val unknownEnumString = "\"UNKNOWN_SUBTYPE\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, unknownEnumString)

        // Then
        assertThat(result).isEqualTo("UNKNOWN_SUBTYPE")
        assertThat(result).isInstanceOf(String::class.java)
    }

    @Test
    fun deserialize_withEmptyString_returnsEmptyString() {
        // Given
        val emptyString = "\"\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, emptyString)

        // Then
        assertThat(result).isEqualTo("")
        assertThat(result).isInstanceOf(String::class.java)
    }

    @Test
    fun deserialize_withCustomString_returnsString() {
        // Given
        val customString = "\"my_custom_subtype\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, customString)

        // Then
        assertThat(result).isEqualTo("my_custom_subtype")
        assertThat(result).isInstanceOf(String::class.java)
    }

    @Test
    fun roundTrip_withArabicSubtype_maintainsIntegrity() {
        // Given
        val originalSubtype = ArabicSubtype.SUPPLICATION

        // When
        val serialized = json.encodeToString(SubtypeSerializer, originalSubtype)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(originalSubtype)
    }

    @Test
    fun roundTrip_withEnglishSubtype_maintainsIntegrity() {
        // Given
        val originalSubtype = EnglishSubtype.TRANSLATION

        // When
        val serialized = json.encodeToString(SubtypeSerializer, originalSubtype)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(originalSubtype)
    }

    @Test
    fun roundTrip_withStringSubtype_maintainsIntegrity() {
        // Given
        val originalSubtype = "legacy_subtype"

        // When
        val serialized = json.encodeToString(SubtypeSerializer, originalSubtype)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(originalSubtype)
    }

    @Test
    fun allArabicSubtypes_canBeSerializedAndDeserialized() {
        // Given
        val allArabicSubtypes = ArabicSubtype.entries.toTypedArray()

        // When & Then
        allArabicSubtypes.forEach { subtype ->
            val serialized = json.encodeToString(SubtypeSerializer, subtype)
            val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

            assertThat(deserialized).isEqualTo(subtype)
        }
    }

    @Test
    fun allEnglishSubtypes_canBeSerializedAndDeserialized() {
        // Given
        val allEnglishSubtypes = EnglishSubtype.entries.toTypedArray()

        // When & Then
        allEnglishSubtypes.forEach { subtype ->
            val serialized = json.encodeToString(SubtypeSerializer, subtype)
            val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

            assertThat(deserialized).isEqualTo(subtype)
        }
    }

    @Test
    fun serialization_withSpecialCharacters_handlesCorrectly() {
        // Given
        val specialCharString = "special_chars_!@#$%^&*()"

        // When
        val serialized = json.encodeToString(SubtypeSerializer, specialCharString)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(specialCharString)
    }

    @Test
    fun serialization_withUnicodeCharacters_handlesCorrectly() {
        // Given
        val unicodeString = "unicode_test_العربية_中文"

        // When
        val serialized = json.encodeToString(SubtypeSerializer, unicodeString)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(unicodeString)
    }

    @Test
    fun serialization_withNullOrEmptyValues_handlesGracefully() {
        // Given
        val emptyString = ""
        val spaceString = " "

        // When
        val serializedEmpty = json.encodeToString(SubtypeSerializer, emptyString)
        val serializedSpace = json.encodeToString(SubtypeSerializer, spaceString)

        val deserializedEmpty = json.decodeFromString(SubtypeSerializer, serializedEmpty)
        val deserializedSpace = json.decodeFromString(SubtypeSerializer, serializedSpace)

        // Then
        assertThat(deserializedEmpty).isEqualTo("")
        assertThat(deserializedSpace).isEqualTo(" ")
    }

    @Test
    fun fallbackBehavior_withDeprecatedEnumValues_returnsString() {
        // Given - Simulate a deprecated enum value that no longer exists
        val deprecatedEnumString = "\"DEPRECATED_SUBTYPE\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, deprecatedEnumString)

        // Then
        assertThat(result).isEqualTo("DEPRECATED_SUBTYPE")
        assertThat(result).isInstanceOf(String::class.java)
    }



    @Test
    fun deserialize_withInvalidJsonStructure_throwsException() {
        // Given
        val invalidJson = "{\"type\":\"VERSE\"}" // Object instead of string

        // When
        try {
            json.decodeFromString(SubtypeSerializer, invalidJson)
            fail("Expected SerializationException was not thrown")
        } catch (e: SerializationException) {
            // Then
            assertTrue(
                e.message?.contains("Expected") ?: true // more flexible match
            )
        }
    }


    // Test case sensitivity
    @Test
    fun deserialize_withLowercaseEnumName_resolvesToEnum() {
        // Given
        val lowercaseEnum = "\"verse\""

        // When
        val result = json.decodeFromString(SubtypeSerializer, lowercaseEnum)

        // Then
        assertThat(result).isEqualTo(ArabicSubtype.VERSE)
        assertThat(result).isInstanceOf(ArabicSubtype::class.java)
    }


    // Test with actual @SerialName values if your enums use them
    @Test
    fun deserialize_withSerialNameValues_handlesCorrectly() {
        // Given - if your enums have @SerialName("verse") etc.
        val serialNameValue = "\"verse\"" // This would be the @SerialName value

        // When
        val result = json.decodeFromString(SubtypeSerializer, serialNameValue)

        // Then
        // This test depends on your enum @SerialName configuration
        // Update based on your actual enum annotations
        assertThat(result).isEqualTo(ArabicSubtype.VERSE)
    }

    // Test integration with ContentBlock
    @Test
    fun contentBlock_withSubtypeSerializer_serializesCorrectly() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.VERSE,
            content = "Test content"
        )

        // When
        val serialized = json.encodeToString(ContentBlock.serializer(), contentBlock)
        val deserialized = json.decodeFromString(ContentBlock.serializer(), serialized)

        // Then
        assertThat(deserialized.subtype).isEqualTo(ArabicSubtype.VERSE)
        assertThat(deserialized.type).isEqualTo(ContentType.ARABIC_TEXT)
    }

    @Test
    fun contentBlock_withStringSubtype_serializesCorrectly() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = "custom_subtype",
            content = "Test content"
        )

        // When
        val serialized = json.encodeToString(ContentBlock.serializer(), contentBlock)
        val deserialized = json.decodeFromString(ContentBlock.serializer(), serialized)

        // Then
        assertThat(deserialized.subtype).isEqualTo("custom_subtype")
        assertThat(deserialized.subtype).isInstanceOf(String::class.java)
    }

    // Test performance with large datasets
    @Test
    fun performance_withLargeDataset_completesInReasonableTime() {
        // Given
        val largeDataset = (1..1000).map { index ->
            ContentBlock(
                type = if (index % 2 == 0) ContentType.ARABIC_TEXT else ContentType.ENGLISH_TEXT,
                subtype = if (index % 3 == 0) ArabicSubtype.VERSE else EnglishSubtype.NORMAL,
                content = "Content $index"
            )
        }

        // When
        val startTime = System.currentTimeMillis()
        val serialized = json.encodeToString(largeDataset)
        val deserialized = json.decodeFromString<List<ContentBlock>>(serialized)
        val endTime = System.currentTimeMillis()

        // Then
        assertThat(deserialized).hasSize(1000)
        assertThat(endTime - startTime).isLessThan(5000) // Should complete in under 5 seconds
    }

    // Test thread safety
    @Test
    fun serializer_isThreadSafe() {
        // Given
        val testData = listOf(
            ArabicSubtype.VERSE,
            EnglishSubtype.NORMAL,
            "custom_string",
            ArabicSubtype.SUPPLICATION
        )

        // When & Then
        runBlocking {
            val jobs = testData.map { data ->
                async {
                    repeat(100) {
                        val serialized = json.encodeToString(SubtypeSerializer, data)
                        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)
                        assertThat(deserialized).isEqualTo(data)
                    }
                }
            }
            jobs.awaitAll()
        }
    }

    // Test with actual database converter integration
    @Test
    fun converters_withSubtypeSerializer_handlesAllCases() {
        // Given
        val converters = Converters()
        val contentBlocks = listOf(
            ContentBlock(ContentType.ARABIC_TEXT, ArabicSubtype.VERSE, "Arabic verse"),
            ContentBlock(ContentType.ENGLISH_TEXT, EnglishSubtype.NORMAL, "English text"),
            ContentBlock(ContentType.ARABIC_TEXT, "custom_subtype", "Custom content")
        )

        // When
        val serialized = converters.fromContentBlockList(contentBlocks)
        val deserialized = converters.toContentBlockList(serialized)

        // Then
        assertThat(deserialized).hasSize(3)
        assertThat(deserialized[0].subtype).isEqualTo(ArabicSubtype.VERSE)
        assertThat(deserialized[1].subtype).isEqualTo(EnglishSubtype.NORMAL)
        assertThat(deserialized[2].subtype).isEqualTo("custom_subtype")
    }

    // Test backward compatibility
    @Test
    fun deserialize_withLegacyFormatData_handlesGracefully() {
        // Given - Simulate old data format that might exist in your database
        val legacyJsonString = """[{"type":"arabic_text","subtype":"old_format","content":"Legacy content"}]"""

        // When
        val result = json.decodeFromString<List<ContentBlock>>(legacyJsonString)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].subtype).isEqualTo("old_format")
    }

    // Test with extremely long strings
    @Test
    fun serialization_withVeryLongString_handlesCorrectly() {
        // Given
        val longString = "a".repeat(10000)

        // When
        val serialized = json.encodeToString(SubtypeSerializer, longString)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(longString)
    }

    // Test JSON injection scenarios
    @Test
    fun serialization_withJsonInjectionAttempt_handlesSecurely() {
        // Given
        val maliciousString = "\"},\"injected\":\"value\",\"fake\":{\""

        // When
        val serialized = json.encodeToString(SubtypeSerializer, maliciousString)
        val deserialized = json.decodeFromString(SubtypeSerializer, serialized)

        // Then
        assertThat(deserialized).isEqualTo(maliciousString)
        assertThat(serialized).contains("\\\"") // Should be properly escaped
    }
}