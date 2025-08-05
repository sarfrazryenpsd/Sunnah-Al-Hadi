package com.ryen.sunnah_alhadi.presentation.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ryen.sunnah_alhadi.domain.model.ArabicSubtype
import com.ryen.sunnah_alhadi.domain.model.ContentBlock
import com.ryen.sunnah_alhadi.domain.model.ContentType
import com.ryen.sunnah_alhadi.domain.model.EnglishSubtype
import com.ryen.sunnah_alhadi.domain.model.ExtraContent
import com.ryen.sunnah_alhadi.domain.model.ExtraContentType
import com.ryen.sunnah_alhadi.domain.model.Reference
import com.ryen.sunnah_alhadi.domain.model.Sunnah
import com.ryen.sunnah_alhadi.presentation.util.DynamicContentBlockRenderer
import com.ryen.sunnah_alhadi.ui.theme.LocalScreenSize
import com.ryen.sunnah_alhadi.ui.theme.ScreenSize
import com.ryen.sunnah_alhadi.ui.theme.SunnahAlHadiTheme

@Composable
fun SotdCard(
    sunnah: Sunnah,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.tertiary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Sunnah Title
            Text(
                text = sunnah.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(12.dp))
            // Sunnah Content Preview (first few lines)
            DynamicContentBlockRenderer(
                contentBlocks = sunnah.body, // Show only first 2 blocks
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun SotdPrev() {
    CompositionLocalProvider(
        LocalScreenSize provides ScreenSize.EXPANDED // or whatever default makes sense
    ){
        SunnahAlHadiTheme(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
        ) {
            SotdCard(
                sunnah = Sunnah(
                    id = "",
                    categoryId = 5,
                    title = "Title",
                    body = listOf(
                        ContentBlock(
                            type = ContentType.ENGLISH_TEXT,
                            subtype = EnglishSubtype.NORMAL.name,
                            content = "The beloved Prophet صَلَّى اللهُ عَلَيْهِ وَسَلَّم said: \"By virtue of the righteous Muslim, Allah Almighty removes a calamity from 100 houses in his neighborhood.\""
                        ),
                        ContentBlock(
                            type = ContentType.ENGLISH_TEXT,
                            subtype = EnglishSubtype.NORMAL.name,
                            content = "Then he صَلَّى اللهُ عَلَيْهِ وَسَلَّم recited:"
                        ),
                        ContentBlock(
                            type = ContentType.ARABIC_TEXT,
                            subtype = ArabicSubtype.VERSE.name,
                            content = "وَلَوْلَا دَفْعُ اللَّهِ النَّاسَ بَعْضَهُم بِبَعْضٍۢ لَّفَسَدَتِ الْأَرْضُ"
                        ),
                        ContentBlock(
                            type = ContentType.ENGLISH_TEXT,
                            subtype = EnglishSubtype.TRANSLATION.name,
                            content = "\"And if Allah does not keep away some people by means of others, the earth would have been corrupted.\""
                        )
                    ),
                    references = listOf(
                        Reference("Kanz-ul-Iman, Surah Al-Baqarah, verse 251"),
                        Reference("Majma' Al-Zawa`id, vol. 8, p. 299, Hadith 13533")
                    ),
                    extra = listOf(
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
                )
            )
        }
    }
}