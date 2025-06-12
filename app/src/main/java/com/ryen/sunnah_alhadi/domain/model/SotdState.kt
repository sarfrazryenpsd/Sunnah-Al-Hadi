package com.ryen.sunnah_alhadi.domain.model

data class SotdState(
    val currentSotd: Sunnah?,
    val isSeen: Boolean,
    val isAvailable: Boolean,
    val generatedDate: Long
)