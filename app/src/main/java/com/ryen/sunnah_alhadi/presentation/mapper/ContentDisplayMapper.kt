package com.ryen.sunnah_alhadi.presentation.mapper

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import com.ryen.sunnah_alhadi.data.local.database.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.ui.theme.ArabicTypography
import com.ryen.sunnah_alhadi.ui.theme.ContentTypography

// Typography mapper for easier management
object TypographyMapper {

    fun getContentTextStyle(contentType: ContentType, subtype: Any): TextStyle {
        return when (contentType) {
            ContentType.ARABIC_TEXT -> getArabicStyle(subtype)
            ContentType.ENGLISH_TEXT -> getEnglishStyle(subtype)
        }
    }

    private fun getArabicStyle(subtype: Any): TextStyle {
        return when (subtype) {
            ArabicSubtype.VERSE -> ArabicTypography.quranicVerse
            ArabicSubtype.SUPPLICATION -> ArabicTypography.supplication
            ArabicSubtype.HONORIFIC -> ArabicTypography.honorific
            ArabicSubtype.OTHER -> ArabicTypography.other
            is String -> parseArabicSubtypeString(subtype)
            else -> ArabicTypography.other
        }
    }

    private fun getEnglishStyle(subtype: Any): TextStyle {
        return when (subtype) {
            EnglishSubtype.NORMAL -> ContentTypography.englishBodyNormal
            EnglishSubtype.TRANSLATION -> ContentTypography.englishBodyTranslation
            is String -> parseEnglishSubtypeString(subtype)
            else -> ContentTypography.englishBodyNormal
        }
    }

    private fun parseArabicSubtypeString(subtypeString: String): TextStyle {
        return when (subtypeString.lowercase()) {
            "verse" -> ArabicTypography.quranicVerse
            "supplication" -> ArabicTypography.supplication
            "honorifics", "honorific" -> ArabicTypography.honorific
            else -> ArabicTypography.other
        }
    }

    private fun parseEnglishSubtypeString(subtypeString: String): TextStyle {
        return when (subtypeString.lowercase()) {
            "normal" -> ContentTypography.englishBodyNormal
            "translation" -> ContentTypography.englishBodyTranslation
            else -> ContentTypography.englishBodyNormal
        }
    }
}

// Extension functions for easier usage
fun ContentBlock.getTextStyle(): TextStyle {
    return TypographyMapper.getContentTextStyle(this.type, this.subtype)
}

fun ContentBlock.getTextAlign(): TextAlign {
    return when (this.type) {
        ContentType.ARABIC_TEXT -> TextAlign.Right
        ContentType.ENGLISH_TEXT -> when (this.subtype) {
            EnglishSubtype.TRANSLATION -> TextAlign.Center
            is String -> if (this.subtype == "translation") TextAlign.Center else TextAlign.Start
            else -> TextAlign.Start
        }
    }
}

fun ContentBlock.getTextDirection(): TextDirection {
    return when (this.type) {
        ContentType.ARABIC_TEXT -> TextDirection.Rtl
        ContentType.ENGLISH_TEXT -> TextDirection.Ltr
    }
}