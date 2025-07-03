package com.ryen.sunnah_alhadi.data.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.datasource.entity.Reference
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ExtraContentType
import org.junit.Before

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setup() {
        converters = Converters()
    }

    @Test
    fun fromContentBlockList_withValidList_returnsJsonString() {
        // Given
        val contentBlocks = listOf(
            ContentBlock(
                type = ContentType.ARABIC_TEXT,
                subtype = ArabicSubtype.VERSE,
                content = "بسم الله الرحمن الرحيم"
            ),
            ContentBlock(
                type = ContentType.ENGLISH_TEXT,
                subtype = EnglishSubtype.TRANSLATION,
                content = "In the name of Allah"
            )
        )

        // When
        val result = converters.fromContentBlockList(contentBlocks)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result).contains("بسم الله الرحمن الرحيم")
        assertThat(result).contains("In the name of Allah")
    }

    @Test
    fun fromContentBlockList_withNullList_returnsEmptyArray() {
        // Given
        val contentBlocks: List<ContentBlock>? = null

        // When
        val result = converters.fromContentBlockList(contentBlocks)

        // Then
        assertThat(result).isEqualTo("[]")
    }

    @Test
    fun fromContentBlockList_withEmptyList_returnsEmptyArray() {
        // Given
        val contentBlocks = emptyList<ContentBlock>()

        // When
        val result = converters.fromContentBlockList(contentBlocks)

        // Then
        assertThat(result).isEqualTo("[]")
    }

    fun toContentBlockList_withValidJsonString_returnsContentBlockList() {
        // Given - Updated to use @SerialName format
        val jsonString = """[{"type":"arabic_text","subtype":"VERSE","content":"Test content"}]"""

        // When
        val result = converters.toContentBlockList(jsonString)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().content).isEqualTo("Test content")
        assertThat(result.first().type).isEqualTo(ContentType.ARABIC_TEXT)
    }

    // Additional test to verify enum serialization
    @Test
    fun contentBlock_serialization_usesCorrectFormat() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.VERSE,
            content = "Test content"
        )

        // When
        val result = converters.fromContentBlockList(listOf(contentBlock))

        // Then
        assertThat(result).contains("arabic_text")
        assertThat(result).contains("VERSE")
    }

    @Test
    fun toContentBlockList_withBlankString_returnsEmptyList() {
        // Given
        val jsonString = ""

        // When
        val result = converters.toContentBlockList(jsonString)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun toContentBlockList_withMalformedJson_returnsEmptyList() {
        // Given
        val malformedJson = "{"

        // When
        val result = converters.toContentBlockList(malformedJson)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun toContentBlockList_withInvalidJsonStructure_returnsEmptyList() {
        // Given
        val invalidJson = """{"invalid": "structure"}"""

        // When
        val result = converters.toContentBlockList(invalidJson)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun fromReferenceList_withValidList_returnsJsonString() {
        // Given
        val references = listOf(
            Reference("Sahih Bukhari 1234"),
            Reference("Sahih Muslim 5678")
        )

        // When
        val result = converters.fromReferenceList(references)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result).contains("Sahih Bukhari 1234")
        assertThat(result).contains("Sahih Muslim 5678")
    }

    @Test
    fun fromReferenceList_withNullList_returnsEmptyArray() {
        // Given
        val references: List<Reference>? = null

        // When
        val result = converters.fromReferenceList(references)

        // Then
        assertThat(result).isEqualTo("[]")
    }

    @Test
    fun toReferenceList_withValidJsonString_returnsReferenceList() {
        // Given
        val jsonString = """[{"source":"Test Reference"}]"""

        // When
        val result = converters.toReferenceList(jsonString)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).hasSize(1)
        assertThat(result!!.first().source).isEqualTo("Test Reference")
    }

    @Test
    fun toReferenceList_withEmptyArrayString_returnsNull() {
        // Given
        val jsonString = "[]"

        // When
        val result = converters.toReferenceList(jsonString)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun toReferenceList_withBlankString_returnsNull() {
        // Given
        val jsonString = ""

        // When
        val result = converters.toReferenceList(jsonString)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun toReferenceList_withMalformedJson_returnsNull() {
        // Given
        val malformedJson = "{"

        // When
        val result = converters.toReferenceList(malformedJson)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun fromExtraContentList_withValidList_returnsJsonString() {
        // Given
        val extraContent = listOf(
            ExtraContent(
                type = ExtraContentType.EXPLANATION,
                content = listOf(
                    ContentBlock(
                        type = ContentType.ENGLISH_TEXT,
                        subtype = EnglishSubtype.NORMAL,
                        content = "This is an explanation"
                    )
                )
            )
        )

        // When
        val result = converters.fromExtraContentList(extraContent)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result).contains("This is an explanation")
        // Updated to expect @SerialName format
        assertThat(result).contains("explanation")
        assertThat(result).contains("english_text")
    }

    @Test
    fun fromExtraContentList_withNullList_returnsEmptyArray() {
        // Given
        val extraContent: List<ExtraContent>? = null

        // When
        val result = converters.fromExtraContentList(extraContent)

        // Then
        assertThat(result).isEqualTo("[]")
    }

    @Test
    fun toExtraContentList_withValidJsonString_returnsExtraContentList() {
        // Given - Updated to use @SerialName format
        val jsonString = """[{"type":"explanation","content":[{"type":"english_text","subtype":"NORMAL","content":"Test"}]}]"""

        // When
        val result = converters.toExtraContentList(jsonString)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).hasSize(1)
        assertThat(result!!.first().type).isEqualTo(ExtraContentType.EXPLANATION)
    }

    @Test
    fun toExtraContentList_withEmptyArrayString_returnsNull() {
        // Given
        val jsonString = "[]"

        // When
        val result = converters.toExtraContentList(jsonString)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun toExtraContentList_withBlankString_returnsNull() {
        // Given
        val jsonString = ""

        // When
        val result = converters.toExtraContentList(jsonString)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun toExtraContentList_withMalformedJson_returnsNull() {
        // Given
        val malformedJson = "{"

        // When
        val result = converters.toExtraContentList(malformedJson)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun toExtraContentList_withInvalidJsonStructure_returnsNull() {
        // Given
        val invalidJson = """{"invalid": "structure"}"""

        // When
        val result = converters.toExtraContentList(invalidJson)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun jsonSerialization_withComplexNestedStructure_maintainsIntegrity() {
        // Given
        val complexContent = listOf(
            ExtraContent(
                type = ExtraContentType.SCHOLARLY_EXPLANATION,
                content = listOf(
                    ContentBlock(
                        type = ContentType.ARABIC_TEXT,
                        subtype = ArabicSubtype.VERSE,
                        content = "Arabic text with special chars: اللهم صل على محمد"
                    ),
                    ContentBlock(
                        type = ContentType.ENGLISH_TEXT,
                        subtype = EnglishSubtype.TRANSLATION,
                        content = "English translation with quotes: \"Peace be upon Muhammad\""
                    )
                )
            ),
            ExtraContent(
                type = ExtraContentType.HADITH,
                content = listOf(
                    ContentBlock(
                        type = ContentType.ENGLISH_TEXT,
                        subtype = EnglishSubtype.NORMAL,
                        content = "Hadith text with newlines\nand special characters: & < > \""
                    )
                )
            )
        )

        // When
        val serialized = converters.fromExtraContentList(complexContent)
        val deserialized = converters.toExtraContentList(serialized)

        // Then
        assertThat(deserialized).isNotNull()
        assertThat(deserialized).hasSize(2)
        assertThat(deserialized!!.first().type).isEqualTo(ExtraContentType.SCHOLARLY_EXPLANATION)
        assertThat(deserialized.first().content).hasSize(2)
        assertThat(deserialized.first().content.first().content).contains("اللهم صل على محمد")
        assertThat(deserialized[1].content.first().content).contains("newlines\nand special characters")
    }

    @Test
    fun jsonSerialization_withEmptyContentInBlocks_handlesGracefully() {
        // Given
        val contentWithEmptyStrings = listOf(
            ContentBlock(
                type = ContentType.ENGLISH_TEXT,
                subtype = EnglishSubtype.NORMAL,
                content = ""
            ),
            ContentBlock(
                type = ContentType.ARABIC_TEXT,
                subtype = ArabicSubtype.OTHER,
                content = "   "
            )
        )

        // When
        val serialized = converters.fromContentBlockList(contentWithEmptyStrings)
        val deserialized = converters.toContentBlockList(serialized)

        // Then
        assertThat(deserialized).hasSize(2)
        assertThat(deserialized.first().content).isEmpty()
        assertThat(deserialized[1].content).isEqualTo("   ")
    }

    @Test
    fun jsonSerialization_withNullFields_handlesGracefully() {
        // Given
        val references = listOf(Reference("Test Reference"))
        val extraContent: List<ExtraContent>? = null

        // When
        val serializedReferences = converters.fromReferenceList(references)
        val serializedExtra = converters.fromExtraContentList(extraContent)

        val deserializedReferences = converters.toReferenceList(serializedReferences)
        val deserializedExtra = converters.toExtraContentList(serializedExtra)

        // Then
        assertThat(deserializedReferences).isNotNull()
        assertThat(deserializedReferences).hasSize(1)
        assertThat(deserializedExtra).isNull()
    }
}