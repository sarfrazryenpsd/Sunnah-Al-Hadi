package com.ryen.sunnah_alhadi.data.model

import com.ryen.sunnah_alhadi.data.local.datasource.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BookmarkEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.CategoryEntity
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.datasource.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.datasource.entity.EnglishSubtype
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
        lastSeen = null // Will be handled by repository if needed
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

// ContentBlock entity to domain mapper
fun ContentBlock.toDomain(): com.ryen.sunnah_alhadi.domain.model.ContentBlock {
    return com.ryen.sunnah_alhadi.domain.model.ContentBlock(
        type = when (this.type) {
            ContentType.ARABIC_TEXT -> com.ryen.sunnah_alhadi.domain.model.ContentType.ARABIC_TEXT
            ContentType.ENGLISH_TEXT -> com.ryen.sunnah_alhadi.domain.model.ContentType.ENGLISH_TEXT
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

fun Reference.toDomain(): com.ryen.sunnah_alhadi.domain.model.Reference {
    return com.ryen.sunnah_alhadi.domain.model.Reference(
        source = this.source
    )
}