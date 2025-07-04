package com.ryen.sunnah_alhadi.data.model

import com.google.common.truth.Truth.assertThat
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ExtraContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.Reference
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahWithBookmark
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SunnahMappersTest {

    @Test
    fun sunnah_entity_to_domain_should_map_correctly_with_default_values() {
        // Given
        val contentBlocks = listOf(
            ContentBlock(
                type = ContentType.ARABIC_TEXT,
                subtype = ArabicSubtype.VERSE,
                content = "Arabic content"
            ),
            ContentBlock(
                type = ContentType.ENGLISH_TEXT,
                subtype = EnglishSubtype.TRANSLATION,
                content = "English translation"
            )
        )

        val references = listOf(Reference("Sahih Bukhari"))
        val extraContent = listOf(
            ExtraContent(
                type = ExtraContentType.EXPLANATION,
                content = contentBlocks
            )
        )

        val sunnahEntity = SunnahEntity(
            id = "01_05",
            categoryId = 1,
            title = "Test Sunnah",
            body = contentBlocks,
            references = references,
            extra = extraContent
        )

        // When
        val domain = sunnahEntity.toDomain()

        // Then
        assertThat(domain.id).isEqualTo("01_05")
        assertThat(domain.categoryId).isEqualTo(1)
        assertThat(domain.title).isEqualTo("Test Sunnah")
        assertThat(domain.body).hasSize(2)
        assertThat(domain.references).hasSize(1)
        assertThat(domain.extra).hasSize(1)
        assertThat(domain.isBookmarked).isFalse()
        assertThat(domain.bookmarkedAt).isNull()
    }

    @Test
    fun sunnah_entity_to_domain_should_map_with_bookmark_info() {
        // Given
        val sunnahEntity = SunnahEntity(
            id = "01_05",
            categoryId = 1,
            title = "Test Sunnah",
            body = emptyList(),
            references = null,
            extra = null
        )
        val bookmarkedAt = System.currentTimeMillis()

        // When
        val domain = sunnahEntity.toDomain(isBookmarked = true, bookmarkedAt = bookmarkedAt)

        // Then
        assertThat(domain.isBookmarked).isTrue()
        assertThat(domain.bookmarkedAt).isEqualTo(bookmarkedAt)
    }

    @Test
    fun sunnah_entity_to_domain_should_handle_null_optional_fields() {
        // Given
        val sunnahEntity = SunnahEntity(
            id = "01_05",
            categoryId = 1,
            title = "Test Sunnah",
            body = emptyList(),
            references = null,
            extra = null
        )

        // When
        val domain = sunnahEntity.toDomain()

        // Then
        assertThat(domain.references).isNull()
        assertThat(domain.extra).isNull()
        assertThat(domain.body).isEmpty()
    }

    @Test
    fun sunnah_with_bookmark_to_domain_should_map_correctly() {
        // Given
        val sunnahEntity = SunnahEntity(
            id = "01_05",
            categoryId = 1,
            title = "Test Sunnah",
            body = emptyList(),
            references = null,
            extra = null
        )
        val bookmarkedAt = System.currentTimeMillis()
        val sunnahWithBookmark = SunnahWithBookmark(
            sunnah = sunnahEntity,
            isBookmarked = true,
            bookmarkedAt = bookmarkedAt
        )

        // When
        val domain = sunnahWithBookmark.toDomain()

        // Then
        assertThat(domain.id).isEqualTo("01_05")
        assertThat(domain.isBookmarked).isTrue()
        assertThat(domain.bookmarkedAt).isEqualTo(bookmarkedAt)
    }

    @Test
    fun category_entity_to_domain_should_map_correctly() {
        // Given
        val categoryEntity = CategoryEntity(
            id = 1,
            topic = "Prayer"
        )

        // When
        val domain = categoryEntity.toDomain()

        // Then
        assertThat(domain.id).isEqualTo(1)
        assertThat(domain.topic).isEqualTo("Prayer")
    }

    @Test
    fun bookmark_entity_to_domain_should_map_correctly() {
        // Given
        val bookmarkedAt = System.currentTimeMillis()
        val bookmarkEntity = BookmarkEntity(
            sunnahId = "01_05",
            bookmarkedAt = bookmarkedAt
        )

        // When
        val domain = bookmarkEntity.toDomain()

        // Then
        assertThat(domain.sunnahId).isEqualTo("01_05")
        assertThat(domain.bookmarkedAt).isEqualTo(bookmarkedAt)
    }

    @Test
    fun content_block_to_domain_should_map_arabic_subtype() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.VERSE,
            content = "Arabic verse"
        )

        // When
        val domain = contentBlock.toDomain()

        // Then
        assertThat(domain.type).isEqualTo(com.ryen.sunnah_alhadi.domain.model.ContentType.ARABIC_TEXT)
        assertThat(domain.subtype).isEqualTo("verse")
        assertThat(domain.content).isEqualTo("Arabic verse")
    }

    @Test
    fun content_block_to_domain_should_map_english_subtype() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL,
            content = "English content"
        )

        // When
        val domain = contentBlock.toDomain()

        // Then
        assertThat(domain.type).isEqualTo(com.ryen.sunnah_alhadi.domain.model.ContentType.ENGLISH_TEXT)
        assertThat(domain.subtype).isEqualTo("normal")
        assertThat(domain.content).isEqualTo("English content")
    }

    @Test
    fun content_block_to_domain_should_map_string_subtype() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = "custom_subtype",
            content = "Custom content"
        )

        // When
        val domain = contentBlock.toDomain()

        // Then
        assertThat(domain.subtype).isEqualTo("custom_subtype")
    }

    @Test
    fun content_block_to_domain_should_handle_unknown_subtype() {
        // Given
        val contentBlock = ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = mockk<Any>(), // Unknown type
            content = "Content"
        )

        // When
        val domain = contentBlock.toDomain()

        // Then
        assertThat(domain.subtype).isEqualTo("unknown")
    }

    @Test
    fun extra_content_to_domain_should_map_all_types() {
        // Given
        val contentBlocks = listOf(
            ContentBlock(
                type = ContentType.ENGLISH_TEXT,
                subtype = EnglishSubtype.NORMAL,
                content = "Explanation content"
            )
        )

        val extraContentTypes = listOf(
            ExtraContentType.PARABLE,
            ExtraContentType.SCHOLARLY_EXPLANATION,
            ExtraContentType.EXPLANATION,
            ExtraContentType.TRANSLATION,
            ExtraContentType.HADITH,
            ExtraContentType.NOTES,
            ExtraContentType.WARNING,
            ExtraContentType.BENEFIT
        )

        extraContentTypes.forEach { type ->
            // Given
            val extraContent = ExtraContent(
                type = type,
                content = contentBlocks
            )

            // When
            val domain = extraContent.toDomain()

            // Then
            assertThat(domain.content).hasSize(1)
            assertThat(domain.type).isNotNull()
        }
    }

    @Test
    fun reference_to_domain_should_map_correctly() {
        // Given
        val reference = Reference("Sahih Muslim")

        // When
        val domain = reference.toDomain()

        // Then
        assertThat(domain.source).isEqualTo("Sahih Muslim")
    }

    @Test
    fun mapping_should_handle_empty_collections() {
        // Given
        val sunnahEntity = SunnahEntity(
            id = "01_05",
            categoryId = 1,
            title = "Test Sunnah",
            body = emptyList(),
            references = emptyList(),
            extra = emptyList()
        )

        // When
        val domain = sunnahEntity.toDomain()

        // Then
        assertThat(domain.body).isEmpty()
        assertThat(domain.references).isEmpty()
        assertThat(domain.extra).isEmpty()
    }

    @Test
    fun mapping_should_preserve_complex_nested_structures() {
        // Given
        val nestedContentBlocks = listOf(
            ContentBlock(
                type = ContentType.ARABIC_TEXT,
                subtype = ArabicSubtype.SUPPLICATION,
                content = "Dua content"
            ),
            ContentBlock(
                type = ContentType.ENGLISH_TEXT,
                subtype = EnglishSubtype.TRANSLATION,
                content = "Translation content"
            )
        )

        val extraContent = listOf(
            ExtraContent(
                type = ExtraContentType.EXPLANATION,
                content = nestedContentBlocks
            ),
            ExtraContent(
                type = ExtraContentType.BENEFIT,
                content = listOf(
                    ContentBlock(
                        type = ContentType.ENGLISH_TEXT,
                        subtype = EnglishSubtype.NORMAL,
                        content = "Benefit explanation"
                    )
                )
            )
        )

        val sunnahEntity = SunnahEntity(
            id = "01_05",
            categoryId = 1,
            title = "Complex Sunnah",
            body = nestedContentBlocks,
            references = listOf(Reference("Source 1"), Reference("Source 2")),
            extra = extraContent
        )

        // When
        val domain = sunnahEntity.toDomain()

        // Then
        assertThat(domain.body).hasSize(2)
        assertThat(domain.references).hasSize(2)
        assertThat(domain.extra).hasSize(2)
        assertThat(domain.extra!![0].content).hasSize(2)
        assertThat(domain.extra[1].content).hasSize(1)
    }
}