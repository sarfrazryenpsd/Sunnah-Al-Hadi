package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.data.local.database.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.Reference
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import com.ryen.sunnah_alhadi.ui.theme.ArabicTypography
import com.ryen.sunnah_alhadi.ui.theme.ContentTypography
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

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
        ExtraContentType.WARNING -> "Important"
        ExtraContentType.BENEFIT -> "Benefit"
    }
}































object PreviewData {

    val mixedContentBlocks = listOf(
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL,
            content = "The beloved Prophet صَلَّى اللهُ عَلَيْهِ وَسَلَّم said: \"By virtue of the righteous Muslim, Allah Almighty removes a calamity from 100 houses in his neighborhood.\""
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL,
            content = "Then he صَلَّى اللهُ عَلَيْهِ وَسَلَّم recited:"
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.VERSE,
            content = "وَلَوْلَا دَفْعُ اللَّهِ النَّاسَ بَعْضَهُم بِبَعْضٍۢ لَّفَسَدَتِ الْأَرْضُ"
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.TRANSLATION,
            content = "\"And if Allah does not keep away some people by means of others, the earth would have been corrupted.\""
        )
    )

    val pureArabicBlocks = listOf(
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.SUPPLICATION,
            content = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.HONORIFIC,
            content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.OTHER,
            content = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا"
        )
    )

    val englishBlocks = listOf(
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL,
            content = "It is recommended to recite Bismillah before starting any good deed. This practice brings blessings and protection from Allah."
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.TRANSLATION,
            content = "Translation: \"In the name of Allah, the Most Gracious, the Most Merciful.\""
        )
    )

    val sampleReferences = listOf(
        Reference("Kanz-ul-Iman, Surah Al-Baqarah, verse 251"),
        Reference("Majma' Al-Zawa`id, vol. 8, p. 299, Hadith 13533")
    )

    val sampleExtraContent = listOf(
        ExtraContent(
            type = ExtraContentType.BENEFIT,
            content = listOf(
                ContentBlock(
                    type = ContentType.ENGLISH_TEXT,
                    subtype = EnglishSubtype.NORMAL,
                    content = "This hadith teaches us the importance of being a righteous neighbor and how our good deeds can benefit the entire community."
                )
            )
        ),
        ExtraContent(
            type = ExtraContentType.WARNING,
            content = listOf(
                ContentBlock(
                    type = ContentType.ENGLISH_TEXT,
                    subtype = EnglishSubtype.NORMAL,
                    content = "Remember that righteousness should be consistent in all aspects of life, not just in public display."
                )
            )
        )
    )

    val sampleSunnah = SunnahEntity(
        id = "preview_01",
        categoryId = 1,
        title = "The Righteous Neighbor Saves Many",
        body = mixedContentBlocks,
        references = sampleReferences,
        extra = sampleExtraContent
    )
}

@Preview(name = "Mixed Content - Light Theme")
@Composable
fun ContentBlockRendererPreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mixed English-Arabic Content",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ContentBlockRenderer(
                            contentBlocks = PreviewData.mixedContentBlocks,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Arabic Content Only")
@Composable
fun ArabicContentPreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Pure Arabic Content",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ContentBlockRenderer(
                            contentBlocks = PreviewData.pureArabicBlocks,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "English Content Only")
@Composable
fun EnglishContentPreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "English Content",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ContentBlockRenderer(
                            contentBlocks = PreviewData.englishBlocks,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Complete Sunnah Card")
@Composable
fun CompleteSunnahCardPreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SunnahCardPreview(
                    sunnah = PreviewData.sampleSunnah,
                    isBookmarked = false
                )
            }
        }
    }
}

@Preview(name = "Complete Sunnah Card - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CompleteSunnahCardDarkPreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SunnahCardPreview(
                    sunnah = PreviewData.sampleSunnah,
                    isBookmarked = true
                )
            }
        }
    }
}

@Preview(name = "References and Extra Content")
@Composable
fun ReferencesAndExtraPreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "References Section",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ReferenceRenderer(
                            references = PreviewData.sampleReferences,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text(
                    text = "Extra Content Section",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ExtraContentRenderer(
                            extraContent = PreviewData.sampleExtraContent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SunnahCardPreview(
    sunnah: SunnahEntity,
    isBookmarked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = sunnah.title,
                    style = ContentTypography.sunnahTitle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Main content
            ContentBlockRenderer(
                contentBlocks = sunnah.body,
                modifier = Modifier.fillMaxWidth()
            )

            // Extra content
            sunnah.extra?.let { extras ->
                ExtraContentRenderer(
                    extraContent = extras,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // References
            sunnah.references?.let { refs ->
                ReferenceRenderer(
                    references = refs,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

// Additional preview for typography testing
@Preview(name = "Typography Showcase", heightDp = 800)
@Composable
fun TypographyShowcasePreview() {
    SunnahAlHadiTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Typography Showcase",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider()

                // Arabic Typography
                Text(
                    text = "Arabic Styles:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "وَلَوْلَا دَفْعُ اللَّهِ النَّاسَ بَعْضَهُم بِبَعْضٍۢ لَّفَسَدَتِ الْأَرْضُ",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // English Typography
                Text(
                    text = "English Styles:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "Regular body text with proper line height and spacing for comfortable reading.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Translation text with italic styling for better distinction from regular content.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "Reference text with medium weight",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}