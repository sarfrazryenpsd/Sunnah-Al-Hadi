package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import com.ryen.sunnah_alhadi.data.local.database.entity.ArabicSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentBlock
import com.ryen.sunnah_alhadi.data.local.database.entity.ContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.EnglishSubtype
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContent
import com.ryen.sunnah_alhadi.data.local.database.entity.ExtraContentType
import com.ryen.sunnah_alhadi.data.local.database.entity.Reference
import com.ryen.sunnah_alhadi.data.local.database.entity.SunnahEntity
import com.ryen.sunnah_alhadi.ui.theme.DynamicAppTypography
import com.ryen.sunnah_alhadi.ui.theme.DynamicArabicTypography
import com.ryen.sunnah_alhadi.ui.theme.DynamicContentTypography
import com.ryen.sunnah_alhadi.ui.theme.DynamicTypographyProvider
import com.ryen.sunnah_alhadi.ui.theme.LocalDynamicTextConfig
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

/**
 * Dynamic Content Style Resolver that adapts to different screen sizes
 */
object DynamicContentStyleResolver {

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
            ArabicSubtype.VERSE -> DynamicArabicTypography.quranicVerse()
            ArabicSubtype.SUPPLICATION -> DynamicArabicTypography.supplication()
            ArabicSubtype.HONORIFIC -> DynamicArabicTypography.honorific()
            ArabicSubtype.OTHER -> DynamicArabicTypography.other()
            else -> DynamicArabicTypography.other() // Fallback for string subtypes
        }
    }

    @Composable
    private fun getEnglishStyle(subtype: Any): TextStyle {
        return when (subtype) {
            EnglishSubtype.NORMAL -> DynamicContentTypography.englishBodyNormal()
            EnglishSubtype.TRANSLATION -> DynamicContentTypography.englishBodyTranslation()
            else -> DynamicContentTypography.englishBodyNormal() // Fallback for string subtypes
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

    @Composable
    fun getContentPadding(): PaddingValues {
        val screenSize = LocalDynamicTextConfig.current.screenSize
        return when (screenSize) {
            ScreenSize.COMPACT -> PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ScreenSize.MEDIUM -> PaddingValues(horizontal = 6.dp, vertical = 10.dp)
            ScreenSize.EXPANDED -> PaddingValues(horizontal = 8.dp, vertical = 12.dp)
        }
    }

    @Composable
    fun getBlockSpacing(): Dp {
        val screenSize = LocalDynamicTextConfig.current.screenSize
        return when (screenSize) {
            ScreenSize.COMPACT -> 12.dp
            ScreenSize.MEDIUM -> 14.dp
            ScreenSize.EXPANDED -> 16.dp
        }
    }
}

/**
 * Main Content Block Renderer with dynamic typography support
 * Must be wrapped with DynamicTypographyProvider
 */
@Composable
fun DynamicContentBlockRenderer(
    contentBlocks: List<ContentBlock>,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    DynamicTypographyProvider(windowSizeClass = windowSizeClass) {
        val blockSpacing = DynamicContentStyleResolver.getBlockSpacing()

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(blockSpacing)
        ) {
            contentBlocks.forEach { block ->
                DynamicContentBlockItem(
                    contentBlock = block,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DynamicContentBlockItem(
    contentBlock: ContentBlock,
    modifier: Modifier = Modifier
) {
    val textStyle = DynamicContentStyleResolver.getTextStyle(
        type = contentBlock.type,
        subtype = contentBlock.subtype
    )
    val textAlign = DynamicContentStyleResolver.getTextAlignment(contentBlock.type)
    val textDirection = DynamicContentStyleResolver.getTextDirection(contentBlock.type)
    val contentPadding = DynamicContentStyleResolver.getContentPadding()

    // Check if content contains mixed languages
    val hasMixedContent = containsMixedLanguages(contentBlock.content)

    if (hasMixedContent && contentBlock.type == ContentType.ENGLISH_TEXT) {
        // Handle mixed English-Arabic content
        DynamicMixedContentRenderer(
            content = contentBlock.content,
            baseStyle = textStyle,
            modifier = modifier.padding(contentPadding)
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
                    if (contentBlock.type == ContentType.ARABIC_TEXT) {
                        contentPadding.copy(horizontal = contentPadding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) + 8.dp)
                    } else contentPadding
                ),
                color = getContentColor(contentBlock.type, contentBlock.subtype)
            )
        }
    }
}

/**
 * Dynamic Mixed Content Renderer with adaptive styling
 */
@Composable
private fun DynamicMixedContentRenderer(
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
            val arabicStyle = determineDynamicArabicStyle(arabicText, commonArabicPhrases)

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
 * Determines appropriate Arabic text style based on content using dynamic typography
 */
@Composable
private fun determineDynamicArabicStyle(arabicText: String, commonPhrases: List<String>): TextStyle {
    return when {
        commonPhrases.any { arabicText.contains(it) } -> DynamicArabicTypography.honorific()
        arabicText.length > 50 -> DynamicArabicTypography.quranicVerse() // Likely a verse
        else -> DynamicArabicTypography.supplication()
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
 * Dynamic Reference Renderer with adaptive styling
 */
@Composable
fun DynamicReferenceRenderer(
    references: List<Reference>,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    if (references.isNotEmpty()) {
        DynamicTypographyProvider(windowSizeClass = windowSizeClass) {
            val screenSize = LocalDynamicTextConfig.current.screenSize
            val topPadding = when (screenSize) {
                ScreenSize.COMPACT -> 16.dp
                ScreenSize.MEDIUM -> 18.dp
                ScreenSize.EXPANDED -> 20.dp
            }
            val itemSpacing = when (screenSize) {
                ScreenSize.COMPACT -> 4.dp
                ScreenSize.MEDIUM -> 5.dp
                ScreenSize.EXPANDED -> 6.dp
            }

            Column(
                modifier = modifier.padding(top = topPadding),
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                Text(
                    text = "References:",
                    style = DynamicContentTypography.reference().copy(
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.outline
                )

                references.forEach { reference ->
                    Text(
                        text = "• ${reference.source}",
                        style = DynamicContentTypography.reference(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dynamic Extra Content Renderer with adaptive styling
 */
@Composable
fun DynamicExtraContentRenderer(
    extraContent: List<ExtraContent>,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    if (extraContent.isNotEmpty()) {
        DynamicTypographyProvider(windowSizeClass = windowSizeClass) {
            val screenSize = LocalDynamicTextConfig.current.screenSize
            val topPadding = when (screenSize) {
                ScreenSize.COMPACT -> 16.dp
                ScreenSize.MEDIUM -> 18.dp
                ScreenSize.EXPANDED -> 20.dp
            }
            val itemSpacing = when (screenSize) {
                ScreenSize.COMPACT -> 12.dp
                ScreenSize.MEDIUM -> 14.dp
                ScreenSize.EXPANDED -> 16.dp
            }

            Column(
                modifier = modifier.padding(top = topPadding),
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                extraContent.forEach { extra ->
                    DynamicExtraContentSection(
                        extraContent = extra,
                        windowSizeClass = windowSizeClass,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicExtraContentSection(
    extraContent: ExtraContent,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalDynamicTextConfig.current.screenSize
    val cornerRadius = when (screenSize) {
        ScreenSize.COMPACT -> 4.dp
        ScreenSize.MEDIUM -> 6.dp
        ScreenSize.EXPANDED -> 8.dp
    }
    val containerPadding = when (screenSize) {
        ScreenSize.COMPACT -> 16.dp
        ScreenSize.MEDIUM -> 18.dp
        ScreenSize.EXPANDED -> 20.dp
    }
    val contentPadding = when (screenSize) {
        ScreenSize.COMPACT -> 12.dp
        ScreenSize.MEDIUM -> 14.dp
        ScreenSize.EXPANDED -> 16.dp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(containerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section header
            Text(
                text = getExtraContentTitle(extraContent.type),
                style = DynamicAppTypography.titleSmall(),
                color = MaterialTheme.colorScheme.primary
            )

            // Section content
            DynamicContentBlockRenderer(
                contentBlocks = extraContent.content,
                windowSizeClass = windowSizeClass,
                modifier = Modifier.padding(start = contentPadding)
            )
        }
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

/**
 * Utility extension for PaddingValues
 */
private fun PaddingValues.copy(
    horizontal: Dp? = null,
    vertical: Dp? = null
): PaddingValues {
    return PaddingValues(
        start = horizontal ?: calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        end = horizontal ?: calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
        top = vertical ?: calculateTopPadding(),
        bottom = vertical ?: calculateBottomPadding()
    )
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

//@Preview(name = "Mixed Content - Light Theme")
//@Composable
//fun ContentBlockRendererPreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = "Mixed English-Arabic Content",
//                    style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        DynamicContentBlockRenderer(
//                            contentBlocks = PreviewData.mixedContentBlocks,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(name = "Arabic Content Only")
//@Composable
//fun ArabicContentPreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = "Pure Arabic Content",
//                    style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        DynamicContentBlockRenderer(
//                            contentBlocks = PreviewData.pureArabicBlocks,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(name = "English Content Only")
//@Composable
//fun EnglishContentPreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = "English Content",
//                    style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        DynamicContentBlockRenderer(
//                            contentBlocks = PreviewData.englishBlocks,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(name = "Complete Sunnah Card")
//@Composable
//fun CompleteSunnahCardPreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                SunnahCardPreview(
//                    sunnah = PreviewData.sampleSunnah,
//                    isBookmarked = false
//                )
//            }
//        }
//    }
//}
//
//@Preview(name = "Complete Sunnah Card - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun CompleteSunnahCardDarkPreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                SunnahCardPreview(
//                    sunnah = PreviewData.sampleSunnah,
//                    isBookmarked = true
//                )
//            }
//        }
//    }
//}
//
//@Preview(name = "References and Extra Content")
//@Composable
//fun ReferencesAndExtraPreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = "References Section",
//                    style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        DynamicReferenceRenderer(
//                            references = PreviewData.sampleReferences,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//
//                Text(
//                    text = "Extra Content Section",
//                    style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        DynamicExtraContentRenderer(
//                            extraContent = PreviewData.sampleExtraContent,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SunnahCardPreview(
//    sunnah: SunnahEntity,
//    isBookmarked: Boolean,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            // Title section
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top
//            ) {
//                Text(
//                    text = sunnah.title,
//                    style = DynamicContentTypography.sunnahTitle(),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.weight(1f),
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//            }
//
//            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
//
//            // Main content
//            DynamicContentBlockRenderer(
//                contentBlocks = sunnah.body,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            // Extra content
//            sunnah.extra?.let { extras ->
//                DynamicExtraContentRenderer(
//                    extraContent = extras,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
//            // References
//            sunnah.references?.let { refs ->
//                DynamicReferenceRenderer(
//                    references = refs,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
//
//        }
//    }
//}
//
//// Additional preview for typography testing
//@Preview(name = "Typography Showcase", heightDp = 800)
//@Composable
//fun TypographyShowcasePreview() {
//    SunnahAlHadiTheme {
//        Surface {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Text(
//                    text = "Typography Showcase",
//                    style = MaterialTheme.typography.headlineLarge,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                HorizontalDivider()
//
//                // Arabic Typography
//                Text(
//                    text = "Arabic Styles:",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.secondary
//                )
//
//                Text(
//                    text = "وَلَوْلَا دَفْعُ اللَّهِ النَّاسَ بَعْضَهُم بِبَعْضٍۢ لَّفَسَدَتِ الْأَرْضُ",
//                    style = DynamicArabicTypography.quranicVerse(),
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Text(
//                    text = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم",
//                    style = DynamicArabicTypography.honorific(),
//                    color = MaterialTheme.colorScheme.tertiary
//                )
//
//                Text(
//                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
//                    style = DynamicArabicTypography.supplication(),
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // English Typography
//                Text(
//                    text = "English Styles:",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.secondary
//                )
//
//                Text(
//                    text = "Regular body text with proper line height and spacing for comfortable reading.",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//
//                Text(
//                    text = "Translation text with italic styling for better distinction from regular content.",
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontStyle = FontStyle.Italic
//                    ),
//                    color = MaterialTheme.colorScheme.secondary
//                )
//
//                Text(
//                    text = "Reference text with medium weight",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.outline
//                )
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun previewWindowSizeClass(screenWidthDp: Int, screenHeightDp: Int): WindowSizeClass {
    val size = DpSize(screenWidthDp.dp, screenHeightDp.dp)
    return WindowSizeClass.calculateFromSize(size)
}

@Composable
fun DynamicContentPreviewWrapper(
    windowSizeClass: WindowSizeClass,
    contentBlocks: List<ContentBlock>,
    references: List<Reference> = emptyList(),
    extraContent: List<ExtraContent> = emptyList()
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp)
    ) {
        DynamicContentBlockRenderer(
            contentBlocks = contentBlocks,
            windowSizeClass = windowSizeClass
        )

        Spacer(modifier = Modifier.height(24.dp))

        DynamicReferenceRenderer(
            references = references,
            windowSizeClass = windowSizeClass
        )

        Spacer(modifier = Modifier.height(24.dp))

        DynamicExtraContentRenderer(
            extraContent = extraContent,
            windowSizeClass = windowSizeClass
        )
    }
}

@Preview(name = "Mixed Content - Compact", widthDp = 400)
@Composable
fun PreviewMixedCompact() {
    DynamicContentPreviewWrapper(
        windowSizeClass = previewWindowSizeClass(400, 640),
        contentBlocks = PreviewData.mixedContentBlocks,
        references = PreviewData.sampleReferences,
        extraContent = PreviewData.sampleExtraContent
    )
}

@Preview(name = "Mixed Content - Medium", widthDp = 600)
@Composable
fun PreviewMixedMedium() {
    DynamicContentPreviewWrapper(
        windowSizeClass = previewWindowSizeClass(600, 800),
        contentBlocks = PreviewData.mixedContentBlocks,
        references = PreviewData.sampleReferences,
        extraContent = PreviewData.sampleExtraContent
    )
}

@Preview(name = "Mixed Content - Expanded", widthDp = 840)
@Composable
fun PreviewMixedExpanded() {
    DynamicContentPreviewWrapper(
        windowSizeClass = previewWindowSizeClass(840, 1024),
        contentBlocks = PreviewData.mixedContentBlocks,
        references = PreviewData.sampleReferences,
        extraContent = PreviewData.sampleExtraContent
    )
}

@Preview(name = "Pure Arabic - Medium", widthDp = 600)
@Composable
fun PreviewArabicMedium() {
    DynamicContentPreviewWrapper(
        windowSizeClass = previewWindowSizeClass(600, 800),
        contentBlocks = PreviewData.pureArabicBlocks
    )
}

@Preview(name = "English Content - Compact", widthDp = 400)
@Composable
fun PreviewEnglishCompact() {
    DynamicContentPreviewWrapper(
        windowSizeClass = previewWindowSizeClass(400, 640),
        contentBlocks = PreviewData.englishBlocks
    )
}

