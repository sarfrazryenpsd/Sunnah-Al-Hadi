package com.ryen.sunnah_alhadi.data.model

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
import com.ryen.sunnah_alhadi.domain.model.Bookmark
import com.ryen.sunnah_alhadi.domain.model.Category
import com.ryen.sunnah_alhadi.domain.model.Sunnah

fun SunnahEntity.toDomain(isBookmarked: Boolean = false): Sunnah {
    return Sunnah(
        id = this.id,
        categoryId = this.categoryId,
        title = this.title,
        body = this.body.map { it.toDomain() },
        references = this.references?.map { it.toDomain() },
        isBookmarked = isBookmarked,
        extra = this.extra?.map { it.toDomain() },
        lastSeen = null
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        topic = this.topic
    )
}

fun BookmarkEntity.toDomain(): Bookmark {
    return Bookmark(
        sunnahId = this.sunnahId,
        bookmarkedAt = this.bookmarkedAt
    )
}

// Fixed ContentBlock entity to domain mapper
fun ContentBlock.toDomain(): com.ryen.sunnah_alhadi.domain.model.ContentBlock {
    return com.ryen.sunnah_alhadi.domain.model.ContentBlock(
        type = when (this.type) {
            ContentType.ARABIC_TEXT ->
                com.ryen.sunnah_alhadi.domain.model.ContentType.ARABIC_TEXT
            ContentType.ENGLISH_TEXT ->
                com.ryen.sunnah_alhadi.domain.model.ContentType.ENGLISH_TEXT
        },
        subtype = when (this.subtype) {
            is ArabicSubtype -> this.subtype.name.lowercase()
            is EnglishSubtype -> this.subtype.name.lowercase()
            is String -> this.subtype
            else -> "unknown"
        },
        content = this.content
    )
}


fun ExtraContent.toDomain(): com.ryen.sunnah_alhadi.domain.model.ExtraContent {
    return com.ryen.sunnah_alhadi.domain.model.ExtraContent(
        type = when (this.type) {
            ExtraContentType.PARABLE -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.PARABLE
            ExtraContentType.SCHOLARLY_EXPLANATION -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.SCHOLARLY_EXPLANATION
            ExtraContentType.EXPLANATION -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.EXPLANATION
            ExtraContentType.TRANSLATION -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.TRANSLATION
            ExtraContentType.HADITH -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.HADITH
            ExtraContentType.NOTES -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.NOTES
            ExtraContentType.WARNING -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.WARNING
            ExtraContentType.BENEFIT -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.BENEFIT
        },
        content = this.content.map { it.toDomain() }
    )
}

fun Reference.toDomain(): com.ryen.sunnah_alhadi.domain.model.Reference {
    return com.ryen.sunnah_alhadi.domain.model.Reference(
        source = this.source
    )
}