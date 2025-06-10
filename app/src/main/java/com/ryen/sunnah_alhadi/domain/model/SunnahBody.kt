package com.ryen.sunnah_alhadi.domain.model


enum class ContentType { ARABIC_TEXT, ENGLISH_TEXT }

enum class ArabicSubtype { VERSE, SUPPLICATION, HONORIFIC, OTHER }

enum class EnglishSubtype { NORMAL, TRANSLATION }


data class ExtraContent(
    val type: ExtraContentType,
    val content: List<ContentBlock>
)

enum class ExtraContentType {
    PARABLE, SCHOLARLY_EXPLANATION, EXPLANATION,
    TRANSLATION, HADITH, NOTES, WARNING, BENEFIT
}



data class Reference(val source: String)
