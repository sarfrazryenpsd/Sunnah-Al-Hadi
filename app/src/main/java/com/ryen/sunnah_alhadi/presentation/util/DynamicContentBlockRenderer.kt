package com.ryen.sunnah_alhadi.presentation.util

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.EnglishSubtype
import com.ryen.sunnah_alhadi.domain.model.ExtraContent
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme
import com.ryen.sunnah_alhadi.ui.theme.TypographyConfig
import com.ryen.sunnah_alhadi.ui.theme.appTypography

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
        val safeSubtype = when (subtype) {
            is ArabicSubtype -> subtype
            is String -> try {
                ArabicSubtype.valueOf(subtype.uppercase())
            } catch (e: IllegalArgumentException) {
                null // Invalid enum name
            }

            else -> null
        }

        return when (safeSubtype) {
            ArabicSubtype.VERSE -> MaterialTheme.appTypography.arabicTitle
            ArabicSubtype.SUPPLICATION -> MaterialTheme.appTypography.arabicTitle
            ArabicSubtype.HONORIFICS -> MaterialTheme.appTypography.arabicReference
            ArabicSubtype.OTHER -> MaterialTheme.appTypography.arabicReference
            else -> MaterialTheme.appTypography.arabicReference // Fallback
        }
    }

    @Composable
    private fun getEnglishStyle(subtype: Any): TextStyle {
        val safeSubtype = when (subtype) {
            is EnglishSubtype -> subtype
            is String -> try {
                EnglishSubtype.valueOf(subtype.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }

            else -> null
        }

        return when (safeSubtype) {
            EnglishSubtype.NORMAL -> MaterialTheme.appTypography.extraAndNotificationTitle.copy(fontWeight = FontWeight.Normal)
            EnglishSubtype.TRANSLATION -> MaterialTheme.appTypography.reminderTime
            else -> MaterialTheme.appTypography.tabs.copy(fontWeight = FontWeight.Normal)
        }
    }


    @Composable
    fun getTextAlignment(type: ContentType): TextAlign {
        return when (type) {
            ContentType.ARABIC_TEXT -> TextAlign.Center
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
        val screenSize = LocalScreenSize.current
        return when (screenSize) {
            ScreenSize.COMPACT -> PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ScreenSize.MEDIUM -> PaddingValues(horizontal = 6.dp, vertical = 10.dp)
            ScreenSize.EXPANDED -> PaddingValues(horizontal = 8.dp, vertical = 12.dp)
        }
    }

    @Composable
    fun getBlockSpacing(): Dp {
        val screenSize = LocalScreenSize.current
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

private sealed class BlockGroup {
    data class InlineGroup(val blocks: List<ContentBlock>) : BlockGroup()
    data class SingleBlock(val block: ContentBlock) : BlockGroup()
}

private fun groupConsecutiveInlineBlocks(
    contentBlocks: List<ContentBlock>,
    inlineSubtypes: Set<String>
): List<BlockGroup> {
    val result = mutableListOf<BlockGroup>()
    val currentInlineGroup = mutableListOf<ContentBlock>()

    contentBlocks.forEach { block ->
        // Handle both enum and string subtype formats
        val subtypeString = when (val subtype = block.subtype) {
            else -> subtype
        }

        val isInline = inlineSubtypes.contains(subtypeString.uppercase()) ||
                inlineSubtypes.contains(subtypeString.lowercase())

        if (isInline) {
            currentInlineGroup.add(block)
        } else {
            // If we have accumulated inline blocks, add them as a group
            if (currentInlineGroup.isNotEmpty()) {
                result.add(BlockGroup.InlineGroup(currentInlineGroup.toList()))
                currentInlineGroup.clear()
            }
            // Add the non-inline block
            result.add(BlockGroup.SingleBlock(block))
        }
    }

    // Don't forget the last group if it ends with inline blocks
    if (currentInlineGroup.isNotEmpty()) {
        result.add(BlockGroup.InlineGroup(currentInlineGroup.toList()))
    }

    return result
}

// Enhanced version with better Arabic text handling
@Composable
fun DynamicContentBlockRendererV2(
    contentBlocks: List<ContentBlock>,
    modifier: Modifier = Modifier,
    isHomeSunnah: Boolean = false,
    debugMode: Boolean = false
) {
    val blockSpacing = DynamicContentStyleResolver.getBlockSpacing()
    // Updated to handle both enum and string formats, including plural forms
    val inlineSubtypes = setOf(
        "NORMAL", "HONORIFIC", "HONORIFICS", "OTHER",
        "normal", "honorific", "honorifics", "other"
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(blockSpacing)
    ) {
        val groupedBlocks = groupConsecutiveInlineBlocks(contentBlocks, inlineSubtypes)

        groupedBlocks.forEachIndexed { index, group ->
            when (group) {
                is BlockGroup.InlineGroup -> {
                    if (debugMode) {
                        DebugInfoBox(
                            groupType = "Inline Group ${index + 1}",
                            blocks = group.blocks
                        )
                    }
                    RenderInlineGroupV2(group.blocks, isHomeSunnah)
                }

                is BlockGroup.SingleBlock -> {
                    if (debugMode) {
                        DebugInfoBox(
                            groupType = "Single Block ${index + 1}",
                            blocks = listOf(group.block)
                        )
                    }
                    RenderSingleBlock(group.block, isHomeSunnah)
                }
            }
        }
    }
}

@Composable
private fun DebugInfoBox(
    groupType: String,
    modifier: Modifier = Modifier,
    blocks: List<ContentBlock>,
    isHomeSunnah: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "DEBUG: $groupType",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            blocks.forEachIndexed { index, block ->
                val subtypeDisplay = when (val subtype = block.subtype) {
                    else -> "$subtype (String)"
                }

                Text(
                    text = "  ${index + 1}. ${block.type.name} → $subtypeDisplay",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Text(
                    text = "     Content: \"${block.content.take(50)}${if (block.content.length > 50) "..." else ""}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    val annotatedString = buildAnnotatedString {
        blocks.forEach { block ->
            val style = DynamicContentStyleResolver.getTextStyle(
                type = block.type,
                subtype = block.subtype
            )

            // Create span style based on the block's properties
            val spanStyle = SpanStyle(
                fontFamily = style.fontFamily,
                fontSize = style.fontSize,
                fontWeight = style.fontWeight,
                fontStyle = style.fontStyle,
                color = getContentColor(isHomeSunnah)
            )

            // Apply different styling based on content type
            when (block.type) {
                ContentType.ARABIC_TEXT -> {
                    withStyle(
                        spanStyle.copy(
                            // You might want to apply RTL direction here if needed
                            // Note: Individual spans can't have text direction,
                            // so Arabic text will follow the overall text direction
                        )
                    ) {
                        append(block.content)
                    }
                }

                ContentType.ENGLISH_TEXT -> {
                    withStyle(spanStyle) {
                        append(block.content)
                    }
                }
            }
        }
    }

// Determine the base text direction and alignment
// Use the first block's properties as the base
    val firstBlock = blocks.first()
    val baseAlignment = DynamicContentStyleResolver.getTextAlignment(firstBlock.type)
    val baseDirection = DynamicContentStyleResolver.getTextDirection(firstBlock.type)
    val baseStyle = DynamicContentStyleResolver.getTextStyle(firstBlock.type, firstBlock.subtype)

    SelectionContainer {
        Text(
            text = annotatedString,
            style = baseStyle.copy(
                textAlign = baseAlignment,
                textDirection = baseDirection,
                color = Color.Unspecified // Let individual spans control color
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RenderSingleBlock(block: ContentBlock, isHomeSunnah: Boolean) {
    val textStyle = DynamicContentStyleResolver.getTextStyle(
        type = block.type,
        subtype = block.subtype
    )
    val textAlign = DynamicContentStyleResolver.getTextAlignment(block.type)
    val textDirection = DynamicContentStyleResolver.getTextDirection(block.type)
    val contentPadding = DynamicContentStyleResolver.getContentPadding()

    // Check if content contains mixed languages
    val hasMixedContent = containsMixedLanguages(block.content)

    if (hasMixedContent && block.type == ContentType.ENGLISH_TEXT) {
        // Handle mixed English-Arabic content
        DynamicMixedContentRenderer(
            content = block.content,
            baseStyle = textStyle,
            modifier = Modifier.padding(contentPadding)
        )
    } else {
        // Handle single language content
        SelectionContainer {
            Text(
                text = block.content,
                style = textStyle.copy(
                    textAlign = textAlign,
                    textDirection = textDirection
                ),
                modifier = Modifier.padding(
                    if (block.type == ContentType.ARABIC_TEXT) {
                        contentPadding.copy(
                            horizontal = contentPadding.calculateStartPadding(
                                androidx.compose.ui.unit.LayoutDirection.Ltr
                            ) + 8.dp
                        )
                    } else contentPadding
                ),
                color = getContentColor(isHomeSunnah = isHomeSunnah)
            )
        }
    }
}
@Composable
private fun RenderInlineGroupV2(blocks: List<ContentBlock>, isHomeSunnah: Boolean) {
    // Determine if we have mixed languages in the group
    val hasMixedLanguages = blocks.any { it.type == ContentType.ARABIC_TEXT } &&
            blocks.any { it.type == ContentType.ENGLISH_TEXT }

    val annotatedString = buildAnnotatedString {
        blocks.forEach { block ->
            val style = DynamicContentStyleResolver.getTextStyle(
                type = block.type,
                subtype = block.subtype
            )

            val spanStyle = SpanStyle(
                fontFamily = style.fontFamily,
                fontSize = style.fontSize,
                fontWeight = style.fontWeight,
                fontStyle = style.fontStyle,
                color = getContentColor(isHomeSunnah = isHomeSunnah)
            )

            withStyle(spanStyle) {
                append(block.content)
            }
        }
    }

    // For mixed content, use LTR base direction but let Arabic text render properly
    val baseDirection = if (hasMixedLanguages) {
        TextDirection.Ltr
    } else {
        DynamicContentStyleResolver.getTextDirection(blocks.first().type)
    }

    val baseAlignment = if (hasMixedLanguages) {
        TextAlign.Start
    } else {
        DynamicContentStyleResolver.getTextAlignment(blocks.first().type)
    }

    val baseStyle = DynamicContentStyleResolver.getTextStyle(
        blocks.first().type,
        blocks.first().subtype
    )

    SelectionContainer {
        Text(
            text = annotatedString,
            style = baseStyle.copy(
                textAlign = baseAlignment,
                textDirection = baseDirection,
                color = Color.Unspecified
            ),
            modifier = Modifier.fillMaxWidth()
        )
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
    val arabicPattern =
        Regex("""[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\uFB50-\uFDFF\uFE70-\uFEFF]+""")
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
private fun determineDynamicArabicStyle(
    arabicText: String,
    commonPhrases: List<String>
): TextStyle {
    return when {
        commonPhrases.any { arabicText.contains(it) } -> MaterialTheme.appTypography.arabicTitle
        arabicText.length > 50 -> MaterialTheme.appTypography.arabicTitle
        else -> MaterialTheme.appTypography.arabicTitle
    }
}

/**
 * Determines text color based on content type and subtype
 */
@Composable
private fun getContentColor(isHomeSunnah: Boolean): Color {
    return if(isHomeSunnah){
        MaterialTheme.colorScheme.onTertiary
    } else {
        MaterialTheme.colorScheme.tertiary
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
    modifier: Modifier = Modifier
) {
    if (references.isNotEmpty()) {

        val screenSize = LocalScreenSize.current
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
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = modifier.padding(top = topPadding),
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                references.forEach { reference ->
                    Text(
                        text = "• ${reference.source}",
                        style = MaterialTheme.appTypography.tabs.copy(
                            fontStyle = FontStyle.Italic,
                            fontFamily = TypographyConfig.amiri,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.tertiary,
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
    modifier: Modifier = Modifier
) {
    if (extraContent.isNotEmpty()) {
        val screenSize = LocalScreenSize.current
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
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DynamicExtraContentSection(
    extraContent: ExtraContent,
    modifier: Modifier = Modifier
) {
    val screenSize = LocalScreenSize.current
    val cornerRadius = when (screenSize) {
        ScreenSize.COMPACT -> 12.dp
        ScreenSize.MEDIUM -> 16.dp
        ScreenSize.EXPANDED -> 18.dp
    }
    val containerPadding = when (screenSize) {
        ScreenSize.COMPACT -> 12.dp
        ScreenSize.MEDIUM -> 16.dp
        ScreenSize.EXPANDED -> 16.dp
    }

    val metaInfo = getExtraContentMetaInfo(extraContent.type)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = metaInfo.colors.iconBackground.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.dp, metaInfo.colors.borderColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(containerPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                ECIconBox(
                    iconColor = metaInfo.colors.iconColor,
                    iconRes = metaInfo.icon,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = extraContent.type.name.replace("_", " ").titleCase(),
                    style = MaterialTheme.appTypography.extraAndNotificationTitle.copy(fontWeight = FontWeight.Medium),
                    color = metaInfo.colors.iconColor
                )
            }
            DynamicContentBlockRendererV2(
                contentBlocks = extraContent.content,
                modifier = modifier
            )
        }

    }
}

@Composable
fun ECIconBox(
    iconColor: Color,
    @DrawableRes iconRes: Int,
) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier.size(20.dp)
    )
}

private fun String.titleCase(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
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
            subtype = EnglishSubtype.NORMAL.name,
            content = "The beloved Prophet "
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.HONORIFICS.name,
            content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = " has said, \"On Judgement Day, there will be no other shade except for the shade of Arsh of Allah Almighty.\n\n" + "Three people will be under the shade of Arsh of Allah Almighty.\""
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = "It was humbly asked, ‘Ya Rasoolallah "
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.HONORIFICS.name,
            content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = " ! Who will be those people?’ He "
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.HONORIFICS.name,
            content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = " replied:\n\n" +
                    "1. The person who removes the worry of my Ummah.\n" +
                    "2. The one who revives my Sunnah.\n" +
                    "3. The one who recites salat upon me abundantly."
        ),
    )

    val pureArabicBlocks = listOf(
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.SUPPLICATION.name,
            content = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.HONORIFICS.name,
            content = "صَلَّى اللهُ عَلَيْهِ وَسَلَّم"
        ),
        ContentBlock(
            type = ContentType.ARABIC_TEXT,
            subtype = ArabicSubtype.OTHER.name,
            content = "اللَّهُمَّ بَارِكْ لَنَا فِيمَا رَزَقْتَنَا"
        )
    )

    val englishBlocks = listOf(
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.NORMAL.name,
            content = "It is recommended to recite Bismillah before starting any good deed. This practice brings blessings and protection from Allah."
        ),
        ContentBlock(
            type = ContentType.ENGLISH_TEXT,
            subtype = EnglishSubtype.TRANSLATION.name,
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
                    subtype = EnglishSubtype.NORMAL.name,
                    content = "This hadith teaches us the importance of being a righteous neighbor and how our good deeds can benefit the entire community."
                )
            )
        ),
        ExtraContent(
            type = ExtraContentType.WARNING,
            content = listOf(
                ContentBlock(
                    type = ContentType.ENGLISH_TEXT,
                    subtype = EnglishSubtype.NORMAL.name,
                    content = "Remember that righteousness should be consistent in all aspects of life, not just in public display."
                )
            )
        )
    )

}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun DynamicContentPreviewWrapper(
    contentBlocks: List<ContentBlock>,
    references: List<Reference> = emptyList(),
    extraContent: List<ExtraContent> = emptyList()
) {
    SunnahAlHadiTheme(
        windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            DynamicContentBlockRendererV2(
                contentBlocks = contentBlocks,
            )

            Spacer(modifier = Modifier.height(24.dp))

            DynamicExtraContentRenderer(
                extraContent = extraContent,
            )

            Spacer(modifier = Modifier.height(24.dp))

            DynamicReferenceRenderer(
                references = references,
            )
        }
    }
}

@Preview(name = "Mixed Content - Compact", widthDp = 400)
@Composable
fun PreviewMixedCompact() {
    DynamicContentPreviewWrapper(
        contentBlocks = PreviewData.mixedContentBlocks,
        references = PreviewData.sampleReferences,
        extraContent = PreviewData.sampleExtraContent
    )
}

@Preview(name = "Mixed Content - Medium", widthDp = 600)
@Composable
fun PreviewMixedMedium() {
    DynamicContentPreviewWrapper(
        contentBlocks = PreviewData.mixedContentBlocks,
        references = PreviewData.sampleReferences,
        extraContent = PreviewData.sampleExtraContent
    )
}

@Preview(name = "Mixed Content - Expanded", widthDp = 840)
@Composable
fun PreviewMixedExpanded() {
    DynamicContentPreviewWrapper(
        contentBlocks = PreviewData.mixedContentBlocks,
        references = PreviewData.sampleReferences,
        extraContent = PreviewData.sampleExtraContent
    )
}

@Preview(name = "Pure Arabic - Medium", widthDp = 600)
@Composable
fun PreviewArabicMedium() {
    DynamicContentPreviewWrapper(
        contentBlocks = PreviewData.pureArabicBlocks
    )
}

@Preview(name = "English Content - Compact", widthDp = 400)
@Composable
fun PreviewEnglishCompact() {
    DynamicContentPreviewWrapper(
        contentBlocks = PreviewData.englishBlocks
    )
}

