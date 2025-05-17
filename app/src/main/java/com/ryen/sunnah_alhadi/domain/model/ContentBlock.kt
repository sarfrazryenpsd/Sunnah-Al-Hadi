package com.ryen.sunnah_alhadi.domain.model



data class ContentBlock(
    val type: ContentType,
    val subtype: String,
    val content: String
)