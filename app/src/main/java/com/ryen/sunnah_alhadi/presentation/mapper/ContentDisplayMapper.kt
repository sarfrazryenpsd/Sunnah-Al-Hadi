package com.ryen.sunnah_alhadi.presentation.mapper

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentType
import com.ryen.sunnah_alhadi.ui.theme.ArabicTypography
import com.ryen.sunnah_alhadi.ui.theme.ContentTypography

object ContentDisplayMapper {
    fun mapToTextStyle(block: ContentBlock): TextStyle {
        return when (block.type) {
            ContentType.ARABIC_TEXT -> when (block.subtype) {
                "verse" -> { ArabicTypography.quranicVerse }
                "supplication" -> { ArabicTypography.supplication }
                "honorific" -> { ArabicTypography.honorific }
                else -> { ArabicTypography.other }
            }
            ContentType.ENGLISH_TEXT -> when (block.subtype) {
                "translation" -> { ContentTypography.englishBodyTranslation }
                else -> { ContentTypography.englishBodyNormal }
            }
        }
    }

    fun mapToAlignment(block: ContentBlock): TextAlign {
        return if (block.type == ContentType.ARABIC_TEXT) TextAlign.End else TextAlign.Start
    }
}