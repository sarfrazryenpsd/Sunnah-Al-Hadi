package com.ryen.sunnah_alhadi.presentation.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

// Sample data for preview
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
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { /* Preview only */ }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }

                    IconButton(onClick = { /* Preview only */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Main content
            ContentBlockRenderer(
                contentBlocks = sunnah.body,
                modifier = Modifier.fillMaxWidth()
            )

            // References
            sunnah.references?.let { refs ->
                ReferenceRenderer(
                    references = refs,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Extra content
            sunnah.extra?.let { extras ->
                ExtraContentRenderer(
                    extraContent = extras,
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