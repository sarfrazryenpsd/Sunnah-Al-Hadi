package com.ryen.sunnah_alhadi.domain.model


data class ContentBlock(
    val type: ContentType,       // "arabic_text" or "english_text"
    val subtype: Any,    // e.g., "verse", "normal"
    val content: String
)