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
import com.ryen.sunnah_alhadi.data.local.datasource.entity.SunnahWithBookmark

fun SunnahEntity.toDomain(isBookmarked: Boolean = false, bookmarkedAt: Long? = null): com.ryen.sunnah_alhadi.domain.model.Sunnah {
    return com.ryen.sunnah_alhadi.domain.model.Sunnah(
        id = this.id,
        categoryId = this.categoryId,
        title = this.title,
        body = this.body.map { it.toDomain() },
        references = this.references?.map { it.toDomain() },
        extra = this.extra?.map { it.toDomain() },
        isBookmarked = isBookmarked,
        bookmarkedAt = bookmarkedAt
    )
}

fun SunnahWithBookmark.toDomain(): com.ryen.sunnah_alhadi.domain.model.Sunnah {
    return this.sunnah.toDomain(this.isBookmarked, this.bookmarkedAt)
}

fun CategoryEntity.toDomain(): com.ryen.sunnah_alhadi.domain.model.Category {
    return com.ryen.sunnah_alhadi.domain.model.Category(
        id = this.id,
        topic = this.topic
    )
}

fun BookmarkEntity.toDomain(): com.ryen.sunnah_alhadi.domain.model.SunnahBookmarked {
    return com.ryen.sunnah_alhadi.domain.model.SunnahBookmarked(
        sunnahId = this.sunnahId,
        bookmarkedAt = this.bookmarkedAt
    )
}

fun ContentBlock.toDomain(): com.ryen.sunnah_alhadi.domain.model.ContentBlock {
    val domainType = when (this.type) {
        ContentType.ARABIC_TEXT -> com.ryen.sunnah_alhadi.domain.model.ContentType.ARABIC_TEXT
        ContentType.ENGLISH_TEXT -> com.ryen.sunnah_alhadi.domain.model.ContentType.ENGLISH_TEXT
    }

    val domainSubtype = when (this.subtype) {
        is ArabicSubtype -> this.subtype.name.lowercase()
        is EnglishSubtype -> this.subtype.name.lowercase()
        is String -> this.subtype
        else -> "unknown"
    }

    return com.ryen.sunnah_alhadi.domain.model.ContentBlock(
        type = domainType,
        subtype = domainSubtype,
        content = this.content
    )
}

fun ExtraContent.toDomain(): com.ryen.sunnah_alhadi.domain.model.ExtraContent {
    val domainType = when (this.type) {
        ExtraContentType.PARABLE -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.PARABLE
        ExtraContentType.SCHOLARLY_EXPLANATION -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.SCHOLARLY_EXPLANATION
        ExtraContentType.EXPLANATION -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.EXPLANATION
        ExtraContentType.TRANSLATION -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.TRANSLATION
        ExtraContentType.HADITH -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.HADITH
        ExtraContentType.NOTES -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.NOTES
        ExtraContentType.WARNING -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.WARNING
        ExtraContentType.BENEFIT -> com.ryen.sunnah_alhadi.domain.model.ExtraContentType.BENEFIT
    }

    return com.ryen.sunnah_alhadi.domain.model.ExtraContent(
        type = domainType,
        content = this.content.map { it.toDomain() }
    )
}

fun Reference.toDomain(): com.ryen.sunnah_alhadi.domain.model.Reference {
    return com.ryen.sunnah_alhadi.domain.model.Reference(
        source = this.source
    )
}