package com.ryen.sunnah_alhadi.domain.model

data class SunnahBookmarked(
    val sunnahId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)