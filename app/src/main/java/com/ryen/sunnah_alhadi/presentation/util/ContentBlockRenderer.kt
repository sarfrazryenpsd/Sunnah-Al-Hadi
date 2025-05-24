package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.data.local.database.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.Reference
import com.ryen.sunnah_alhadi.ui.theme.ArabicTypography
import com.ryen.sunnah_alhadi.ui.theme.ContentTypography

// ContentBlockRenderer.kt
object ContentStyleResolver {

    @Composable
    fun getTextStyle(type: ContentType, subtype: Any): TextStyle {
        return when (type) {
            ContentType.ARABIC_TEXT -> getArabicStyle(subtype)
            ContentType.ENGLISH_TEXT -> getEnglishStyle(subtype)
        }
    }

    @Composable
    private fun getArabicStyle(subtype: Any): TextStyle {
        return when (subtype) {
            ArabicSubtype.VERSE -> ArabicTypography.quranicVerse
            ArabicSubtype.SUPPLICATION -> ArabicTypography.supplication
            ArabicSubtype.HONORIFIC -> ArabicTypography.honorific
            ArabicSubtype.OTHER -> ArabicTypography.other
            else -> ArabicTypography.other // Fallback for string subtypes
        }
    }

    @Composable
    private fun getEnglishStyle(subtype: Any): TextStyle {
        return when (subtype) {
            EnglishSubtype.NORMAL -> ContentTypography.englishBodyNormal
            EnglishSubtype.TRANSLATION -> ContentTypography.englishBodyTranslation
            else -> ContentTypography.englishBodyNormal // Fallback for string subtypes
        }
    }

    @Composable
    fun getTextAlignment(type: ContentType): TextAlign {
        return when (type) {
            ContentType.ARABIC_TEXT -> TextAlign.End
            ContentType.ENGLISH_TEXT -> TextAlign.Start
        }
    }

    @Composable
    fun getTextDirection(type: ContentType): TextDirection {
        return when (type) {
            ContentType.ARABIC_TEXT -> TextDirection.Rtl
            ContentType.ENGLISH_TEXT -> TextDirection.Ltr
        }
    }
}

/**
 * Renders individual content blocks with proper styling and alignment
 */
@Composable
fun ContentBlockRenderer(
    contentBlocks: List<ContentBlock>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        contentBlocks.forEach { block ->
            ContentBlockItem(
                contentBlock = block,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ContentBlockItem(
    contentBlock: ContentBlock,
    modifier: Modifier = Modifier
) {
    val textStyle = ContentStyleResolver.getTextStyle(
        type = contentBlock.type,
        subtype = contentBlock.subtype
    )
    val textAlign = ContentStyleResolver.getTextAlignment(contentBlock.type)
    val textDirection = ContentStyleResolver.getTextDirection(contentBlock.type)

    // Check if content contains mixed languages
    val hasMixedContent = containsMixedLanguages(contentBlock.content)

    if (hasMixedContent && contentBlock.type == ContentType.ENGLISH_TEXT) {
        // Handle mixed English-Arabic content
        MixedContentRenderer(
            content = contentBlock.content,
            baseStyle = textStyle,
            modifier = modifier
        )
    } else {
        // Handle single language content
        SelectionContainer {
            Text(
                text = contentBlock.content,
                style = textStyle.copy(
                    textAlign = textAlign,
                    textDirection = textDirection
                ),
                modifier = modifier.padding(
                    horizontal = if (contentBlock.type == ContentType.ARABIC_TEXT) 8.dp else 0.dp
                ),
                color = getContentColor(contentBlock.type, contentBlock.subtype)
            )
        }
    }
}

/**
 * Renders mixed English-Arabic content with proper styling for each part
 */
@Composable
private fun MixedContentRenderer(
    content: String,
    baseStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    // Arabic patterns - common Arabic Unicode ranges and specific religious phrases
    val arabicPattern = Regex("""[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\uFB50-\uFDFF\uFE70-\uFEFF]+""")
    val commonArabicPhrases = listOf(
        "صَلَّى اللهُ عَلَيْهِ وَسَلَّم",
        "عَلَيْهِ السَّلَام",
        "رَضِيَ اللهُ عَنْه",
        "رَضِيَ اللهُ عَنْها"
    )

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        val matches = arabicPattern.findAll(content).toList()

        matches.forEach { match ->
            // Add English text before Arabic
            if (match.range.first > lastIndex) {
                val englishText = content.substring(lastIndex, match.range.first)
                withStyle(
                    SpanStyle(
                        fontFamily = baseStyle.fontFamily,
                        fontSize = baseStyle.fontSize,
                        fontWeight = baseStyle.fontWeight,
                        fontStyle = baseStyle.fontStyle
                    )
                ) {
                    append(englishText)
                }
            }

            // Add Arabic text with appropriate styling
            val arabicText = match.value
            val arabicStyle = determineArabicStyle(arabicText, commonArabicPhrases)

            withStyle(
                SpanStyle(
                    fontFamily = arabicStyle.fontFamily,
                    fontSize = arabicStyle.fontSize,
                    fontWeight = arabicStyle.fontWeight,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append(arabicText)
            }

            lastIndex = match.range.last + 1
        }

        // Add remaining English text
        if (lastIndex < content.length) {
            val remainingText = content.substring(lastIndex)
            withStyle(
                SpanStyle(
                    fontFamily = baseStyle.fontFamily,
                    fontSize = baseStyle.fontSize,
                    fontWeight = baseStyle.fontWeight,
                    fontStyle = baseStyle.fontStyle
                )
            ) {
                append(remainingText)
            }
        }
    }

    SelectionContainer {
        Text(
            text = annotatedString,
            style = baseStyle.copy(
                textAlign = TextAlign.Start,
                textDirection = TextDirection.Ltr
            ),
            modifier = modifier
        )
    }
}

/**
 * Determines appropriate Arabic text style based on content
 */
@Composable
private fun determineArabicStyle(arabicText: String, commonPhrases: List<String>): TextStyle {
    return when {
        commonPhrases.any { arabicText.contains(it) } -> ArabicTypography.honorific
        arabicText.length > 50 -> ArabicTypography.quranicVerse // Likely a verse
        else -> ArabicTypography.supplication
    }
}

/**
 * Determines text color based on content type and subtype
 */
@Composable
private fun getContentColor(type: ContentType, subtype: Any): Color {
    return when {
        type == ContentType.ARABIC_TEXT && subtype == ArabicSubtype.VERSE ->
            MaterialTheme.colorScheme.primary
        type == ContentType.ENGLISH_TEXT && subtype == EnglishSubtype.TRANSLATION ->
            MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }
}

/**
 * Checks if content contains mixed Arabic-English text
 */
private fun containsMixedLanguages(content: String): Boolean {
    val hasArabic = content.any { it.code in 0x0600..0x06FF || it.code in 0xFB50..0xFDFF }
    val hasEnglish = content.any { it in 'A'..'Z' || it in 'a'..'z' }
    return hasArabic && hasEnglish
}

/**
 * Renders reference section with proper styling
 */
@Composable
fun ReferenceRenderer(
    references: List<Reference>,
    modifier: Modifier = Modifier
) {
    if (references.isNotEmpty()) {
        Column(
            modifier = modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "References:",
                style = ContentTypography.reference.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.outline
            )

            references.forEach { reference ->
                Text(
                    text = "• ${reference.source}",
                    style = ContentTypography.reference,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * Renders extra content sections (parables, explanations, etc.)
 */
@Composable
fun ExtraContentRenderer(
    extraContent: List<ExtraContent>,
    modifier: Modifier = Modifier
) {
    if (extraContent.isNotEmpty()) {
        Column(
            modifier = modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            extraContent.forEach { extra ->
                ExtraContentSection(
                    extraContent = extra,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ExtraContentSection(
    extraContent: ExtraContent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        Text(
            text = getExtraContentTitle(extraContent.type),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Section content
        ContentBlockRenderer(
            contentBlocks = extraContent.content,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

/**
 * Gets display title for extra content types
 */
private fun getExtraContentTitle(type: ExtraContentType): String {
    return when (type) {
        ExtraContentType.PARABLE -> "Parable"
        ExtraContentType.SCHOLARLY_EXPLANATION -> "Scholarly Explanation"
        ExtraContentType.EXPLANATION -> "Explanation"
        ExtraContentType.TRANSLATION -> "Translation"
        ExtraContentType.HADITH -> "Related Hadith"
        ExtraContentType.NOTES -> "Note"
        ExtraContentType.WARNING -> "⚠️ Important"
        ExtraContentType.BENEFIT -> "Benefit"
    }
}