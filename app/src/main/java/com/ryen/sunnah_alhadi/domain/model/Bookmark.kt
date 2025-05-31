package com.ryen.sunnah_alhadi.domain.model

data class Bookmark(
    val sunnahId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)