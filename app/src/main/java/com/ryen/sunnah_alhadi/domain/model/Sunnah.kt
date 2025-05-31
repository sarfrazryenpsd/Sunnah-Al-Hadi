package com.ryen.sunnah_alhadi.domain.model

data class Sunnah(
    val id: String,
    val categoryId: Int,
    val title: String,
    val body: List<ContentBlock>,
    val references: List<String>? = null,
    val isBookmarked: Boolean = false,
    val lastSeen: Long? = null
)