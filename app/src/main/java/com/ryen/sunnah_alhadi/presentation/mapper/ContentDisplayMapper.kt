package com.ryen.sunnah_alhadi.presentation.mapper

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentType

object ContentDisplayMapper {
    fun mapToTextStyle(block: ContentBlock): TextStyle {
        return when (block.type) {
            ContentType.ARABIC_TEXT -> when (block.subtype) {
                "verse" -> {/* ArabicTextStyle.verse*/TextStyle()}
                "supplication" -> {/* ArabicTextStyle.supplication*/TextStyle()}
                "honorific" -> {/* ArabicTextStyle.honorifics*/TextStyle()}
                else -> {/* ArabicTextStyle.other*/TextStyle()}
            }
            ContentType.ENGLISH_TEXT -> when (block.subtype) {
                "translation" -> {/*EnglishTextStyle.translation*/ TextStyle()}
                else -> {/*EnglishTextStyle.regular*/ TextStyle()}
            }
        }
    }

    fun mapToAlignment(block: ContentBlock): TextAlign {
        return if (block.type == ContentType.ARABIC_TEXT) TextAlign.End else TextAlign.Start
    }
}