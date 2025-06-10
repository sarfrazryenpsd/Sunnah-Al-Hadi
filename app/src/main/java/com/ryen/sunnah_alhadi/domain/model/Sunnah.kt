package com.ryen.sunnah_alhadi.domain.model

data class Sunnah(
    val id: String,
    val categoryId: Int,
    val title: String,
    val body: List<ContentBlock>,
    val references: List<Reference>? = null,
    val isBookmarked: Boolean = false,
    val extra: List<ExtraContent>? = null,
    val lastSeen: Long? = null
)